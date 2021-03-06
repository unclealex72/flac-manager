/*
 * Copyright 2017 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package json
import java.nio.file.{FileSystem, Path}

import io.circe._
import json.PathCodec._
import json.RepositoryType.{FlacRepositoryType, StagingRepositoryType}

/**
  * A marker trait for server command parameters. Each command parameter object is an RPC style JSON payload that
  * describes what command should be executed on the server and also includes the arguments that should be sent
  * to the command.
  **/
sealed trait Parameters

/**
  * The `checkin` command
  * @param relativeDirectories A list of paths relative to the staging repository.
  * @param allowUnowned A flag to indicate whether files without owners can be checked in.
  */
case class CheckinParameters(relativeDirectories: Seq[PathAndRepository] = Seq.empty, allowUnowned: Boolean = false) extends Parameters

/**
  * The `checkout` command
  * @param relativeDirectories A list of paths relative to the flac repository.
  * @param unown A flag to indicate whether checked out files should also be removed from user repositories.
  */
case class CheckoutParameters(relativeDirectories: Seq[PathAndRepository] = Seq.empty, unown: Boolean = false) extends Parameters

/**
  * The `own` command
  * @param relativeDirectories A list of paths relative to the staging or flac repository.
  * @param users The names of the users who will own the albums.
  */
case class OwnParameters(
                          relativeDirectories: Seq[PathAndRepository] = Seq.empty,
                          users: Seq[String] = Seq.empty) extends Parameters

/**
  * The `own` command
  * @param relativeDirectories A list of paths relative to the staging or flac repository.
  * @param users The names of the users who will no longer own the albums.
  */
case class UnownParameters(relativeDirectories: Seq[PathAndRepository] = Seq.empty,
                           users: Seq[String] = Seq.empty) extends Parameters

/**
  * The `multidisc` command
  * @param relativeDirectories A list of paths relative to the staging repository.
  * @param maybeMultiAction An enumeration used to decide whether multi disc albums should be split or joined.
  */
case class MultiDiscParameters(relativeDirectories: Seq[PathAndRepository] = Seq.empty,
                               maybeMultiAction: Option[MultiAction] = None) extends Parameters


/**
  * The `calibrate` command.
  * @param maybeMaximumNumberOfThreads The maximum number of threads to use during calibration.
  */
case class CalibrateParameters(maybeMaximumNumberOfThreads: Option[Int] = None) extends Parameters

/**
  * A class to hold a relative path an the repository it is relative to.
  * @param path The relative path.
  * @param repositoryType The type of repository the path is relative to.
  */
case class PathAndRepository(path: Path, repositoryType: RepositoryType)

/**
  * The `initialise` command.
  */
case class InitialiseParameters() extends Parameters

/**
  * JSON serialisers and deserialisers.
  */
object Parameters {

  /**
    * Get the directory types for a [[Parameters]] object.
    * @param parameters The parameters object to query.
    * @return The directory types that the parameters object changes, if any.
    */
  def repositoryTypes(parameters: Parameters): Seq[RepositoryType] = {
    parameters match {
      case _ : InitialiseParameters => Seq.empty
      case _ : CheckoutParameters => Seq(FlacRepositoryType)
      case _ : OwnParameters => Seq(FlacRepositoryType, StagingRepositoryType)
      case _ : UnownParameters => Seq(FlacRepositoryType, StagingRepositoryType)
      case _ => Seq(StagingRepositoryType)
    }
  }



  /**
    * A [[https://circe.github.io/circe/ circe]] decoder for parameters.
    */
  implicit def parametersDecoder(implicit fs: FileSystem): Decoder[Parameters] = {

    def enumDecoder[E <: IdentifiableEnumEntry](name: String, values: Seq[E]): Decoder[E] = Decoder.decodeString.flatMap { str =>
      values.find(rt => rt.identifier == str) match {
        case Some(rt) => Decoder.const(rt)
        case None => Decoder.failedWithMessage(
          s"$name must be one of: ${values.map(_.identifier).mkString(", ")}")
      }
    }
    implicit val repositoryTypeDecoder: Decoder[RepositoryType] = enumDecoder("repositoryType", RepositoryType.values)
    implicit val multiActionDecoder: Decoder[MultiAction] = enumDecoder("action", MultiAction.values)

    implicit val pathAndRepositoryDecoder: Decoder[PathAndRepository] =
      Decoder.forProduct2("path", "repositoryType")(PathAndRepository.apply)

    val checkinParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "allowUnowned")(CheckinParameters.apply)
    val checkoutParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "unown")(CheckoutParameters.apply)
    val ownParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "users")(OwnParameters.apply)
    val unownParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "users")(UnownParameters.apply)
    val multiParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "action")(MultiDiscParameters.apply)
    val calibrateParametersDecoder =
      Decoder.forProduct1("maximumNumberOfThreads")(CalibrateParameters.apply)
    val initialiseParametersDecoder = Decoder.instance { hcursor =>
      if (hcursor.value.isObject) {
        Right(InitialiseParameters())
      }
      else {
        Left(DecodingFailure("An object is required to decode parameters", hcursor.history))
      }
    }
    Decoder.instance { hcursor =>
      hcursor.value.asObject match {
        case Some(jsonObject) =>
          jsonObject.toMap.get("command").flatMap(v => v.asString) match {
            case Some(commandName) =>
              val maybeDecoder: Option[Decoder[Parameters]] = commandName match {
                case "own" => Some(ownParametersDecoder.map[Parameters](identity))
                case "unown" => Some(unownParametersDecoder.map[Parameters](identity))
                case "checkin" => Some(checkinParametersDecoder.map[Parameters](identity))
                case "checkout" => Some(checkoutParametersDecoder.map[Parameters](identity))
                case "initialise" => Some(initialiseParametersDecoder.map[Parameters](identity))
                case "multidisc" => Some(multiParametersDecoder.map[Parameters](identity))
                case "calibrate" => Some(calibrateParametersDecoder.map[Parameters](identity))
                case _ => None
              }
              maybeDecoder match {
                case Some(decoder) => decoder(hcursor)
                case None =>
                  Left(DecodingFailure("Cannot find a command string for a parameter", hcursor.history))
              }
            case None => Left(DecodingFailure("Cannot find a command string for a parameter", hcursor.history))
          }
        case None => Left(DecodingFailure("An object is required to decode parameters", hcursor.history))
      }
    }
  }

  /**
    * A [[https://circe.github.io/circe/ circe]] encoder for parameters.
    */
  implicit def parametersEncoder(implicit fs: FileSystem): Encoder[Parameters] = {
    implicit val pathEncoder: Encoder[Path] = Encoder.encodeString.contramap(_.toString)
    def enumEncoder[E <: IdentifiableEnumEntry]: Encoder[E] = Encoder.encodeString.contramap(_.identifier)
    implicit val repositoryTypeEncoder: Encoder[RepositoryType] = enumEncoder[RepositoryType]
    implicit val multiActionEncoder: Encoder[MultiAction] = enumEncoder[MultiAction]

    implicit val pathAndRepositoryEncoder: Encoder[PathAndRepository] =
      Encoder.forProduct2("path", "repositoryType")(par => (par.path, par.repositoryType))

    val checkinParametersEncoder: Encoder[CheckinParameters] =
      Encoder.forProduct2("relativeDirectories", "allowUnowned")(cp => (cp.relativeDirectories, cp.allowUnowned))
    val checkoutParametersEncoder: Encoder[CheckoutParameters] =
      Encoder.forProduct2("relativeDirectories", "unown")(cp => (cp.relativeDirectories, cp.unown))
    val ownParametersEncoder: Encoder[OwnParameters] =
      Encoder.forProduct2("relativeDirectories", "users")(op => (op.relativeDirectories, op.users))
    val unownParametersEncoder: Encoder[UnownParameters] =
      Encoder.forProduct2("relativeDirectories", "users")(up => (up.relativeDirectories, up.users))
    val multiParametersEncoder: Encoder[MultiDiscParameters] =
      Encoder.forProduct2("relativeDirectories", "action")(mp => (mp.relativeDirectories, mp.maybeMultiAction))
    val initialiseParametersEncoder: Encoder[InitialiseParameters] =
      Encoder.encodeJsonObject.contramap(_ => JsonObject.empty)
    val calibrateParametersEncoder: Encoder[CalibrateParameters] =
      Encoder.forProduct1("maximumNumberOfThreads")(mp => mp.maybeMaximumNumberOfThreads)
    Encoder.instance { parameters =>
      val (commandName, json) = parameters match {
        case cp : CheckinParameters => ("checkin", checkinParametersEncoder(cp))
        case cp : CheckoutParameters => ("checkout", checkoutParametersEncoder(cp))
        case op : OwnParameters => ("own", ownParametersEncoder(op))
        case up : UnownParameters => ("unown", unownParametersEncoder(up))
        case ip : InitialiseParameters => ("initialise", initialiseParametersEncoder(ip))
        case mp : MultiDiscParameters => ("multidisc", multiParametersEncoder(mp))
        case cp : CalibrateParameters => ("calibrate", calibrateParametersEncoder(cp))
      }
      val obj = json.asObject.
        map(obj => obj.+:("command" -> Json.fromString(commandName))).
        getOrElse(JsonObject.empty)
      Json.fromJsonObject(obj)
    }
  }
}