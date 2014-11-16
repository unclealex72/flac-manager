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

package own

import common.configuration.User
import common.files.StagedFlacFileLocation
import common.message.MessageService

/**
 * Created by alex on 09/11/14.
 */
trait OwnCommand {
  def changeOwnership(action: OwnAction, users: Seq[User], locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): Unit
}

sealed trait OwnAction

object Own extends OwnAction

object Unown extends OwnAction