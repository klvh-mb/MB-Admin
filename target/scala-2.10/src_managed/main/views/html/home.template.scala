
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object home extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.1*/("""<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>minibean</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="assets/assets/css/module.stylesheet-complete.min.css" />
  	<link rel="stylesheet" href=""""),_display_(Seq[Any](/*10.34*/routes/*10.40*/.Assets.at("app/bower_components/ng-file-upload/css/file-upload.css"))),format.raw/*10.109*/("""" />
  	
  	<link  href=""""),_display_(Seq[Any](/*12.18*/routes/*12.24*/.Assets.at("app/bower_components/angular-xeditable/dist/css/xeditable.css"))),format.raw/*12.99*/("""" rel="stylesheet">
  	<link  href=""""),_display_(Seq[Any](/*13.18*/routes/*13.24*/.Assets.at("app/bower_components/angular-bootstrap-datetimepicker/src/css/datetimepicker.css"))),format.raw/*13.118*/("""" rel="stylesheet"/>
  	
  	<link rel="icon" type="image/png" href=""""),_display_(Seq[Any](/*15.45*/routes/*15.51*/.Assets.at("app/images/favicon-2.ico"))),format.raw/*15.89*/("""">
    <link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*16.55*/routes/*16.61*/.Assets.at("app/images/favicon-2.ico"))),format.raw/*16.99*/("""">
    
  	<style type="text/css">
  		.widget-img-mini """),format.raw/*19.22*/("""{"""),format.raw/*19.23*/("""
  			width:50px;
  			height:50px;
  		"""),format.raw/*22.5*/("""}"""),format.raw/*22.6*/("""
  		.btn-mini """),format.raw/*23.15*/("""{"""),format.raw/*23.16*/("""
  			padding: 0px 3px 0px 3px !important;
  			font-size: smaller !important;
  		"""),format.raw/*26.5*/("""}"""),format.raw/*26.6*/("""
  		.error """),format.raw/*27.12*/("""{"""),format.raw/*27.13*/("""
			padding-top: 0px !important;
		"""),format.raw/*29.3*/("""}"""),format.raw/*29.4*/("""
  	
  	</style>
	
  </head>
  <body class="menu-right-hidden" ng-app="minibean" >
  <span us-spinner spinner-key="loading..." ></span>
  	<div class="container-fluid menu-hidden">
  		<div id="content" ng-view="">
    	
  		</div>
  	</div>
    
    <script src=""""),_display_(Seq[Any](/*42.19*/routes/*42.25*/.Assets.at("components/library/jquery/jquery.min.js"))),format.raw/*42.78*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*43.19*/routes/*43.25*/.Assets.at("app/bower_components/ng-file-upload/angular-file-upload-shim.min.js"))),format.raw/*43.106*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*44.19*/routes/*44.25*/.Assets.at("app/bower_components/angular/angular.js"))),format.raw/*44.78*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*45.19*/routes/*45.25*/.Assets.at("app/bower_components/angular-resource/angular-resource.js"))),format.raw/*45.96*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*46.19*/routes/*46.25*/.Assets.at("app/bower_components/angular-route/angular-route.js"))),format.raw/*46.90*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*47.19*/routes/*47.25*/.Assets.at("app/bower_components/ngInfiniteScroll/ng-infinite-scroll.js"))),format.raw/*47.98*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*48.19*/routes/*48.25*/.Assets.at("app/bower_components/angular-route/angular-route.js"))),format.raw/*48.90*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*49.19*/routes/*49.25*/.Assets.at("app/bower_components/angular-animate/angular-animate.min.js"))),format.raw/*49.98*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*50.19*/routes/*50.25*/.Assets.at("app/bower_components/angular-bootstrap/ui-bootstrap.js"))),format.raw/*50.93*/(""""></script>
	<script src=""""),_display_(Seq[Any](/*51.16*/routes/*51.22*/.Assets.at("app/bower_components/angular-bootstrap/ui-bootstrap-tpls.js"))),format.raw/*51.95*/(""""></script> 
	<script src=""""),_display_(Seq[Any](/*52.16*/routes/*52.22*/.Assets.at("app/bower_components/ng-file-upload/angular-file-upload.min.js"))),format.raw/*52.98*/(""""></script> 
	<script src=""""),_display_(Seq[Any](/*53.16*/routes/*53.22*/.Assets.at("app/bower_components/angular-xeditable/dist/js/xeditable.js"))),format.raw/*53.95*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*54.19*/routes/*54.25*/.Assets.at("app/bower_components/moment/moment.js"))),format.raw/*54.76*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*55.19*/routes/*55.25*/.Assets.at("app/bower_components/angular-validator/dist/angular-validator.js"))),format.raw/*55.103*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*56.19*/routes/*56.25*/.Assets.at("app/bower_components/angular-validator/dist/angular-validator-rules.js"))),format.raw/*56.109*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*57.19*/routes/*57.25*/.Assets.at("app/bower_components/spin.js/spin.js"))),format.raw/*57.75*/(""""></script>
	<script src=""""),_display_(Seq[Any](/*58.16*/routes/*58.22*/.Assets.at("app/bower_components/angular-spinner/angular-spinner.min.js"))),format.raw/*58.95*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*59.19*/routes/*59.25*/.Assets.at("app/bower_components/angular-truncate/dist/angular-truncate.min.js"))),format.raw/*59.105*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*60.19*/routes/*60.25*/.Assets.at("app/bower_components/tinymce/tinymce.min.js"))),format.raw/*60.82*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*61.19*/routes/*61.25*/.Assets.at("app/bower_components/angular-ui-tinymce/src/tinymce.js"))),format.raw/*61.93*/(""""></script>
    
    <script src=""""),_display_(Seq[Any](/*63.19*/routes/*63.25*/.Assets.at("components/library/bootstrap/js/bootstrap.min.js"))),format.raw/*63.87*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*64.19*/routes/*64.25*/.Assets.at("app/bower_components/angular-bootstrap-datetimepicker/src/js/datetimepicker.js"))),format.raw/*64.117*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*65.19*/routes/*65.25*/.Assets.at("components/plugins/ajaxify/script.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*65.98*/(""""></script>
    
	<script>var App = """),format.raw/*67.20*/("""{"""),format.raw/*67.21*/("""}"""),format.raw/*67.22*/(""";</script>
	<script data-id="App.Scripts">
		App.Scripts = """),format.raw/*69.17*/("""{"""),format.raw/*69.18*/("""
		
			/* CORE scripts always load first; */
			core: [
				'"""),_display_(Seq[Any](/*73.7*/routes/*73.13*/.Assets.at("components/library/jquery/jquery.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*73.85*/("""', 
				'"""),_display_(Seq[Any](/*74.7*/routes/*74.13*/.Assets.at("components/library/modernizr/modernizr.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*74.87*/("""'
			],
		
			/* PLUGINS_DEPENDENCY always load after CORE but before PLUGINS; */
			plugins_dependency: [
				'"""),_display_(Seq[Any](/*79.7*/routes/*79.13*/.Assets.at("components/library/bootstrap/js/bootstrap.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*79.94*/("""', 
				'"""),_display_(Seq[Any](/*80.7*/routes/*80.13*/.Assets.at("components/library/jquery/jquery-migrate.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*80.93*/("""'
			],
		
			/* PLUGINS always load after CORE and PLUGINS_DEPENDENCY, but before the BUNDLE / initialization scripts; */
			plugins: [
				'"""),_display_(Seq[Any](/*85.7*/routes/*85.13*/.Assets.at("components/plugins/nicescroll/jquery.nicescroll.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*85.100*/("""', 
				'"""),_display_(Seq[Any](/*86.7*/routes/*86.13*/.Assets.at("components/plugins/breakpoints/breakpoints.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*86.91*/("""', 
				//'"""),_display_(Seq[Any](/*87.9*/routes/*87.15*/.Assets.at("components/plugins/ajaxify/davis.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*87.87*/("""', 
				//'"""),_display_(Seq[Any](/*88.9*/routes/*88.15*/.Assets.at("components/plugins/ajaxify/jquery.lazyjaxdavis.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*88.101*/("""', 
				//'"""),_display_(Seq[Any](/*89.9*/routes/*89.15*/.Assets.at("components/plugins/preload/pace/pace.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*89.91*/("""', 
				'"""),_display_(Seq[Any](/*90.7*/routes/*90.13*/.Assets.at("components/plugins/menus/sidr/jquery.sidr.js?v=v1.0.0"))),format.raw/*90.80*/("""', 
				'"""),_display_(Seq[Any](/*91.7*/routes/*91.13*/.Assets.at("components/plugins/holder/holder.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*91.81*/("""', 
				'"""),_display_(Seq[Any](/*92.7*/routes/*92.13*/.Assets.at("components/common/forms/elements/bootstrap-select/assets/lib/js/bootstrap-select.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*92.129*/("""', 
				'"""),_display_(Seq[Any](/*93.7*/routes/*93.13*/.Assets.at("components/plugins/mixitup/jquery.mixitup.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*93.94*/("""', 
				'"""),_display_(Seq[Any](/*94.7*/routes/*94.13*/.Assets.at("components/plugins/less-js/less.min.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*94.84*/("""', 
				'"""),_display_(Seq[Any](/*95.7*/routes/*95.13*/.Assets.at("components/modules/admin/charts/flot/assets/lib/excanvas.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*95.105*/("""', 
				'"""),_display_(Seq[Any](/*96.7*/routes/*96.13*/.Assets.at("components/plugins/browser/ie/ie.prototype.polyfill.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*96.100*/("""'
			],
		
			/* The initialization scripts always load last and are automatically and dynamically loaded when AJAX navigation is enabled; */
			bundle: [
				//'"""),_display_(Seq[Any](/*101.9*/routes/*101.15*/.Assets.at("components/plugins/ajaxify/ajaxify.init.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*101.90*/("""', 
				'"""),_display_(Seq[Any](/*102.7*/routes/*102.13*/.Assets.at("components/core/js/sidebar.main.init.js?v=v1.0.0"))),format.raw/*102.75*/("""', 
				'"""),_display_(Seq[Any](/*103.7*/routes/*103.13*/.Assets.at("components/core/js/sidebar.collapse.init.js?v=v1.0.0"))),format.raw/*103.79*/("""', 
				'"""),_display_(Seq[Any](/*104.7*/routes/*104.13*/.Assets.at("components/common/forms/elements/bootstrap-select/assets/custom/js/bootstrap-select.init.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*104.137*/("""', 
				'"""),_display_(Seq[Any](/*105.7*/routes/*105.13*/.Assets.at("components/modules/admin/menus/menus.sidebar.chat.init.js?v=v1.0.0"))),format.raw/*105.93*/("""', 
				'"""),_display_(Seq[Any](/*106.7*/routes/*106.13*/.Assets.at("components/plugins/mixitup/mixitup.init.js?v=v1.0.0&sv=v0.0.1"))),format.raw/*106.88*/("""', 
				'"""),_display_(Seq[Any](/*107.7*/routes/*107.13*/.Assets.at("components/core/js/core.init.js?v=v1.0.0"))),format.raw/*107.67*/("""'
			]
		
		"""),format.raw/*110.3*/("""}"""),format.raw/*110.4*/(""";
	</script>

	<script>
		$script(App.Scripts.core, 'core');
		
		$script.ready(['core'], function()"""),format.raw/*116.37*/("""{"""),format.raw/*116.38*/("""
			$script(App.Scripts.plugins_dependency, 'plugins_dependency');
		"""),format.raw/*118.3*/("""}"""),format.raw/*118.4*/(""");
		$script.ready(['core', 'plugins_dependency'], function()"""),format.raw/*119.59*/("""{"""),format.raw/*119.60*/("""
			$script(App.Scripts.plugins, 'plugins');
		"""),format.raw/*121.3*/("""}"""),format.raw/*121.4*/(""");
		$script.ready(['core', 'plugins_dependency', 'plugins'], function()"""),format.raw/*122.70*/("""{"""),format.raw/*122.71*/("""
			$script(App.Scripts.bundle, 'bundle');
		"""),format.raw/*124.3*/("""}"""),format.raw/*124.4*/(""");
	</script>
	
	
    <script src=""""),_display_(Seq[Any](/*128.19*/routes/*128.25*/.Assets.at("app/scripts/app.js"))),format.raw/*128.57*/(""""></script>
    <script src=""""),_display_(Seq[Any](/*129.19*/routes/*129.25*/.Assets.at("app/scripts/controllers/main.js"))),format.raw/*129.70*/(""""></script>
</body>

	<script data-id="App.Config">
		var basePath = '',
		commonPath = '"""),_display_(Seq[Any](/*134.18*/routes/*134.24*/.Assets.at(""))),format.raw/*134.38*/("""',
		rootPath = '../',
		DEV = false,
		componentsPath = '"""),_display_(Seq[Any](/*137.22*/routes/*137.28*/.Assets.at("components/"))),format.raw/*137.53*/("""';
	
	var primaryColor = '#25ad9f',
		dangerColor = '#b55151',
		successColor = '#609450',
		infoColor = '#4a8bc2',
		warningColor = '#ab7a4b',
		inverseColor = '#45484d';
	
	var themerPrimaryColor = primaryColor;

		App.Config = """),format.raw/*148.16*/("""{"""),format.raw/*148.17*/("""
		ajaxify_menu_selectors: ['#menu'],
		ajaxify_layout_app: false	"""),format.raw/*150.29*/("""}"""),format.raw/*150.30*/(""";
	</script>
</html>
"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Wed Aug 06 17:56:29 IST 2014
                    SOURCE: C:/Documents and Settings/User/MB-Admin/app/views/home.scala.html
                    HASH: d592cdec3937f90086825f7ed8401dd7f36c76e2
                    MATRIX: 786->0|1196->374|1211->380|1303->449|1367->477|1382->483|1479->558|1553->596|1568->602|1685->696|1792->767|1807->773|1867->811|1961->869|1976->875|2036->913|2123->972|2152->973|2222->1016|2250->1017|2294->1033|2323->1034|2436->1120|2464->1121|2505->1134|2534->1135|2598->1172|2626->1173|2940->1451|2955->1457|3030->1510|3097->1541|3112->1547|3216->1628|3283->1659|3298->1665|3373->1718|3440->1749|3455->1755|3548->1826|3615->1857|3630->1863|3717->1928|3784->1959|3799->1965|3894->2038|3961->2069|3976->2075|4063->2140|4130->2171|4145->2177|4240->2250|4307->2281|4322->2287|4412->2355|4476->2383|4491->2389|4586->2462|4651->2491|4666->2497|4764->2573|4829->2602|4844->2608|4939->2681|5006->2712|5021->2718|5094->2769|5161->2800|5176->2806|5277->2884|5344->2915|5359->2921|5466->3005|5533->3036|5548->3042|5620->3092|5684->3120|5699->3126|5794->3199|5861->3230|5876->3236|5979->3316|6046->3347|6061->3353|6140->3410|6207->3441|6222->3447|6312->3515|6385->3552|6400->3558|6484->3620|6551->3651|6566->3657|6681->3749|6748->3780|6763->3786|6858->3859|6924->3897|6953->3898|6982->3899|7071->3960|7100->3961|7201->4027|7216->4033|7310->4105|7356->4116|7371->4122|7467->4196|7620->4314|7635->4320|7738->4401|7784->4412|7799->4418|7901->4498|8084->4646|8099->4652|8209->4739|8255->4750|8270->4756|8370->4834|8418->4847|8433->4853|8527->4925|8575->4938|8590->4944|8699->5030|8747->5043|8762->5049|8860->5125|8906->5136|8921->5142|9010->5209|9056->5220|9071->5226|9161->5294|9207->5305|9222->5311|9361->5427|9407->5438|9422->5444|9525->5525|9571->5536|9586->5542|9679->5613|9725->5624|9740->5630|9855->5722|9901->5733|9916->5739|10026->5826|10230->5994|10246->6000|10344->6075|10391->6086|10407->6092|10492->6154|10539->6165|10555->6171|10644->6237|10691->6248|10707->6254|10855->6378|10902->6389|10918->6395|11021->6475|11068->6486|11084->6492|11182->6567|11229->6578|11245->6584|11322->6638|11365->6653|11394->6654|11529->6760|11559->6761|11658->6832|11687->6833|11778->6895|11808->6896|11885->6945|11914->6946|12016->7019|12046->7020|12121->7067|12150->7068|12227->7108|12243->7114|12298->7146|12366->7177|12382->7183|12450->7228|12582->7323|12598->7329|12635->7343|12734->7405|12750->7411|12798->7436|13068->7677|13098->7678|13195->7746|13225->7747
                    LINES: 29->1|38->10|38->10|38->10|40->12|40->12|40->12|41->13|41->13|41->13|43->15|43->15|43->15|44->16|44->16|44->16|47->19|47->19|50->22|50->22|51->23|51->23|54->26|54->26|55->27|55->27|57->29|57->29|70->42|70->42|70->42|71->43|71->43|71->43|72->44|72->44|72->44|73->45|73->45|73->45|74->46|74->46|74->46|75->47|75->47|75->47|76->48|76->48|76->48|77->49|77->49|77->49|78->50|78->50|78->50|79->51|79->51|79->51|80->52|80->52|80->52|81->53|81->53|81->53|82->54|82->54|82->54|83->55|83->55|83->55|84->56|84->56|84->56|85->57|85->57|85->57|86->58|86->58|86->58|87->59|87->59|87->59|88->60|88->60|88->60|89->61|89->61|89->61|91->63|91->63|91->63|92->64|92->64|92->64|93->65|93->65|93->65|95->67|95->67|95->67|97->69|97->69|101->73|101->73|101->73|102->74|102->74|102->74|107->79|107->79|107->79|108->80|108->80|108->80|113->85|113->85|113->85|114->86|114->86|114->86|115->87|115->87|115->87|116->88|116->88|116->88|117->89|117->89|117->89|118->90|118->90|118->90|119->91|119->91|119->91|120->92|120->92|120->92|121->93|121->93|121->93|122->94|122->94|122->94|123->95|123->95|123->95|124->96|124->96|124->96|129->101|129->101|129->101|130->102|130->102|130->102|131->103|131->103|131->103|132->104|132->104|132->104|133->105|133->105|133->105|134->106|134->106|134->106|135->107|135->107|135->107|138->110|138->110|144->116|144->116|146->118|146->118|147->119|147->119|149->121|149->121|150->122|150->122|152->124|152->124|156->128|156->128|156->128|157->129|157->129|157->129|162->134|162->134|162->134|165->137|165->137|165->137|176->148|176->148|178->150|178->150
                    -- GENERATED --
                */
            