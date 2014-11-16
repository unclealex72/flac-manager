package sync

import java.nio.file.{Path, Paths}

import scala.sys.process._

/**
 * A device connection service that uses pmount and pumount to mount and unmount devices.
 * Created by alex on 16/11/14.
 */
class DeviceConnectionServiceImpl extends DeviceConnectionService {

  override def mount(uuid: String): Path = {
    Seq("pmount", uuid) !< ProcessLogger(_ => {})
    Paths.get("/media", uuid)
  }

  override def unmount(path: Path): Unit = {
    Seq("pumount", path.toString) !< ProcessLogger(_ => {})
  }
}
