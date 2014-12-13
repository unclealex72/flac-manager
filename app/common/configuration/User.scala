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

package common.configuration

import java.nio.file.Path

/**
 * A configuration item for a user. Users must also have a MusicBrainz login and own an iPod.
 *
 * @author alex
 *
 */
case class User(
                 /**
                  * Gets the user's name that is to be displayed with their devices and to be
                  * used when changing ownership.
                  *
                  * @return the user's name that is to be displayed with their devices and to
                  *         be used when changing ownership.
                  */
                 name: String,

                 /**
                  * Gets the MusicBrainz user name for this user.
                  *
                  * @return the MusicBrainz user name for this user
                  */
                 musicBrainzUserName: String,

                 /**
                  * Gets the MusicBrainz password for this user.
                  *
                  * @return the MusicBrainz password for this user
                  */
                 musicBrainzPassword: String,

                 /**
                  * Get the mount point of this user's iPod.
                  *
                  * @return The mount point of the user's iPod.
                  */
                 mountPoint: Path)