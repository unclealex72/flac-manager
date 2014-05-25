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

package uk.co.unclealex.music.sync.scsi;

import uk.co.unclealex.music.JDataObject;

/**
 * An immutable bean to represent a unique SCSI ID of a device.
 * 
 * @author alex
 * 
 */
public class JScsiId extends JDataObject {

  /**
   * The HBA number of the device.
   */
  private final int hba;

  /**
   * The channel on the HBA.
   */
  private final int channel;

  /**
   * The target ID.
   */
  private final int targetId;

  /**
   * The LUN.
   */
  private final int lun;

  /**
   * Instantiates a new scsi id.
   * 
   * @param hba
   *          the hba
   * @param channel
   *          the channel
   * @param targetId
   *          the target id
   * @param lun
   *          the lun
   */
  public JScsiId(final int hba, final int channel, final int targetId, final int lun) {
    super();
    this.hba = hba;
    this.channel = channel;
    this.targetId = targetId;
    this.lun = lun;
  }

  /**
   * Represent this SCSI ID as a string in the format of <code>[h:c:t:l]</code>.
   * 
   * @return A string in the format of <code>[h:c:t:l]</code>.
   */
  @Override
  public String toString() {
    return String.format("[%d:%d:%d:%d]", getHba(), getChannel(), getTargetId(), getLun());
  }

  /**
   * Gets the HBA number of the device.
   * 
   * @return the HBA number of the device
   */
  public int getHba() {
    return hba;
  }

  /**
   * Gets the channel on the HBA.
   * 
   * @return the channel on the HBA
   */
  public int getChannel() {
    return channel;
  }

  /**
   * Gets the target ID.
   * 
   * @return the target ID
   */
  public int getTargetId() {
    return targetId;
  }

  /**
   * Gets the LUN.
   * 
   * @return the LUN
   */
  public int getLun() {
    return lun;
  }
}
