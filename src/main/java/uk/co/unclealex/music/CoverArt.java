/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package uk.co.unclealex.music;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An immutable class that contains information about a cover art picture, namely the image data and its mime type.
 * @author alex
 *
 */
public class CoverArt extends DataObject {

  /**
   * The binary image data for the cover art.
   */
  private final byte[] imageData;
  
  /**
   * The mime type for the cover art.
   */
  private final String mimeType;
  
  
  /**
   * Instantiates a new cover art.
   *
   * @param imageData the image data
   * @param mimeType the mime type
   */
  @JsonCreator
  public CoverArt(@JsonProperty("imageData") byte[] imageData, @JsonProperty("mimeType") String mimeType) {
    super();
    this.imageData = imageData;
    this.mimeType = mimeType;
  }

  /**
   * Gets the binary image data for the cover art.
   *
   * @return the binary image data for the cover art
   */
  @NotEmpty
  public byte[] getImageData() {
    return imageData;
  }

  /**
   * Gets the mime type for the cover art.
   *
   * @return the mime type for the cover art
   */
  @NotEmpty
  public String getMimeType() {
    return mimeType;
  }
}
