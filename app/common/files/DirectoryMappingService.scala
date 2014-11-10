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

package common.files

import java.nio.file.Path

/**
 * The trait for services that can resolve NFS mounts on client services
 * Created by alex on 06/11/14.
 */
trait DirectoryMappingService {

  /**
   * Resolve a set of client side directories into a map of server side directories.
   * @param mtab The contents of the client's /etc/mtab file.
   * @param directories
   * @return A map of the original directories to the resolved directories
   */
  def withMtab(mtab: String): String => Path
}