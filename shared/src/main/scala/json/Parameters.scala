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
import java.nio.file.{Path, Paths}

import io.circe._
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
  */
case class CheckinParameters(relativeDirectories: Seq[PathAndRepository] = Seq.empty) extends Parameters

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
  implicit val parametersDecoder: Decoder[Parameters] = {

    implicit val pathDecoder: Decoder[Path] = Decoder.decodeString.map(Paths.get(_))
    implicit val repositoryTypeDecoder: Decoder[RepositoryType] = Decoder.decodeString.flatMap { str =>
      RepositoryType.values.find(rt => rt.identifier == str) match {
        case Some(rt) => Decoder.const(rt)
        case None => Decoder.failedWithMessage(
          s"Repository type must be one of: ${RepositoryType.values.map(_.identifier).mkString(", ")}")
      }
    }
    implicit val pathAndRepositoryDecoder: Decoder[PathAndRepository] =
      Decoder.forProduct2("path", "repositoryType")(PathAndRepository.apply)

    val checkinParametersDecoder =
      Decoder.forProduct1("relativeDirectories")(CheckinParameters.apply)
    val checkoutParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "unown")(CheckoutParameters.apply)
    val ownParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "users")(OwnParameters.apply)
    val unownParametersDecoder =
      Decoder.forProduct2("relativeDirectories", "users")(UnownParameters.apply)
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
  implicit val parametersEncoder: Encoder[Parameters] = {
    implicit val pathEncoder: Encoder[Path] = Encoder.encodeString.contramap(_.toString)
    implicit val repositoryTypeEncoder: Encoder[RepositoryType] = Encoder.encodeString.contramap(_.identifier)
    implicit val pathAndRepositoryEncoder: Encoder[PathAndRepository] =
      Encoder.forProduct2("path", "repositoryType")(par => (par.path, par.repositoryType))

    val checkinParametersEncoder: Encoder[CheckinParameters] =
      Encoder.forProduct1("relativeDirectories")(cp => cp.relativeDirectories)
    val checkoutParametersEncoder: Encoder[CheckoutParameters] =
      Encoder.forProduct2("relativeDirectories", "unown")(cp => (cp.relativeDirectories, cp.unown))
    val ownParametersEncoder: Encoder[OwnParameters] =
      Encoder.forProduct2("relativeDirectories", "users")(op => (op.relativeDirectories, op.users))
    val unownParametersEncoder: Encoder[UnownParameters] =
      Encoder.forProduct2("relativeDirectories", "users")(up => (up.relativeDirectories, up.users))
    val initialiseParametersEncoder: Encoder[InitialiseParameters] =
      Encoder.encodeJsonObject.contramap(_ => JsonObject.empty)
    Encoder.instance { parameters =>
      val (commandName, json) = parameters match {
        case cp : CheckinParameters => ("checkin", checkinParametersEncoder(cp))
        case cp : CheckoutParameters => ("checkout", checkoutParametersEncoder(cp))
        case op : OwnParameters => ("own", ownParametersEncoder(op))
        case up : UnownParameters => ("unown", unownParametersEncoder(up))
        case ip : InitialiseParameters => ("initialise", initialiseParametersEncoder(ip))
      }
      val obj = json.asObject.
        map(obj => obj.+:("command" -> Json.fromString(commandName))).
        getOrElse(JsonObject.empty)
      Json.fromJsonObject(obj)
    }
  }
}