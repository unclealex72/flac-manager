import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms._

/**
 * @author maba, 2013-04-10
 */
case class User(id: Long)

case class Blog(id: Long, author: User)

case class Comment(id: Long, blog: Blog, comment: String)

object Blog {
  def findById(id: Long): Option[Blog] = {
    Some(Blog(id, User(1L)))
  }
}

object Comment {

  def create(comment: Comment) {
    // Save to DB
  }
}

object Comments extends Controller {

  val form = Form(
    mapping(
      "comment" -> nonEmptyText,
      "blog" -> mapping(
        "id" -> longNumber
      )((blogId) => {
        Blog.findById(blogId)
      }
        )(
          (blog: Option[Blog]) => Option(blog.get.id)
        ).verifying("The blog does not exist.", blog => blog.isDefined)
    )(
        (comment, blog) => {
          // blog.get is always possible since it has already been validated
          Comment(1L, blog.get, comment)
        }
      )(
        (comment: Comment) => Option(comment.comment, Some(comment.blog))
      )
  )

  def index = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => BadRequest,
      comment => {
        Comment.create(comment)
        Ok
      }
    )
  }
}