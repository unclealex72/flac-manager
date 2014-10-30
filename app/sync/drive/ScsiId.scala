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

package sync.drive

;

/**
 * An immutable bean to represent a unique SCSI ID of a device.
 *
 * @author alex
 *
 */
case class ScsiId(
                   /**
                    * The HBA number of the device.
                    */
                   hba: Int,

                   /**
                    * The channel on the HBA.
                    */
                   channel: Int,

                   /**
                    * The target ID.
                    */
                   targetId: Int,

                   /**
                    * The LUN.
                    */
                   lun: Int) {

  /**
   * Represent this SCSI ID as a string in the format of <code>[h:c:t:l]</code>.
   *
   * @return A string in the format of <code>[h:c:t:l]</code>.
   */
  override def toString = s"[$hba:$channel:$targetId:$lun]"
}

object ScsiId {

  /**
   * The {@link java.util.regex.Pattern} used to parse <code>[h:c:t:l]</code> strings.
   */
  private val HCTL = """\[([0-9]+):([0-9]+):([0-9]+):([0-9]+)\]""".r

  def apply(hctl: String): ScsiId = hctl match {
    case HCTL(hba, channel, targetId, lun) => apply(hba.toInt, channel.toInt, targetId.toInt, lun.toInt)
    case _ => throw new IllegalArgumentException(String.format("String '%s' is not in [h:c:t:l] format.", hctl))
  }
}