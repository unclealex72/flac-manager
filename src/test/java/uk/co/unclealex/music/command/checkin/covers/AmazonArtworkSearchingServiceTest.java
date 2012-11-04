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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import uk.co.unclealex.music.command.checkin.covers.AmazonArtworkSearchingService;
import uk.co.unclealex.music.command.checkin.covers.SignedRequestsService;

import com.google.common.collect.Maps;

/**
 * @author alex
 *
 */
public class AmazonArtworkSearchingServiceTest {

  private Path artworkPath;

  @After
  public void tearDown() throws IOException {
    if (artworkPath != null) {
      Files.deleteIfExists(artworkPath);
    }
  }
  
  @Test
  public void testChristIllusion() throws IOException, URISyntaxException {
    runTest("B000G75AE8", new URI("http://ecx.images-amazon.com/images/I/51cTfd-Z95L.jpg"));
  }
  
  @Test
  public void testThinkingOfYou() throws IOException, URISyntaxException {
    runTest("B000ZHESD8", null);
  }

  
  public void runTest(String asin, URI expectedUri) throws IOException {
    artworkPath = Files.createTempFile("amazon-artwork-" + asin + "-", ".xml");
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(asin + ".xml")) {
      Files.copy(in, artworkPath, StandardCopyOption.REPLACE_EXISTING);
    }
    SignedRequestsService signedRequestsService = mock(SignedRequestsService.class);
    Map<String, String> parameters = Maps.newHashMap();
    parameters.put("Service", "AWSECommerceService");
    parameters.put("Operation", "ItemLookup");
    parameters.put("ItemId", asin);
    parameters.put("ResponseGroup", "Images");
    parameters.put("AssociateTag", "dummy");
    when(signedRequestsService.signedUrl(parameters)).thenReturn(artworkPath.toUri().toString());
    AmazonArtworkSearchingService amazonArtworkSearchingService = new AmazonArtworkSearchingService(signedRequestsService);
    URI actualUrl = amazonArtworkSearchingService.findArtwork(asin);
    Assert.assertEquals("The wrong URI was returned for ASIN " + asin, expectedUri, actualUrl);
  }
}
