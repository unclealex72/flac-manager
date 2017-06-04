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

package common.changes

import java.time.Instant

/**
  * A single change to an album.
  *
  * @param parentRelativePath The parent path of the album that changed.
  * @param at The date and time of the change.
  * @param relativePath The path of the album that changed.
 */
case class ChangelogItem(parentRelativePath: String, at: Instant, relativePath: String)