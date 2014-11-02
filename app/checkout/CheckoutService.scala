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

package checkout

import common.files.FlacFileLocation
import common.message.MessageService

import scala.util.Try

/**
 * The main trait for checking out files from the FLAC repository into the staging repository.
 * Created by alex on 02/11/14.
 */
trait CheckoutService {

  /**
   * Checkout a list of FLAC files.
   * @param flacFileLocations
   * @return
   */
  def checkout(flacFileLocations: Traversable[FlacFileLocation])(implicit messageService: MessageService): Try[Unit]
}
