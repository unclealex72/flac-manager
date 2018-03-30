/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common.files

import com.typesafe.scalalogging.StrictLogging
import common.message.MessageService

/**
 * An implementation of [[FileSystem]] that decorates another [[FileSystem]] by calling before and after methods.
 *
 */
abstract class DecoratingFileSystem extends FileSystem with StrictLogging {

  /**
    * The filesystem to delete to.
    *
    */
  val delegate: FileSystem

  /**
    * Wrap a method by calling the before and after methods before and after the invocation respectively.
    * @param block The callback to execute.
    * @param files The [[File]]s to send to the callback.
    */
  def wrap(block: => FileSystem => Unit)(files: File*): Unit = {
    before(files)
    try {
      block(delegate)
    }
    finally {
      after(files)
    }
  }

  /**
    * @inheritdoc
    */
  override def move(sourceFile: File, targetFile: File)(implicit messageService: MessageService): Unit =
    wrap(_.move(sourceFile, targetFile))(sourceFile, targetFile)
  /**
    * @inheritdoc
    */
  override def copy(sourceFile: File, targetFile: File)(implicit messageService: MessageService): Unit =
    wrap(_.copy(sourceFile, targetFile))(targetFile)

  /**
    * @inheritdoc
    */
  override def remove(file: File)(implicit messageService: MessageService): Unit = {
    wrap(_.remove(file))(file)
  }

  /**
    * @inheritdoc
    */
  override def makeWorldReadable(file: File)(implicit messageService: MessageService): Unit = {
    wrap(_.makeWorldReadable(file))(file)
  }

  /**
    * @inheritdoc
    */
  override def link(file: File, link: File)(implicit messageService: MessageService): Unit =
    wrap(_.link(file, link))(file, link)

  /**
    * Override this method with code that will be called before any other method invocation.
    * @param files The file s to send to the original invocation.
    */
  def before(files: Seq[File]): Unit

  /**
    * Override this method with code that will be called after any other method invocation.
    * @param files The file s sent to the original invocation.
    */
  def after(files: Seq[File]): Unit
}
