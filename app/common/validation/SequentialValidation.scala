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

package common.validation

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.implicits._
import common.message.Message

/**
  * Add support for validating elements in a sequence to a class.
  **/
trait SequentialValidation {

  /**
    * Run a validation step against every member of a sequence and collate the results in a [[ValidatedNel]]
    * @param as The elements to validate
    * @param validationStep A function that checks an element is valid and transforms it if need be.
    * @tparam A The type of the source elements.
    * @tparam B The type of elements to return.
    * @return A [[ValidatedNel]] that contains the result of all the validation steps.
    */
  def runValidation[A, B](as: Seq[A])(validationStep: A => Validated[Message, B]): ValidatedNel[Message, Seq[B]] = {
    def validationNelStep(a: A): ValidatedNel[Message, B] = validationStep(a).toValidatedNel
    runValidationNel(as)(validationNelStep)
  }

  /**
    * Run a validation step against every member of a sequence and collate the results in a [[ValidatedNel]]
    * @param as The elements to validate
    * @param validationStep A function that checks an element is valid and transforms it if need be.
    * @tparam A The type of the source elements.
    * @tparam B The type of elements to return.
    * @return A [[ValidatedNel]] that contains the result of all the validation steps.
    */
  def runValidationNel[A, B](as: Seq[A])(validationStep: A => ValidatedNel[Message, B]): ValidatedNel[Message, Seq[B]] = {
    val empty: ValidatedNel[Message, Seq[B]] = Valid(Seq.empty)
    as.foldLeft(empty) { (results, a) =>
      (results |@| validationStep(a)).map(_ :+ _)
    }
  }

}
