/*
 * Copyright 2014 Alex Jones
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
 */

package sync

import files.FileLocation

/**
 * A trait to find the last modified time for a file location. This is abstracted to a seperate trait for testing.
 * Created by alex on 29/10/14.
 */
trait LastModifiedFactory extends (FileLocation => Long) {

  /**
   * Get the last time a file location was modified.
   * @param fileLocation
   * @return
   */
  def lastModified(fileLocation: FileLocation): Long

  def apply(fileLocation: FileLocation): Long = lastModified(fileLocation)
}