package common.changes

import org.joda.time.DateTime

/**
 * Created by alex on 14/12/14.
 */
case class ChangelogItem(parentRelativePath: String, at: DateTime, relativePath: String)