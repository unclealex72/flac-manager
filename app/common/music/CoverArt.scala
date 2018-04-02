/*
 * Copyright 2018 Alex Jones
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

package common.music

import cats.data.{Validated, ValidatedNel}
import cats.implicits._
import common.message.Message
import common.message.Messages.INVALID_TAGS
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{JsObject, JsValue, Json}

/**
 *
 * @author alex
 *
 */

/**
  * An immutable class that contains information about a cover art picture, namely the image data and its mime type.
  * @param imageData The binary image data for the cover art.
  * @param mimeType The mime type for the cover art.
  */
case class CoverArt(imageData: Array[Byte], mimeType: String) {

  /**
    * Generate a JSON object that describes this cover art. The image is base 64 encoded.
    * @return A JSON object with mimeType and image fields.
    */
  def toJson: JsObject = Json.obj("mimeType" -> mimeType, "image" -> Base64.encodeBase64String(imageData))

  override def equals(obj: Any): Boolean = obj match {
    case cv: CoverArt => this.toJson == cv.toJson
    case _ => false
  }
}

object CoverArt {
  import JsonSupport._

  def fromJson(json: JsValue): ValidatedNel[Message, CoverArt] = {
    json match {
      case JsObject(fields) =>
        val vImage = str(fields, "image")
        val vMimeType = str(fields, "mimeType")
        (vImage |@| vMimeType).map { (image, mimeType) =>
          CoverArt(Base64.decodeBase64(image), mimeType)
        }
      case _ => Validated.invalidNel(INVALID_TAGS("Cannot parse cover art."))
    }
  }
}