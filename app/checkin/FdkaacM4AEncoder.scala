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

package checkin

import checkin.FfmpegEncoder._
import common.files.Extension.M4A
import javax.inject.Inject
/**
  * An implementation that uses [[http://lame.sourceforge.net/ Lame]] to encode flac files to MP3.
  */
class FdkaacM4AEncoder @Inject()() extends FfmpegEncoder(
  M4A, false, "libfdk_aac", "acodec" ~> "aac", "strict" ~> "experimental")