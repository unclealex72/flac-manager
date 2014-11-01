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
package common.changes

import common.configuration.User
import common.joda.JodaDateTime
import org.joda.time.DateTime
import org.squeryl.KeyedEntity
import sync.DeviceFile

/**
 * A persistable unit that represents a change to a user's encoded repository.
 */
case class Change(
                   /**
                    * The primary key of the change.
                    */
                   val id: Long,

                   /**
                    * The relative path of the file that changed.
                    */
                   val relativePath: String,

                   /**
                    * The time the change occurred.
                    */
                   val at: DateTime,

                   /**
                    * The name of the user whose repository changed.
                    */
                   val user: String,

                   /**
                    * The action that happened to the file (added or removed).
                    */
                   val action: String) extends KeyedEntity[Long] {

  /**
   * Squeryl constructor
   */

  protected def this() = this(0, "", new DateTime(), "", "")
}

object Change {

  def added(deviceFile: DeviceFile, user: User): Change = apply(deviceFile.relativePath, JodaDateTime(deviceFile.lastModified), user, "added")

  def removed(relativePath: String, at: DateTime, user: User): Change = apply(relativePath, at, user, "removed")

  private def apply(relativePath: String, at: DateTime, user: User, action: String): Change = {
    Change(0, relativePath, at, user.name, action)
  }
}