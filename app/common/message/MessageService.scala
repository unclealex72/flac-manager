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
 * http://www.apache.org/licenses/LICENSE-2.0
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

package common.message

import common.files.FileLocation

/**
 * An interface for classes that can print internationalised messages to users.
 *
 * @author alex
 *
 */
trait MessageService {


  /**
   * Print an internationalised message.
   *
   * @param template
   * The template key used to select the message.
   */
  def printMessage(template: MessageType): Unit
}

sealed abstract class MessageType(val key: String, val parameters: Any*)

/**
 * The key for producing an encoding message.
 */
case class ENCODE(fileLocation: FileLocation) extends MessageType("encode", Seq(fileLocation))

/**
 * The key for producing a delete message.
 */
case class DELETE(fileLocation: FileLocation) extends MessageType("delete", Seq(fileLocation))

/**
 * The key for producing a move message.
 */
object MOVE extends MessageType("move")

/**
 * The key for producing a not flac file message.
 */
object NOT_FLAC extends MessageType("notFlac")

/**
 * The key for producing a missing artwork message.
 */
object MISSING_ARTWORK extends MessageType("missingArtwork")

/**
 * The key for producing an overwrite message.
 */
object OVERWRITE extends MessageType("overwrite")

/**
 * The key for producing non unique messages.
 */
object NON_UNIQUE extends MessageType("nonUnique")

/**
 * The key for producing not owned messages.
 */
object NOT_OWNED extends MessageType("notOwned")

/**
 * The key for producing not owned messages.
 */
object NO_OWNER_INFORMATION extends MessageType("noOwner")

/**
 * The key for producing link messages.
 */
object LINK extends MessageType("link")

/**
 * The key for producing link messages.
 */
object UNLINK extends MessageType("unlink")

/**
 * The key for producing unknown user messages.
 */
object UNKNOWN_USER extends MessageType("unknownUser")

/**
 * The key for producing add owner messages.
 */
object ADD_OWNER extends MessageType("addOwner")

/**
 * The key for producing remove owner messages.
 */
object REMOVE_OWNER extends MessageType("removeOwner")

/**
 * The key for producing commit ownership changes messages.
 */
object COMMIT_OWNERSHIP extends MessageType("commitOwnership")

/**
 * The key for producing a message to say that a file is being kept on a device.
 */
object SYNC_KEEP extends MessageType("syncKeep")

/**
 * The key for producing a message to say that a file is being removed from a device.
 */
object SYNC_REMOVE extends MessageType("syncRemove")

/**
 * The key for producing a message to say that a file is being added to a device.
 */
object SYNC_ADD extends MessageType("syncAdd")

/**
 * The key for producing a message to say that a file has been found.
 */
case class FOUND_FILE(fileLocation: FileLocation) extends MessageType("foundFile")

/**
 * The key for producing a message to say that a valid track has been found.
 */
object FOUND_TRACK extends MessageType("foundTrack")

/**
 * The key for producing a message to say that a device is being synchronised.
 */
object SYNCHRONISING extends MessageType("sync")

/**
 * The key for producing a message to say that a device has been found.
 */
object FOUND_DEVICE extends MessageType("foundDevice")

/**
 * The key for producing a message to say that a device has been synchronised.
 */
object DEVICE_SYNCHRONISED extends MessageType("deviceSynchronised")
