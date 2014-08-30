'use strict';

angular.module('minibean', [
  'infinite-scroll',
  'ngResource',
  'ngRoute',
  'xeditable',
  'ngAnimate',
  'ui.bootstrap',
  'ui.bootstrap.tpls',
  'angularFileUpload',
  'ui.bootstrap.datetimepicker',
  'validator',
  'validator.rules',
  'angularSpinner',
  'truncate',
  'ui.tinymce'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/home.html'
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/articlePage.html',
    	controller : 'ViewArticleController'  
      })
      .when('/article/create',{
    	templateUrl: '/assets/app/views/createArticlePage.html',
    	controller : 'CreateArticleController'  
      })
      .when('/article/edit/:id',{
    	templateUrl: '/assets/app/views/editArticlePage.html',
    	controller : 'EditArticleController'  
      })
      .when('/article/show',{
    	templateUrl: '/assets/app/views/showArticlesPage.html',
    	controller : 'ShowArticleController'  
      })
      .when('/manageAnnouncement',{
    	templateUrl: '/assets/app/views/manageAnnouncements.html',
    	controller : 'ShowAnnouncementController'  
      })
      .when('/managePosts',{
    	templateUrl: '/assets/app/views/managePosts.html',
    	controller : 'ManagePostsController'  
      })
      .when('/manageQuestions',{
    	templateUrl: '/assets/app/views/manageQuestions.html',
    	controller : 'ManageQuestionsController'  
      })
      .when('/manageComments',{
    	templateUrl: '/assets/app/views/manageComments.html',
    	controller : 'ManageCommentsController'  
      })
      .when('/manageAnswers',{
    	templateUrl: '/assets/app/views/manageAnswers.html',
    	controller : 'ManageAnswersController'  
      })
      .when('/manageCommunities',{
    	templateUrl: '/assets/app/views/manageCommunities.html',
    	controller : 'ManageCommunitiesController'  
      })
      .when('/manageUsers',{
    	templateUrl: '/assets/app/views/manageUsers.html',
    	controller : 'ManageUsersController'  
      })
      .when('/manageUsers2',{
    	templateUrl: '/assets/app/views/manageUsers2.html',
    	controller : 'ManageUsers2Controller'  
      })
      .when('/manageOperations',{
    	templateUrl: '/assets/app/views/operations.html',
    	controller : 'ManageOperationsController'  
      })
      .otherwise({
          redirectTo: '/'
      });
  })
  .run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

