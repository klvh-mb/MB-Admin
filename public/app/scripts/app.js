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
        templateUrl: '/assets/app/views/manageArticlesPage.html',
        controller : 'ManageArticlesController'
      })
      .when('/manageArticles',{
    	templateUrl: '/assets/app/views/manageArticlesPage.html',
    	controller : 'ManageArticlesController'
      })	  
      .when('/article/create',{
    	templateUrl: '/assets/app/views/createArticlePage.html',
    	controller : 'CreateArticleController'  
      })
      .when('/article/edit/:id',{
    	templateUrl: '/assets/app/views/editArticlePage.html',
    	controller : 'EditArticleController'  
      })
      .when('/manageCampaigns',{
        templateUrl: '/assets/app/views/manageCampaignsPage.html',
        controller : 'ManageCampaignsController'  
      })
      .when('/campaign/create',{
        templateUrl: '/assets/app/views/createCampaignPage.html',
        controller : 'CreateCampaignController'  
      })
      .when('/campaign/edit/:id',{
        templateUrl: '/assets/app/views/editCampaignPage.html',
        controller : 'EditCampaignController'  
      })
      .when('/manageAnnouncements',{
    	templateUrl: '/assets/app/views/manageAnnouncements.html',
    	controller : 'ManageAnnouncementsController'  
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
      .when('/manageSubscriptions',{
    	templateUrl: '/assets/app/views/manageSubscription.html',
    	controller : 'ManageSubscriptionsController'  
      })
      .when('/managePhotoUpload',{
    	templateUrl: '/assets/app/views/managePhotoUpload.html',
    	controller : 'ManagePhotoUploadController'  
      })
      .when('/manageGameAccount',{
    	templateUrl: '/assets/app/views/manageGameAccount.html',
    	controller : 'ManageGameAccountController'  
      })
      .when('/manageRedemption',{
    	templateUrl: '/assets/app/views/manageRedemption.html',
    	controller : 'ManageRedemptionController'  
      })
      .otherwise({
          redirectTo: '/'
      });
  })
  .run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

