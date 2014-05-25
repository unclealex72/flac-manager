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

package uk.co.unclealex.music.action;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import uk.co.unclealex.music.command.checkin.JEncodingService;
import uk.co.unclealex.music.command.checkin.covers.ArtworkService;
import uk.co.unclealex.music.configuration.JUser;
import uk.co.unclealex.music.devices.JDeviceService;
import uk.co.unclealex.music.files.JFileLocation;
import uk.co.unclealex.music.files.JFileUtils;
import uk.co.unclealex.music.message.JMessageService;
import uk.co.unclealex.music.musicbrainz.JChangeOwnershipService;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The default implementation of {@link JActionExecutor}.
 * 
 * @author alex
 * 
 */
public class JActionExecutorImpl extends JActionVisitor.Default implements JActionExecutor {

  /**
   * The {@link uk.co.unclealex.music.message.JMessageService} used to display messages to the user.
   */
  private final JMessageService messageService;

  /**
   * The {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files.
   */
  private final JFileUtils fileUtils;

  /**
   * The {@link ArtworkService} used to add cover art to a file.
   */
  private final ArtworkService artworkService;

  /**
   * The {@link uk.co.unclealex.music.command.checkin.JEncodingService} used to encode FLAC files to MP3 files.
   */
  private final JEncodingService encodingService;

  /**
   * The {@link uk.co.unclealex.music.devices.JDeviceService} that knows about devices.
   */
  private final JDeviceService deviceService;

  /**
   * The {@link uk.co.unclealex.music.musicbrainz.JChangeOwnershipService} used to track and commit changes to file
   * ownership.
   */
  private final JChangeOwnershipService changeOwnershipService;

  /**
   * Instantiates a new action executor impl.
   * 
   * @param messageService
   *          the message service
   * @param fileUtils
   *          the file utils
   * @param artworkService
   *          the artwork service
   * @param encodingService
   *          the encoding service
   * @param deviceService
   *          the device service
   * @param changeOwnershipService
   *          the change ownership service
   */
  @Inject
  public JActionExecutorImpl(
          JMessageService messageService,
          JFileUtils fileUtils,
          ArtworkService artworkService,
          JEncodingService encodingService,
          JDeviceService deviceService,
          JChangeOwnershipService changeOwnershipService) {
    super();
    this.messageService = messageService;
    this.fileUtils = fileUtils;
    this.artworkService = artworkService;
    this.encodingService = encodingService;
    this.deviceService = deviceService;
    this.changeOwnershipService = changeOwnershipService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(JAction action) throws IOException {
    action.accept(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JMoveAction moveAction) throws IOException {
    JFileLocation sourceFileLocation = moveAction.getFileLocation();
    JFileLocation targetFileLocation = moveAction.getTargetFileLocation();
    getMessageService().printMessage(JMessageService.MOVE, sourceFileLocation.resolve(), targetFileLocation.resolve());
    getFileUtils().move(sourceFileLocation, targetFileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JDeleteAction deleteAction) throws IOException {
    JFileLocation fileLocation = deleteAction.getFileLocation();
    getMessageService().printMessage(JMessageService.DELETE, fileLocation.resolve());
    getFileUtils().remove(fileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JEncodeAction encodeAction) throws IOException {
    JFileLocation flacFileLocation = encodeAction.getFileLocation();
    JFileLocation encodedFileLocation = encodeAction.getEncodedFileLocation();
    getMessageService().printMessage(JMessageService.ENCODE, flacFileLocation.resolve(), encodedFileLocation.resolve());
    getEncodingService().encode(flacFileLocation, encodeAction.getFlacMusicFile(), encodedFileLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JAddArtworkAction addArtworkAction) throws IOException {
    URI coverArtUrl = addArtworkAction.getCoverArtUri();
    getMessageService().printMessage(JMessageService.ARTWORK, addArtworkAction.getFileLocation(), coverArtUrl);
    getArtworkService().addArwork(addArtworkAction.getFileLocation().resolve(), coverArtUrl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JLinkAction linkAction) throws IOException {
    JFileLocation targetLocation = linkAction.getFileLocation();
    JFileUtils fileUtils = getFileUtils();
    JMessageService messageService = getMessageService();
    JDeviceService deviceService = getDeviceService();
    for (JUser owner : linkAction.getOwners()) {
      JFileLocation linkLocation = deviceService.getLinkLocation(owner, targetLocation);
      messageService.printMessage(JMessageService.LINK, targetLocation, linkLocation);
      fileUtils.link(targetLocation, linkLocation);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JUnlinkAction unlinkAction) throws IOException {
    JFileLocation targetLocation = unlinkAction.getFileLocation();
    JFileUtils fileUtils = getFileUtils();
    JMessageService messageService = getMessageService();
    JDeviceService deviceService = getDeviceService();
    for (JUser owner : unlinkAction.getOwners()) {
      JFileLocation linkLocation = deviceService.getLinkLocation(owner, targetLocation);
      messageService.printMessage(JMessageService.UNLINK, targetLocation, linkLocation);
      fileUtils.remove(linkLocation);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JChangeOwnerAction changeOwnerAction) throws IOException {
    boolean addOwner = changeOwnerAction.isAddOwner();
    String template = addOwner ? JMessageService.ADD_OWNER : JMessageService.REMOVE_OWNER;
    JFileLocation fileLocation = changeOwnerAction.getFileLocation();
    List<JUser> newOwners = changeOwnerAction.getNewOwners();
    getMessageService().printMessage(template, fileLocation, newOwners);
    getChangeOwnershipService().changeOwnership(
        changeOwnerAction.getMusicFile(),
        addOwner,
        newOwners);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JUpdateOwnershipAction updateOwnershipAction) throws IOException {
    getMessageService().printMessage(JMessageService.COMMIT_OWNERSHIP);
    getChangeOwnershipService().commitChanges();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JFailureAction failureAction) {
    List<Object> allParameters = Lists.newArrayList();
    JFileLocation fileLocation = failureAction.getFileLocation();
    if (fileLocation != null) {
      allParameters.add(fileLocation.resolve());
    }
    allParameters.addAll(Arrays.asList(failureAction.getParameters()));
    getMessageService()
        .printMessage(failureAction.getMessageTemplate(), Iterables.toArray(allParameters, Object.class));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visit(JCoverArtAction coverArtAction) {
    // Default is to do nothing.
  }

  /**
   * Gets the {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files.
   * 
   * @return the {@link uk.co.unclealex.music.files.JFileUtils} used to move and write protect files
   */
  public JFileUtils getFileUtils() {
    return fileUtils;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.message.JMessageService} used to display messages to the user.
   * 
   * @return the {@link uk.co.unclealex.music.message.JMessageService} used to display messages to the user
   */
  public JMessageService getMessageService() {
    return messageService;
  }

  /**
   * Gets the {@link ArtworkService} used to add cover art to a file.
   * 
   * @return the {@link ArtworkService} used to add cover art to a file
   */
  public ArtworkService getArtworkService() {
    return artworkService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.command.checkin.JEncodingService} used to encode FLAC files to MP3 files.
   * 
   * @return the {@link uk.co.unclealex.music.command.checkin.JEncodingService} used to encode FLAC files to MP3 files
   */
  public JEncodingService getEncodingService() {
    return encodingService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.devices.JDeviceService} that knows about devices.
   * 
   * @return the {@link uk.co.unclealex.music.devices.JDeviceService} that knows about devices
   */
  public JDeviceService getDeviceService() {
    return deviceService;
  }

  /**
   * Gets the {@link uk.co.unclealex.music.musicbrainz.JChangeOwnershipService} used to track and commit changes to
   * file ownership.
   * 
   * @return the {@link uk.co.unclealex.music.musicbrainz.JChangeOwnershipService} used to track and commit changes
   *         to file ownership
   */
  public JChangeOwnershipService getChangeOwnershipService() {
    return changeOwnershipService;
  }

}
