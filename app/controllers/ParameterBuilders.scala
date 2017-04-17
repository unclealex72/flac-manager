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

package controllers

import common.configuration.User
import common.files.{FlacFileLocation, StagedFlacFileLocation}
import play.api.data.FormError

trait ParameterBuilders {

  val UNOWN: String = "unown"
  val DATUM: String = "datum"
  val DIRECTORIES: String = "directories"
  val USERS: String = "users"


  val syncParametersBuilder: ParameterBuilder[Parameters]

  val initialiseParametersBuilder: ParameterBuilder[Parameters]

  val checkinParametersBuilder: ParameterBuilder[CheckinParameters]

  val checkoutParametersBuilder: ParameterBuilder[CheckoutParameters]

  val ownerParametersBuilder: ParameterBuilder[OwnerParameters]
}

trait ParameterBuilder[P] {

  def bindFromRequest()(implicit request: play.api.mvc.Request[_]): Either[Seq[FormError], P]
}

/**
 * A marker trait for encapsulating form parameters
 */
sealed trait Parameters

object SyncParameters extends Parameters

object InitialiseParameters extends Parameters

case class CheckinParameters(stagedFileLocations: Seq[StagedFlacFileLocation]) extends Parameters

case class CheckoutParameters(fileLocations: Seq[FlacFileLocation], unown: Boolean) extends Parameters

case class OwnerParameters(stagedFileLocations: Seq[StagedFlacFileLocation], owners: Seq[User]) extends Parameters
