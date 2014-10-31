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

package uk.co.unclealex.music.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alex
 *
 */
public class ProtectionServiceImplTest {

  Path testDirectory;

  @Before
  public void createTestDirectory() throws IOException {
    testDirectory = Files.createTempDirectory("protection-service-impl-test-");
  }

  @Test
  public void testProtectReadOnly() throws IOException {
    runTest(new ProtectCallback(), true, false);
  }

  @Test
  public void testProtectWritable() throws IOException {
    runTest(new ProtectCallback(), false, true);
  }

  @Test
  public void testUnprotectReadOnly() throws IOException {
    runTest(new UnprotectCallback(), true, true);
  }

  @Test
  public void testUnprotectWritable() throws IOException {
    runTest(new UnprotectCallback(), false, true);
  }

  protected void runTest(ProtectionServiceCallback callback, boolean readOnly, boolean expectWritable) throws IOException {
    FileLocation newFile = new FileLocation(testDirectory, Paths.get("a", "good", "time.txt"), readOnly);
    Files.createDirectories(newFile.resolve().getParent());
    Files.createFile(newFile.resolve());
    callback.execute(new ProtectionServiceImpl(), newFile);
    for (Path path : new Path[] {
        testDirectory,
        testDirectory.resolve(Paths.get("a")),
        testDirectory.resolve(Paths.get("a", "good")),
        testDirectory.resolve(Paths.get("a", "good", "time.txt")) }) {
      Assert.assertEquals("Path " + path + " had the wrong writable attribute.", expectWritable, path.toFile().canWrite());
    }
    
  }
  
  interface ProtectionServiceCallback {
    public void execute(ProtectionService protectionService, FileLocation fileLocation) throws IOException;
  }
  
  class ProtectCallback implements ProtectionServiceCallback {
    
    @Override
    public void execute(ProtectionService protectionService, FileLocation fileLocation) throws IOException {
      protectionService.protect(fileLocation);
    }
  }
  
  class UnprotectCallback implements ProtectionServiceCallback {
    
    @Override
    public void execute(ProtectionService protectionService, FileLocation fileLocation) throws IOException {
      protectionService.unprotect(fileLocation);
    }
  }

  @After
  public void removeTestDirectory() throws IOException {
    if (testDirectory != null) {
      removeRecurisvely(testDirectory.toFile());
    }
  }

  protected void removeRecurisvely(File f) throws IOException {
    f.setWritable(true);
    if (f.isDirectory()) {
      for (File child : f.listFiles()) {
        removeRecurisvely(child);
      }
    }
    f.delete();
  }

}
