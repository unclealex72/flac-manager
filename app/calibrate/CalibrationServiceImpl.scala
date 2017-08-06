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

package calibrate

import java.io.FileNotFoundException
import java.nio.file.{CopyOption, Files, Path, StandardCopyOption}
import java.time.{Clock, Duration}
import java.util.concurrent.Executors
import javax.inject.Inject

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import checkin.LossyEncoder
import common.message.Messages._
import common.message.{Message, MessageService, Messaging}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 05/08/17
  **/
class CalibrationServiceImpl @Inject() (
                              val lossyEncoders: Seq[LossyEncoder],
                              clock: Clock)(implicit val ec: ExecutionContext) extends CalibrationService with Messaging {

  /**
    * Encode files to try and find the best concurrency level for encoding.
    *
    * @param messageService The [[MessageService]] used to report progress.
    * @return A [[Future]].
    */
  override def calibrate(highestConcurrencyLevel: Int)(implicit messageService: MessageService): Future[ValidatedNel[Message, Unit]] = {
    val eFlacUri = Option(getClass.getResource("calibration.flac")).toRight(new FileNotFoundException("Cannot find the calibration resource"))
    eFlacUri match {
      case Right(flacUrl) =>
        val flacPath = Files.createTempFile("flac-manager-calibrator-", ".flac")
        val in = flacUrl.openStream()
        Files.copy(in, flacPath, StandardCopyOption.REPLACE_EXISTING)
        in.close()
        calibrate(highestConcurrencyLevel, flacPath).map(Valid(_)).andThen {
          case result =>
            Files.delete(flacPath)
            result
        }
      case Left(err) => Future.successful(Invalid(NonEmptyList.of(EXCEPTION(err))))
    }
  }

  def calibrate(highestConcurrencyLevel: Int, flacPath: Path)(implicit messageService: MessageService): Future[Unit] = {
    val lossyEncoderCount = lossyEncoders.size
    val concurrencyEncoderMismatch = highestConcurrencyLevel % lossyEncoderCount
    val requiredNumberOfEncodingJobs = highestConcurrencyLevel + lossyEncoderCount - concurrencyEncoderMismatch
    val encodingJobs: Seq[EncodingJob] =
      Stream.continually({}).flatMap(_ => lossyEncoders).map(lossyEncoder => EncodingJob(flacPath, lossyEncoder)).take(requiredNumberOfEncodingJobs)
    val empty: Future[Seq[CalibrationResult]] = Future.successful(Seq.empty)
    val eventualCalibrationResults = Range.inclusive(highestConcurrencyLevel, 1, -1).foldLeft(empty) { (acc, concurrencyLevel) =>
      acc.flatMap { previousCalibrationResults =>
        val eventualNewEncodingTime = measureEncodingTime(encodingJobs, concurrencyLevel)
        eventualNewEncodingTime.map {
          newEncodingTime => previousCalibrationResults :+ CalibrationResult(concurrencyLevel, toSeconds(newEncodingTime))
        }
      }
    }
    eventualCalibrationResults.map { calibrationResults =>
      val bestResult = calibrationResults.minBy(_.averageDuration)
      log(CALIBRATION_RESULT(bestResult.threads, bestResult.averageDuration))
    }
  }

  def measureEncodingTime(encodingJobs: Seq[EncodingJob], concurrencyLevel: Int)
                         (implicit messageService: MessageService): Future[Duration] = {
    log(CALIBRATION_RUN_STARTING(encodingJobs.length, concurrencyLevel))
    val start = clock.instant()
    val encodingExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(concurrencyLevel))
    val eventuallyEncodedJobs: Future[Seq[Unit]] = Future.sequence(encodingJobs.map(_.encode(encodingExecutionContext)))
    eventuallyEncodedJobs.map { _ =>
      val finish = clock.instant()
      val totalDuration = Duration.between(start, finish)
      val averageDuration = totalDuration.dividedBy(encodingJobs.length)
      log(CALIBRATION_RUN_FINISHED(encodingJobs.length, concurrencyLevel, toSeconds(totalDuration), toSeconds(averageDuration)))
      averageDuration
    }
  }

  def toSeconds(duration: Duration): Double = {
    duration.toMillis.toDouble / 1000d
  }

  case class EncodingJob(flacPath: Path, lossyEncoder: LossyEncoder) {

    def encode(encodingExecutionContext: ExecutionContext): Future[Unit] = Future {
      val targetPath = Files.createTempFile("flac-manager-calibration-", s".${lossyEncoder.encodesTo.extension}")
      Files.delete(targetPath)
      lossyEncoder.encode(flacPath, targetPath)
      ()
    }(encodingExecutionContext)
  }

  case class CalibrationResult(threads: Int, averageDuration: Double)
}
