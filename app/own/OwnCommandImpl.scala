package own

import common.configuration.User
import common.files.{DirectoryService, FlacFileChecker, StagedFlacFileLocation}
import common.message.MessageService
import common.music.TagsService
import common.owners.OwnerService

/**
 * Created by alex on 23/11/14.
 */
class OwnCommandImpl(val ownerService: OwnerService, directoryService: DirectoryService)(implicit val flacFileChecker: FlacFileChecker, implicit val tagsService: TagsService) extends OwnCommand {

  override def changeOwnership(action: OwnAction, users: Seq[User], locations: Seq[StagedFlacFileLocation])(implicit messageService: MessageService): Unit = {
    val allLocations = directoryService.listFiles(locations)
    val tags = allLocations.filter(_.isFlacFile).flatMap(_.readTags.right.toOption).toSet
    val changeOwnershipFunction: User => Unit = action match {
      case Own => user => ownerService.own(user, tags)
      case Unown => user => ownerService.unown(user, tags)
    }
    users.foreach(changeOwnershipFunction)
  }
}
