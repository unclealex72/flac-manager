/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package common.commands

import java.io._

import scala.sys.process.{BasicIO, ProcessIO}

/**
 * A class that can communicate with processes using stdin and stdout and multiple line responses that must
 * end in a line containing only "OK"
 * Created by alex on 29/10/14.
 */
class ProcessCommunicator {

  val OK: String = "OK"

  var stdin: Option[BufferedWriter] = None
  var stdout: Option[BufferedReader] = None
  var stderr: Option[BufferedReader] = None

  def toProcessIO: ProcessIO = {
    new ProcessIO(
      os => {
        stdin = Some(new BufferedWriter(new OutputStreamWriter(os)))
      },
      is => {
        stdout = Some(new BufferedReader(new InputStreamReader(is)))
      },
      is => {
        stderr = Some(new BufferedReader(new InputStreamReader(is)))
      },
      true)
  }

  def write(command: String): Unit = for (in <- stdin) {
    in.write(command)
    in.newLine
    in.flush
  }

  def read: Seq[String] = stdout match {
    case Some(out) => Stream.continually(out.readLine()).takeWhile(l => l != null && l != OK).toSeq
    case _ => Seq.empty
  }

  def close: Unit = {
    read
    Seq(stdin, stdout, stderr).flatten.foreach(BasicIO.close)
  }
}

/**
 * An implicit to allow a Process Communicator to be used in place of a ProcessIO
 */
object ProcessCommunicator {

  def apply(): ProcessCommunicator = new ProcessCommunicator

  implicit def processCommunicatorToProcessIO(pc: ProcessCommunicator): ProcessIO = pc.toProcessIO
}