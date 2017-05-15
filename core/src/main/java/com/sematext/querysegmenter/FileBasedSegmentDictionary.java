/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class FileBasedSegmentDictionary extends AbstractSegmentDictionary {

  private static final String DEFAULT_SEPARATOR = ",";

  protected String separator = DEFAULT_SEPARATOR;

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  /**
   * Load a dictionary from a file.
   * 
   * @param filename
   *          file to load the dictionary from
   */
  public void load(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() == 0) {
          continue;
        }
        addLineFromFile(line);
      }
      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  abstract protected void addLineFromFile(String line);

}
