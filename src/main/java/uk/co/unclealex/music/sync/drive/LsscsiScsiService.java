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

package uk.co.unclealex.music.sync.drive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import uk.co.unclealex.music.sync.scsi.ScsiId;
import uk.co.unclealex.music.sync.scsi.ScsiIdFactory;
import uk.co.unclealex.process.ListingProcessCallback;
import uk.co.unclealex.process.builder.ProcessRequestBuilder;
import uk.co.unclealex.process.packages.PackagesRequired;

import com.google.common.collect.BiMap;

/**
 * An implementation of {@link ScsiService} that uses the Linux command
 * <code>lsscsi</code> to interrogate connected SCSI devices.
 * 
 * @author alex
 * 
 */
@PackagesRequired("lsscsi")
public class LsscsiScsiService extends AbstractStringCellMappingService<ScsiId, Path> implements ScsiService {

  /**
   * The {@link ProcessRequestBuilder} used to run the <code>lsscsi</code>
   * command.
   */
  private final ProcessRequestBuilder processRequestBuilder;

  /**
   * The {@link ScsiIdFactory} used to parse SCSI identifiers.
   */
  private final ScsiIdFactory scsiIdFactory;

  /**
   * Instantiates a new lsscsi scsi service.
   * 
   * @param keyColumn
   *          the key column
   * @param valueColumn
   *          the value column
   * @param processRequestBuilder
   *          the process request builder
   * @param scsiIdFactory
   *          the scsi id factory
   */
  @Inject
  public LsscsiScsiService(final ProcessRequestBuilder processRequestBuilder, final ScsiIdFactory scsiIdFactory) {
    super(0, -1);
    this.processRequestBuilder = processRequestBuilder;
    this.scsiIdFactory = scsiIdFactory;
  }

  /**
   * Initialise.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @PostConstruct
  public void initialise() throws IOException {
    generateMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BiMap<ScsiId, Path> listDevicePathsByScsiIds() {
    return getMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ScsiId parseKey(final String key) {
    return getScsiIdFactory().create(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path parseValue(final String value) {
    return Paths.get(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> generateLines() throws IOException {
    final ListingProcessCallback callback = new ListingProcessCallback();
    getProcessRequestBuilder().forCommand("lsscsi").withCallbacks(callback).executeAndWait();
    return callback.getOutputLines();
  }

  /**
   * Gets the {@link ProcessRequestBuilder} used to run the <code>lsscsi</code>
   * command.
   * 
   * @return the {@link ProcessRequestBuilder} used to run the
   *         <code>lsscsi</code> command
   */
  public ProcessRequestBuilder getProcessRequestBuilder() {
    return processRequestBuilder;
  }

  /**
   * Gets the {@link ScsiIdFactory} used to parse SCSI identifiers.
   * 
   * @return the {@link ScsiIdFactory} used to parse SCSI identifiers
   */
  public ScsiIdFactory getScsiIdFactory() {
    return scsiIdFactory;
  }

}
