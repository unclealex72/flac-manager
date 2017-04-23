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

package common.music

import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{JsObject, Json}

/**
 * An immutable class that contains information about a cover art picture, namely the image data and its mime type.
 * @author alex
 *
 */
case class CoverArt(
                     /**
                      * The binary image data for the cover art.
                      */
                     imageData: Array[Byte],

                     /**
                      * The mime type for the cover art.
                      */
                     mimeType: String) {

  def toJson: JsObject = Json.obj("mimeType" -> mimeType, "image" -> Base64.encodeBase64String(imageData))
}