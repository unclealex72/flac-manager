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

package uk.co.unclealex.music.command.checkin.covers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import uk.co.unclealex.music.CoverArt;
import uk.co.unclealex.music.MusicFile;

/**
 * The default implementation of {@link ArtworkService}.
 * @author alex
 *
 */
public class ArtworkServiceImpl implements ArtworkService {

  /**
   * {@inheritDoc}
   */
  @Override
  public void addArwork(MusicFile musicFile, URI coverArtUri) throws IOException {
    Path coverArtFile = Files.createTempFile("cover-art-", ".art");
    try {
      try (InputStream in = coverArtUri.toURL().openStream()) {
        Files.copy(in, coverArtFile, StandardCopyOption.REPLACE_EXISTING);
      }
      String contentType = Files.probeContentType(coverArtFile);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Files.copy(coverArtFile, out);
      CoverArt coverArt = new CoverArt(out.toByteArray(), contentType);
      musicFile.setCoverArt(coverArt);
      musicFile.commit();
    }
    finally {
      Files.delete(coverArtFile);
    }
  }
}
