// @SOURCE:C:/Documents and Settings/User/MB-Admin/conf/routes
// @HASH:deb116857d69fbcd20eb3894e48a370e733b158e
// @DATE:Wed Aug 06 17:58:16 IST 2014

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString


// @LINE:26
// @LINE:23
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:9
package controllers {

// @LINE:26
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
class ReverseArticleController {
    

// @LINE:18
def getAllArticles(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "get-all-Articles")
}
                                                

// @LINE:19
def getDescriptionOdArticle(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "getDescriptionOdArticle/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                

// @LINE:17
def getAllArticleCategory(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "getAllArticleCategory")
}
                                                

// @LINE:14
def updateArticle(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "editArticle")
}
                                                

// @LINE:26
def uploadImage(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "image/article/upload")
}
                                                

// @LINE:13
def addArticle(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "createArticle")
}
                                                

// @LINE:20
def deleteArticle(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "deleteArticle/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                

// @LINE:21
def infoArticle(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "Article/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                

// @LINE:16
def getAllDistricts(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "getAllDistricts")
}
                                                
    
}
                          

// @LINE:23
// @LINE:9
class ReverseAssets {
    

// @LINE:23
// @LINE:9
def at(file:String): Call = {
   (file: @unchecked) match {
// @LINE:9
case (file) if file == "app/images/favicon-2.ico" => Call("GET", _prefix + { _defaultPrefix } + "favicon.ico")
                                                        
// @LINE:23
case (file) if true => Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
                                                        
   }
}
                                                
    
}
                          

// @LINE:12
class ReverseApplication {
    

// @LINE:12
def index(): Call = {
   Call("GET", _prefix)
}
                                                
    
}
                          
}
                  


// @LINE:26
// @LINE:23
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:9
package controllers.javascript {

// @LINE:26
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
class ReverseArticleController {
    

// @LINE:18
def getAllArticles : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.getAllArticles",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "get-all-Articles"})
      }
   """
)
                        

// @LINE:19
def getDescriptionOdArticle : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.getDescriptionOdArticle",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "getDescriptionOdArticle/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:17
def getAllArticleCategory : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.getAllArticleCategory",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "getAllArticleCategory"})
      }
   """
)
                        

// @LINE:14
def updateArticle : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.updateArticle",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "editArticle"})
      }
   """
)
                        

// @LINE:26
def uploadImage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.uploadImage",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "image/article/upload"})
      }
   """
)
                        

// @LINE:13
def addArticle : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.addArticle",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "createArticle"})
      }
   """
)
                        

// @LINE:20
def deleteArticle : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.deleteArticle",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "deleteArticle/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:21
def infoArticle : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.infoArticle",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "Article/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:16
def getAllDistricts : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ArticleController.getAllDistricts",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "getAllDistricts"})
      }
   """
)
                        
    
}
              

// @LINE:23
// @LINE:9
class ReverseAssets {
    

// @LINE:23
// @LINE:9
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      if (file == """ + implicitly[JavascriptLitteral[String]].to("app/images/favicon-2.ico") + """) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "favicon.ico"})
      }
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
      }
   """
)
                        
    
}
              

// @LINE:12
class ReverseApplication {
    

// @LINE:12
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        
    
}
              
}
        


// @LINE:26
// @LINE:23
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
// @LINE:12
// @LINE:9
package controllers.ref {

// @LINE:26
// @LINE:21
// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:14
// @LINE:13
class ReverseArticleController {
    

// @LINE:18
def getAllArticles(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.getAllArticles(), HandlerDef(this, "controllers.ArticleController", "getAllArticles", Seq(), "GET", """""", _prefix + """get-all-Articles""")
)
                      

// @LINE:19
def getDescriptionOdArticle(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.getDescriptionOdArticle(id), HandlerDef(this, "controllers.ArticleController", "getDescriptionOdArticle", Seq(classOf[Long]), "GET", """""", _prefix + """getDescriptionOdArticle/$id<[^/]+>""")
)
                      

// @LINE:17
def getAllArticleCategory(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.getAllArticleCategory(), HandlerDef(this, "controllers.ArticleController", "getAllArticleCategory", Seq(), "GET", """""", _prefix + """getAllArticleCategory""")
)
                      

// @LINE:14
def updateArticle(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.updateArticle(), HandlerDef(this, "controllers.ArticleController", "updateArticle", Seq(), "POST", """""", _prefix + """editArticle""")
)
                      

// @LINE:26
def uploadImage(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.uploadImage(), HandlerDef(this, "controllers.ArticleController", "uploadImage", Seq(), "POST", """""", _prefix + """image/article/upload""")
)
                      

// @LINE:13
def addArticle(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.addArticle(), HandlerDef(this, "controllers.ArticleController", "addArticle", Seq(), "POST", """""", _prefix + """createArticle""")
)
                      

// @LINE:20
def deleteArticle(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.deleteArticle(id), HandlerDef(this, "controllers.ArticleController", "deleteArticle", Seq(classOf[Long]), "GET", """""", _prefix + """deleteArticle/$id<[^/]+>""")
)
                      

// @LINE:21
def infoArticle(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.infoArticle(id), HandlerDef(this, "controllers.ArticleController", "infoArticle", Seq(classOf[Long]), "GET", """""", _prefix + """Article/$id<[^/]+>""")
)
                      

// @LINE:16
def getAllDistricts(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ArticleController.getAllDistricts(), HandlerDef(this, "controllers.ArticleController", "getAllDistricts", Seq(), "GET", """""", _prefix + """getAllDistricts""")
)
                      
    
}
                          

// @LINE:23
// @LINE:9
class ReverseAssets {
    

// @LINE:9
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """""", _prefix + """favicon.ico""")
)
                      
    
}
                          

// @LINE:12
class ReverseApplication {
    

// @LINE:12
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq(), "GET", """ Home page""", _prefix + """""")
)
                      
    
}
                          
}
                  
      