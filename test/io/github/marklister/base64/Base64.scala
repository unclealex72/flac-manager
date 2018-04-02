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

package io.github.marklister.base64

object Base64 {

  class B64Scheme(val encodeTable: IndexedSeq[Char]) {
    lazy val decodeTable = collection.immutable.TreeMap(encodeTable.zipWithIndex: _*)
  }

  lazy val base64 = new B64Scheme(('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9') ++ Seq('+', '/'))
  lazy val base64Url = new B64Scheme(base64.encodeTable.dropRight(2) ++ Seq('-', '_'))

  implicit class Encoder(b: Array[Byte]) {
    private[this] val zero: Array[Byte] = Array(0, 0).map(_.toByte)
    lazy val pad: Int = (3 - b.length % 3) % 3

    def toBase64(implicit scheme: B64Scheme = base64): String = {
      def sixBits(x: Array[Byte]): Seq[Int] = {
        val a: Int = (x(0) & 0xfc) >> 2
        val b: Int = ((x(0) & 0x3) << 4) + ((x(1) & 0xf0) >> 4)
        val c: Int = ((x(1) & 0xf) << 2) + ((x(2) & 0xc0) >> 6)
        val d: Int = x(2) & 0x3f
        Seq(a, b, c, d)
      }
      ((b ++ zero.take(pad)).grouped(3)
        .flatMap(sixBits)
        .map(x => scheme.encodeTable(x))
        .toSeq
        .dropRight(pad) :+ "=" * pad)
        .mkString
    }
  }

  implicit class Decoder(s: String) {
    lazy val cleanS: String = s.reverse.dropWhile(_ == '=').reverse
    lazy val pad: Int = s.length - cleanS.length

    def toByteArray(implicit scheme: B64Scheme = base64): Array[Byte] = {
      def threeBytes(s: Seq[Char]): Array[Byte] = {
        val r: Int = s.map(scheme.decodeTable(_)).foldLeft(0)((a, b) => (a << 6) + b)
        java.nio.ByteBuffer.allocate(8).putLong(r).array().takeRight(3)
      }
      if (pad > 2 || s.length % 4 != 0) throw new java.lang.IllegalArgumentException("Invalid Base64 String:" + s)
      if (!cleanS.forall(scheme.encodeTable.contains(_))) throw new java.lang.IllegalArgumentException("Invalid Base64 String:" + s)

      (cleanS + "A" * pad)
        .grouped(4).flatMap(threeBytes(_))
        .toArray
        .dropRight(pad)
    }
  }

}