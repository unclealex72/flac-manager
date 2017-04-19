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

package controllers

import java.util
import javax.inject.{Inject, Singleton}

import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.{Parser, ParserEmulationProfile}
import com.vladsch.flexmark.util.KeepType
import com.vladsch.flexmark.util.options.MutableDataSet
import play.api.mvc.{Action, Controller}

import scala.io.Source

import java.lang.{Boolean => JB, Integer => JI}
/**
  * Created by alex on 18/04/17
  **/
@Singleton
class Index @Inject() extends Controller {

  lazy val html: String = {
    val options = new MutableDataSet
    options.setFrom(ParserEmulationProfile.GITHUB_DOC)
    options.set(Parser.EXTENSIONS, util.Arrays.asList(AutolinkExtension.create, AnchorLinkExtension.create(),
      StrikethroughExtension.create, TablesExtension.create, TaskListExtension.create))

    options.
      set[JB](AnchorLinkExtension.ANCHORLINKS_SET_ID, false).
      set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "anchor").
      set[JB](AnchorLinkExtension.ANCHORLINKS_SET_NAME, true).
      set(AnchorLinkExtension.ANCHORLINKS_TEXT_PREFIX, "<span class=\"octicon octicon-link\"></span>")

    // References compatibility
    options.set(Parser.REFERENCES_KEEP, KeepType.LAST)

    // Set GFM table parsing options
    options.
      set[JB](TablesExtension.COLUMN_SPANS, false).
      set[JI](TablesExtension.MIN_HEADER_ROWS, 1).
      set[JI](TablesExtension.MAX_HEADER_ROWS, 1).
      set[JB](TablesExtension.APPEND_MISSING_COLUMNS, true).
      set[JB](TablesExtension.DISCARD_EXTRA_COLUMNS, true).
      set[JB](TablesExtension.WITH_CAPTION, false).
      set[JB](TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)

    // Setup List Options for GitHub profile which is kramdown for documents
    options.setFrom(ParserEmulationProfile.GITHUB_DOC)

    val parser = Parser.builder(options).build
    val renderer = HtmlRenderer.builder(options).build

    val markdown = Source.fromInputStream(classOf[Index].getClassLoader.getResourceAsStream("markdown/README.md")).mkString
    val document = parser.parse(markdown)
    s"<html><head><title>Flac Manager</title></head><body>${renderer.render(document)}</body></html>"
  }

  def index = Action { implicit request =>
    Ok(views.html.index(html)).withHeaders("Content-Type" -> "text/html")
  }
}
