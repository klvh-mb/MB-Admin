// @SOURCE:C:/Documents and Settings/User/MB-Admin/conf/routes
// @HASH:deb116857d69fbcd20eb3894e48a370e733b158e
// @DATE:Wed Aug 06 17:58:16 IST 2014


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:9
private[this] lazy val controllers_Assets_at0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("favicon.ico"))))
        

// @LINE:12
private[this] lazy val controllers_Application_index1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:13
private[this] lazy val controllers_ArticleController_addArticle2 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("createArticle"))))
        

// @LINE:14
private[this] lazy val controllers_ArticleController_updateArticle3 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("editArticle"))))
        

// @LINE:16
private[this] lazy val controllers_ArticleController_getAllDistricts4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getAllDistricts"))))
        

// @LINE:17
private[this] lazy val controllers_ArticleController_getAllArticleCategory5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getAllArticleCategory"))))
        

// @LINE:18
private[this] lazy val controllers_ArticleController_getAllArticles6 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("get-all-Articles"))))
        

// @LINE:19
private[this] lazy val controllers_ArticleController_getDescriptionOdArticle7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("getDescriptionOdArticle/"),DynamicPart("id", """[^/]+""",true))))
        

// @LINE:20
private[this] lazy val controllers_ArticleController_deleteArticle8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("deleteArticle/"),DynamicPart("id", """[^/]+""",true))))
        

// @LINE:21
private[this] lazy val controllers_ArticleController_infoArticle9 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("Article/"),DynamicPart("id", """[^/]+""",true))))
        

// @LINE:23
private[this] lazy val controllers_Assets_at10 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
        

// @LINE:26
private[this] lazy val controllers_ArticleController_uploadImage11 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("image/article/upload"))))
        
def documentation = List(("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """favicon.ico""","""controllers.Assets.at(path:String = "/public", file:String = "app/images/favicon-2.ico")"""),("""GET""", prefix,"""controllers.Application.index"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """createArticle""","""controllers.ArticleController.addArticle"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """editArticle""","""controllers.ArticleController.updateArticle"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getAllDistricts""","""controllers.ArticleController.getAllDistricts"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getAllArticleCategory""","""controllers.ArticleController.getAllArticleCategory"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """get-all-Articles""","""controllers.ArticleController.getAllArticles"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """getDescriptionOdArticle/$id<[^/]+>""","""controllers.ArticleController.getDescriptionOdArticle(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """deleteArticle/$id<[^/]+>""","""controllers.ArticleController.deleteArticle(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """Article/$id<[^/]+>""","""controllers.ArticleController.infoArticle(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """image/article/upload""","""controllers.ArticleController.uploadImage()""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:9
case controllers_Assets_at0(params) => {
   call(Param[String]("path", Right("/public")), Param[String]("file", Right("app/images/favicon-2.ico"))) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """""", Routes.prefix + """favicon.ico"""))
   }
}
        

// @LINE:12
case controllers_Application_index1(params) => {
   call { 
        invokeHandler(controllers.Application.index, HandlerDef(this, "controllers.Application", "index", Nil,"GET", """ Home page""", Routes.prefix + """"""))
   }
}
        

// @LINE:13
case controllers_ArticleController_addArticle2(params) => {
   call { 
        invokeHandler(controllers.ArticleController.addArticle, HandlerDef(this, "controllers.ArticleController", "addArticle", Nil,"POST", """""", Routes.prefix + """createArticle"""))
   }
}
        

// @LINE:14
case controllers_ArticleController_updateArticle3(params) => {
   call { 
        invokeHandler(controllers.ArticleController.updateArticle, HandlerDef(this, "controllers.ArticleController", "updateArticle", Nil,"POST", """""", Routes.prefix + """editArticle"""))
   }
}
        

// @LINE:16
case controllers_ArticleController_getAllDistricts4(params) => {
   call { 
        invokeHandler(controllers.ArticleController.getAllDistricts, HandlerDef(this, "controllers.ArticleController", "getAllDistricts", Nil,"GET", """""", Routes.prefix + """getAllDistricts"""))
   }
}
        

// @LINE:17
case controllers_ArticleController_getAllArticleCategory5(params) => {
   call { 
        invokeHandler(controllers.ArticleController.getAllArticleCategory, HandlerDef(this, "controllers.ArticleController", "getAllArticleCategory", Nil,"GET", """""", Routes.prefix + """getAllArticleCategory"""))
   }
}
        

// @LINE:18
case controllers_ArticleController_getAllArticles6(params) => {
   call { 
        invokeHandler(controllers.ArticleController.getAllArticles, HandlerDef(this, "controllers.ArticleController", "getAllArticles", Nil,"GET", """""", Routes.prefix + """get-all-Articles"""))
   }
}
        

// @LINE:19
case controllers_ArticleController_getDescriptionOdArticle7(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.ArticleController.getDescriptionOdArticle(id), HandlerDef(this, "controllers.ArticleController", "getDescriptionOdArticle", Seq(classOf[Long]),"GET", """""", Routes.prefix + """getDescriptionOdArticle/$id<[^/]+>"""))
   }
}
        

// @LINE:20
case controllers_ArticleController_deleteArticle8(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.ArticleController.deleteArticle(id), HandlerDef(this, "controllers.ArticleController", "deleteArticle", Seq(classOf[Long]),"GET", """""", Routes.prefix + """deleteArticle/$id<[^/]+>"""))
   }
}
        

// @LINE:21
case controllers_ArticleController_infoArticle9(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.ArticleController.infoArticle(id), HandlerDef(this, "controllers.ArticleController", "infoArticle", Seq(classOf[Long]),"GET", """""", Routes.prefix + """Article/$id<[^/]+>"""))
   }
}
        

// @LINE:23
case controllers_Assets_at10(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        

// @LINE:26
case controllers_ArticleController_uploadImage11(params) => {
   call { 
        invokeHandler(controllers.ArticleController.uploadImage(), HandlerDef(this, "controllers.ArticleController", "uploadImage", Nil,"POST", """""", Routes.prefix + """image/article/upload"""))
   }
}
        
}
    
}
        