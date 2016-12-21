[![Build Status](https://travis-ci.org/sematext/query-segmenter.svg?branch=master)](https://travis-ci.org/sematext/query-segmenter)

# Query Segmenter

The `QuerySegmenter` core library is used to find typed segments within a user query. For example, for the query *“Pizza New York”*, the segment *“New York”* can be extracted as a segment of type *“city”*. The typed segments are matched against a dictionary, which is usually a text file.

## Contents

- [Solr Version](#solr-version)
- [Build](#build)
- [Release Notes](#release-notes)
- [Core Library](#core-library)
  - [API](#api)
  - [Segment Dictionary](#segment-dictionary)
- [Solr Integration](#solr-integration)
  - [Deployment Library Files](#deployment-library-files)
  - [QuerySegmenterQParser](#querysegmenterqparser)
  - [QuerySegmenterComponent](#querysegmentercomponent)
  - [CentroidComponent](#centroidcomponent)
- [License](#license)
- [Contact](#contact)
  
## Solr Version
6.3.0

## Build
You need maven and JDK 8:

```
$ mvn clean package
```

## Release Notes

#### 1.3.6.3.0 (2016-12-09)
- Support for Solr 6.3.0

#### 1.3.6.0.1 (2016-06-27)
- Support for Solr 6.0.1

## Core Library
### API
The main interface is `QuerySegmenter` which contains this method:

```java
List<TypedSegment> segment(String query);
```

The user query is passed to this method and a list of typed segments found within the query is returned. Note that multiple typed segments can be returned. For example, if the query is *“car park slope new york”*, the method call could return *“park slope”* as a segment of type neighborhood and *“new york”* as a city segment. Also, for the query *“pizza new york”*, the method call could return 2 typed segments for *“new york”*, one as a city and another one as a state. Or it could return the same type for both, but for different matches in the dictionary. For example, the query *“pizza menlo park”* could return 2 segments of type neighborhood, one for *Menlo Park, CA* and another one for *Menlo Park, NJ*. The decision about how many typed segments will be returned by type is placed upon each type dictionary.

A `TypeSegment` object has a `getMetadata` method that returns the metadata of the typed segment as stored in the dictionary. For example, for a location type segment, the metadata could be the latitude and longitude of a rectangle that encompasses the location.

The class `QuerySegmenterDefaultImpl` is responsible for splitting the user query into multiple segments and asking each dictionary if they have a match for each segment. This default implementation always parses the user query from left to right, and tries the longest segment possible first (the window size is set to 4 in this class). For example, for the query *“fast pizza delivery new york”*, the segmenter will generate these segments in order:

1. fast pizza delivery new
2. fast pizza delivery
3. fast pizza
4. fast
5. pizza delivery new york
6. pizza delivery new
7. pizza delivery
8. pizza
9. delivery new york
10. delivery new
11. delivery
12. new york
13. new
14. york

Each of these segments will be looked up in each dictionary for matches.

### Segment Dictionary

#### Generic Segment

A dictionary holds a flat list of words. A case-insensitive lookup is made to retrieve a word from the list. It can be used to look up whether a word is part of a dictionary and act upon that knowledge. For example, it could be used to prefix a word in a query if it is found in a dictionary.

#### Synonym Segment

Dictionary used to list synonyms of a label. If we have this entry in the dictionary:

```
New York,nyc,Big Apple
```

Then *"New York"* will be returned when *"nyc"* is looked up in this dictionary.

The first element of the line is the label that will be returned and all other elements on the same line are synonyms. If a lookup is done on the first element, that element is returned. For example, using the dictionary described above, a lookup for *'new york'* will return *"New York"*.
We can also use a plain list of words without any synonyms. In that case, this dictionary will behave exactly like the Generic Segment Dictionary.

#### Area Segment

An area segment is a location segment that represents a rectangular geographical area. An area segment has a minimum and maximum latitude and a minimum and a maximum longitude. The dictionary implementation is the class `AreaSegmentDictionaryMemImpl` and this returns `AreaTypedSegment` object.

Here is an example of a file that is read by the area dictionary (it represents neighborhood data of Anchorage, AK):

```
Northeast,61.235009,-149.703891,61.195252,-149.778423
Old Seward-Oceanview,61.116429,-149.786808,61.040014,-149.899467
Portage Valley,60.906335,-148.740705,60.733033,-149.051696
Glen Alps,61.108623,-149.686223,61.083627,-149.714678
Campbell Park,61.180852,-149.762532,61.166392,-149.860504
Eagle River Valley,61.353984,-149.254768,61.245675,-149.550315
Spenard,61.198908,-149.897944,61.172364,-149.999577
Bear Valley,61.096898,-149.686291,61.045506,-149.7537
Girdwood,61.036304,-148.938593,60.910508,-149.181943
Taku-Campbell,61.173793,-149.855377,61.137486,-149.916868
```

The first field is the label of the segment. This label will be used to match a segment in the user query. The other fields are the metadata of each area. For area, the other fields are minlat, minlon, maxlat and maxlon.

Note that this dictionary does a case-insensitive match. If the user query contains *“northeast”*, it will still match the *“Northeast”* label defined in the dictionary.

#### Centroid Segment

A centroid segment is a location defined by a center location (latitude and longitude). Here is a centroid file (it represents the center location of some US cities) used by the `CentroidSegmentDictionaryMemImpl` class:

```
Aaronsburg|40.9068|-77.4081
Abbeville|31.5865|-85.2161
Abbeville|31.9936|-83.3126
Abbeville|29.9590062298|-92.1441373789
Abbeville|34.4807|-89.489
Abbeville|34.1937|-82.4136
Abbot|45.1998|-69.4683
Abbotsford|44.9529|-90.3145
Abbott|31.8895|-97.0807
```

The `CentroidSegmentDictionaryMemImpl` dictionary returns `CentroidTypedSegment`.

Note that this dictionary does a case-insensitive match. If the user query contains *“aaronsburg”*, it will still match the *“Aaronsburg”* label defined in the dictionary.

## Solr Integration
The `QuerySegmenter` Solr library includes Solr components that use the `QueryComponent` core library. It currently contains 2 components: `QuerySegmenterQParser` and `CentroidComponent`.

### Deployment Library Files. 
Copy the `QuerySegmenter` Solr library jar files (`st-QuerySegmenter-core-x.y.z.jar` and `st-QuerySegmenter-solr-x.y.z.jar`) into the `lib` folder of your Solr core (as defined in `solr.xml` file).

### QuerySegmenterQParser
This `QParser` is used to retrieve segments from a user query. Any dictionary can be used.

If there is a segment in the user query that matches an element of the dictionary, the query is rewritten using either the label or the location (only for the area segment dictionary). For example, for the query *“pizza brooklyn”*, if *“new  york”* is an area, the query will be rewritten to *“pizza neighborhood:brooklyn”* or *“pizza location:[minlat,minlon TO maxlat, maxlon]”*. The field to use and whether we should use the label or the location is configurable.

#### Configuration
The `QuerySegmenterQParser` needs to be configured in the `solrconfig.xml` file. Here is an example:

```xml
<queryParser name="seg"
  class="com.sematext.querysegmenter.solr.QuerySegmenterQParserPlugin">
  <lst name="segments">
    <lst name="neighborhood">
      <str name="field">location</str>
      <str name="dictionary">com.sematext.querysegmenter.geolocation.AreaSegmentDictionaryMemImpl</str>
      <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/neighborhood.txt</str>
      <bool name="useLatLon">true</bool>
    </lst>
    <lst name="authors">
  <str name="field">author</str>
  <str name="dictionary">com.sematext.querysegmenter.GenericSegmentDictionaryMemImpl</str>
  <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/authors.txt</str>
  <bool name="useLatLon">false</bool>
    </lst>
  </lst>
</queryParser>
```

This will configure the `QuerySegmenterQParser` to use an area segment dictionary and a generic segment dictionary. The first dictionary will load the `neighborhood.txt` file, while the other will load the `authors.txt` file. If a match is found in a dictionary, the query will be rewritten using the field defined and, in the case of an area, will use the latitude and longitude of the area instead of the label.

It is also possible to use the `QParser` within a request handler. Here is an example:

```xml
<requestHandler name="/segmenter" class="solr.SearchHandler">
  <lst name="defaults">
    <str name="echoParams">explicit</str>
    <str name="q">{!seg defType=edismax v=$qq}</str>
    <str name="qf">body^2.0 id</str>
  </lst>
</requestHandler>
```

#### Usage
To use the `QParser` directly, use LocalParams syntax:

```
http://localhost:8080/solr/company/select/?q={!seg}pizza+new+york
```

It is also possible to define another `QParser` to be used for the rest of the query:

```
http://localhost:8080/solr/test/select/?q={!seg+defType=edismax}pizza+new+york
```

In the above example, the Query Segmenter would first find the *“new york”* typed segment and rewrite the query to pizza *city:”new york”*, and then this rewritten query would be handled by the `eDismax` parser which would use just the *“pizza”* part with fields defined in its `qf`. The *city:”new york”* portion of the query would not be used with `qf` because of the field-specific prefix.

To use with the request handler defined in the previous section, use parameter dereferencing:

```
http://localhost:8080/solr/company/segmenter/?qq=pizza+new+york
```

### QuerySegmenterComponent

A component that works like the `QParser` described above, but implemented as a Solr `SearchComponent` instead of a `QParser`. A SearchComponent must be used with a Solr `RequestHandler`. This specific component must be used before the standard query component (or simply defined to be the first component), because it needs to rewrite the query before the query is made against Solr. 

#### Configuration
Here is an example configuration (in `solrconfig.xml`):

```xml
<searchComponent name="segmenter"
  class="com.sematext.querysegmenter.solr.QuerySegmenterComponent">   
  <lst name="segments">
    <lst name="authors">
      <str name="field">author</str>
      <str name="dictionary">com.sematext.querysegmenter.GenericSegmentDictionaryMemImpl</str>
      <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/authors.txt</str>
      <bool name="useLatLon">false</bool>
    </lst>
    <lst name="types">
      <str name="field">type</str>
      <str name="dictionary">com.sematext.querysegmenter.GenericSegmentDictionaryMemImpl</str>
      <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/types.txt</str>
      <bool name="useLatLon">false</bool>
    </lst>
    <lst name="projects">
      <str name="field">project</str>
      <str name="dictionary">com.sematext.querysegmenter.GenericSegmentDictionaryMemImpl</str>
      <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/projects.txt</str>
      <bool name="useLatLon">false</bool>
    </lst>
  </lst>
</searchComponent>
  
<requestHandler name="/qs" class="solr.SearchHandler">
  <lst name="defaults">
    <str name="defType">edismax</str>
    <!-- Other dismax params... -->
  </lst>
  <arr name="first-components">
    <str>segmenter</str>
  </arr>
</requestHandler>
```

#### Usage
It can be used like this:

```
http://localhost:8080/solr/test/qs?q=solr
```

For example, if *“solr”* is in the dictionary of projects (i.e. in the `projects.txt` file), the query will be rewritten to *“project:solr”*.


### CentroidComponent

This `SearchComponent` is used to alter the user location if a segment of the query is a centroid. It must be used within a `RequestHandler` that uses a location filter (`bbox` or `geofilt`). If a match is found, the user location (`pt` request param, which is required) is changed to the center location of the centroid. The effect will be that instead of using the user location for the location filter, it will use the centroid location. If multiple centroid segments are returned from the user query, the closest centroid to the original user location is used.

For example, if a user searches for *“pizza Aaronsburg”*, the segment *“Aaronsburg”* could be returned as a centroid with location 40.9068, -77.4081. This location would be used instead of the original location. This would filter the results to keep only the documents around the centroid location.

#### Configuration

First, we need to define the SearchComponent (in `solrconfig.xml`) :

```xml
<searchComponent name="centroidcomp"
   class="com.sematext.querysegmenter.solr.CentroidComponent">
  <str name="filename">${solr.solr.home}/${solr.core.name}/conf/segmenter/centroid.csv</str>
  <str name="separator">|</str>
</searchComponent>
```

The *“filename”* parameter allows to set the centroid dictionary file. The *“separator”* parameter is used to specify the separator in the dictionary file (default is comma). Only one dictionary can be used.

Next, we need to add this component to a request handler:

```xml
<requestHandler name="/centroid" class="solr.SearchHandler">
  <lst name="defaults">
    <str name="echoParams">explicit</str>
    <str name="sfield">location</str>
    <str name="fq">{!geofilt}</str>
    <str name="q.alt">*:*</str>
    <str name="d">75</str>
    </lst>
    <arr name="first-components">
      <str>centroidcomp</str>
    </arr>
</requestHandler>
```

It is important to use *“first-components”* to insert the centroid component in the request handler because it needs to alter the user location before other components of the request handler access it. 

Also note that `bbox` could have been used instead of `geofilt`.

Another thing to note is usage of *<str name="q.alt">*:*</str>* - with `CentroidComponent`, it is possible original user’s query will be transformed into empty string. To handle such cases, you should define `q.alt` which will be used by Solr instead. In this case, we used match-all query (which is typically used in similar cases).

#### Usage

To use it with the request handler defined in the last section:

```
http://localhost:8080/solr/company/centroid?q=Aberdeen+pizza&pt=44.5623,-73.7143
```

The `pt` parameter is the user location. But, if *Aberdeen* is found to be a centroid segment, the user location will be replaced by the precise centroid location.

## License

QuerySegmenter is released under Apache License, Version 2.0

## Contact

For any questions ping [@sematext](http://www.twitter.com/sematext)