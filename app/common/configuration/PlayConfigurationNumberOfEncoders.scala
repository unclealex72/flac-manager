/*
 * Copyright 2015 Alex Jones
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

package common.configuration

import java.nio.file.Paths

import com.typesafe.scalalogging.StrictLogging
import play.api.Configuration

/**
 * Get the users using Play configuration
 * Created by alex on 20/11/14.
 */
case class PlayConfigurationNumberOfEncoders(override val configuration: Configuration) extends PlayConfiguration[Int](configuration) with StrictLogging {

  def load(configuration: Configuration): Option[Int] = configuration.getInt("encoders")

  def numberOfEncoders = result

  override lazy val default = Runtime.getRuntime.availableProcessors()
}
