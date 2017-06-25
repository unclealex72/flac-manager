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

import org.specs2.matcher.{Matcher, MustMatchers}

/**
 * Matchers for changes.
 * Created by alex on 30/11/14.
 */
trait ChangeMatchers extends MustMatchers {

  def beTheSameChangeAs: Change => Matcher[Change] =
    (be_==(_:(String, String, Long, String))) ^^^ ((c: Change) => (c.user.name, c.action.action, c.at.toEpochMilli, c.relativePath.toString))
}
