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

import java.nio.file.Path

import com.typesafe.scalalogging.StrictLogging
import common.configuration.{Directories, User, Users}
import common.files._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.{Form, FormError}
import play.api.mvc.Request

/**
 * Created by alex on 09/11/14.
 */
class ParameterBuildersImpl(val users: Users, val directoryMappingService: DirectoryMappingService)(implicit val directories: Directories, val fileLocationExtensions: FileLocationExtensions) extends ParameterBuilders with StrictLogging {

  class ZeroParameterBuilder[C](constant: C) extends ParameterBuilder[C] {
    override def bindFromRequest()(implicit request: Request[_]): Either[Seq[FormError], C] = Right(constant)
  }

  class SingleParameterBuilder[P](form: Form[P]) extends ParameterBuilder[P] {
    override def bindFromRequest()(implicit request: Request[_]): Either[Seq[FormError], P] =
      form.bindFromRequest().fold(formWithErrors => Left(formWithErrors.errors), p => Right(p))
  }

  implicit class FormToParameterBuilderImplicit[P](form: Form[P]) {
    val asParameterBuilder = new SingleParameterBuilder[P](form)
  }

  class DoubleParameterBuilder[P, Q, R](pb1: ParameterBuilder[P], pb2: P => ParameterBuilder[Q], combiner: P => Q => R) extends ParameterBuilder[R] {
    override def bindFromRequest()(implicit request: Request[_]): Either[Seq[FormError], R] =
      for {
        p <- pb1.bindFromRequest().right
        q <- pb2(p).bindFromRequest().right
      } yield combiner(p)(q)
  }

  /**
   * The model for including a flag as to whether checked out files should be unowned.
   */
  case class UnownParameters(unown: Boolean)

  /**
   * A parameter builder for forms that can contain a flag as to whether checked out files should be unowned.
   */
  val unownParamtersBuilder: SingleParameterBuilder[UnownParameters] =
    Form(mapping(UNOWN -> boolean)(UnownParameters.apply)(UnownParameters.unapply)).asParameterBuilder

  /**
   * The model for including an mtab in a form.
   * @param mtab
   */
  case class MtabParameters(mtab: String)

  /**
   * A parameter builder for forms containing /etc/mtab.
   */
  val mtabParameterBuilder: SingleParameterBuilder[MtabParameters] =
    Form(mapping(MTAB -> nonEmptyText)(MtabParameters.apply)(MtabParameters.unapply)).asParameterBuilder

  /**
   * The model for parameters than include a list of file locations.
   * @param fileLocations
   * @tparam FL
   */
  case class FileLocationsParameters[FL <: FileLocation](fileLocations: Seq[FL])

  /**
   * Allow the min constraint to be used on lengths of sequences as to allow validation against non-empty sequences.
   */
  implicit val seqByLengthOrdering: Ordering[Seq[String]] = Ordering.by(_.size)

  def mtabFileLocationParameterBuilder[FL <: FileLocation](fileLocationBuilder: Path => Option[FL])(mtabParameters: MtabParameters): ParameterBuilder[FileLocationsParameters[FL]] = {
    val mapper = (path: String) =>
      (fileLocationBuilder compose directoryMappingService.withMtab(mtabParameters.mtab))(path).filter{ path =>
        if (path.isDirectory) true else {
          logger.debug(s"Rejecting $path as it is not a directory.")
          false
        }
      }
    val applier: Seq[String] => FileLocationsParameters[FL] = paths => FileLocationsParameters(paths.map(mapper).flatten)
    val nonifier: FileLocationsParameters[FL] => Option[Seq[String]] = _ => None
    val fileLocationConstraint: Constraint[String] = Constraint("fileLocation") { path =>
      mapper(path) match {
        case Some(fileLocation) => Valid
        case None => Invalid(Seq(ValidationError("invalidPath", path)))
      }
    }
    Form(
      mapping(DIRECTORIES -> seq(nonEmptyText.verifying(fileLocationConstraint)).verifying(min(Seq(""))))
        (applier)(nonifier)
    ).asParameterBuilder
  }

  val checkoutParametersBuilder: ParameterBuilder[CheckoutParameters] = {
    val mtabAndFilesParameterBuilder = new DoubleParameterBuilder(
      mtabParameterBuilder, mtabFileLocationParameterBuilder(FlacFileLocation.unapply),
      (m: MtabParameters) => (flp: FileLocationsParameters[FlacFileLocation]) => CheckoutParameters(flp.fileLocations, false))
    new DoubleParameterBuilder(
      mtabAndFilesParameterBuilder,
      (cp: CheckoutParameters) => unownParamtersBuilder,
      (cp: CheckoutParameters) => (u: UnownParameters) => CheckoutParameters(cp.fileLocations, u.unown))
  }

  val checkinParametersBuilder: ParameterBuilder[CheckinParameters] = new DoubleParameterBuilder(
    mtabParameterBuilder, mtabFileLocationParameterBuilder(StagedFlacFileLocation.unapply),
    (m: MtabParameters) => (flp: FileLocationsParameters[StagedFlacFileLocation]) => CheckinParameters(flp.fileLocations))

  val syncParametersBuilder: ParameterBuilder[Parameters] = new ZeroParameterBuilder[Parameters](SyncParameters)

  val initialiseParametersBuilder: ParameterBuilder[Parameters] = new ZeroParameterBuilder[Parameters](InitialiseParameters)

  /**
   * Build the owners parameter builders.
   * @param owners
   */
  case class OwnerOnlyParameters(owners: Seq[User])

  val userFinder = (username: String) => users.allUsers.find(user => user.name == username)
  val applier: Seq[String] => OwnerOnlyParameters = usernames => OwnerOnlyParameters(usernames.map(userFinder).flatten)
  val nonifier: OwnerOnlyParameters => Option[Seq[String]] = _ => None
  val validUserConstraint: Constraint[String] = Constraint("user") { username =>
    userFinder(username) match {
      case Some(fileLocation) => Valid
      case None => Invalid(Seq(ValidationError("invalidUser", username)))
    }
  }
  val ownerOnlyParametersBuilder = Form(
    mapping(USERS -> seq(nonEmptyText.verifying(validUserConstraint)).verifying(min(Seq(""))))
      (applier)(nonifier)
  ).asParameterBuilder

  val ownerParametersBuilder: ParameterBuilder[OwnerParameters] = new DoubleParameterBuilder(
    checkinParametersBuilder,
    (cp: CheckinParameters) => ownerOnlyParametersBuilder,
    (cp: CheckinParameters) => (oop: OwnerOnlyParameters) => OwnerParameters(cp.stagedFileLocations, oop.owners))
}
