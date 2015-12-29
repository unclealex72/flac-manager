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

package common.files

import java.nio.file.Paths

import org.specs2.mutable._
import org.specs2.specification.Scope

/**
 * Created by alex on 07/11/14.
 */
class MtabDirectoryMappingServiceSpec extends Specification {

  val clientMtab =
    """
      |gvfsd-fuse /run/user/1000/gvfs fuse.gvfsd-fuse rw,nosuid,nodev 0 0
      |hurst:/media/DATA/home /mnt/home nfs rw,vers=4 0 0
      |hurst:/media/DATA/music /mnt/music nfs rw,vers=4 0 0
    """.stripMargin

  val mapper = new MtabDirectoryMappingService().withMtab(clientMtab)

  "The directory mapping service with local music directories " should {
    "Resolve a NFS mounted directory to a local directory" in {
      val localPath = mapper("/mnt/music/flac")
      localPath must be equalTo (Paths.get("/media/DATA/music/flac"))
    }
    "Resolve a non-NFS mounted directory to itself" in {
      val localPath = mapper("/mnt/muzac/flac")
      localPath must be equalTo (Paths.get("/mnt/muzac/flac"))
    }
  }
}
