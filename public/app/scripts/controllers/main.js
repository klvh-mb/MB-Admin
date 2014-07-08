'use strict';

var minibean = angular.module('minibean');

minibean.controller('CreateArticleController', function($scope, $http, $location, articleCategoryService, locationService, usSpinnerService){
    $scope.article;
    $scope.submitBtn = "Save";
    
    //Refer to http://www.tinymce.com/
    $scope.tinymceOptions = {
            selector: "textarea",
            plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table contextmenu paste"
            ],
            toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
    }
    
    $scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
    $scope.targetLocations = locationService.getAllDistricts.get();
    
    $scope.select_targetLocation = function(id, name) {
        $scope.targetLocation_id = id;
        $scope.targetLocation_name = name;
        $scope.formData.targetLocation_id = id;
        $scope.isLocationChosen = true;
    }
    
    $scope.select_category = function(id, name, pn) {
        $scope.category_id = id;
        $scope.category_picture = pn;
        $scope.category_name = name;
        $scope.formData.category_id = id;
        $scope.isChosen = true;
    }
    
    $scope.submit = function() {
        usSpinnerService.spin('loading...');
                $http.post('/createArticle', $scope.formData).success(function(data){
                    $scope.submitBtn = "Done";
                    $scope.uniqueName = false;
                    $scope.targetLocationNotChoose = false;
                    $scope.categoryNotChoose = false;
                    $scope.TargetAgeNotSelected = false;
                    $scope.TargetAgeCondition = false;
                    usSpinnerService.stop('loading...');
                    $location.path('/');
                }).error(function(data, status, headers, config) {
                    if( status == 505 ) {
                        $scope.uniqueName = true;
                        usSpinnerService.stop('loading...');
                        $scope.submitBtn = "Try Again";
                    }  
                    if( status == 506 ) {
                        $scope.categoryNotChoose = true;
                        usSpinnerService.stop('loading...');
                        $scope.submitBtn = "Try Again";
                    }
                    if(status == 507){
                        $scope.TargetAgeNotSelected = true;
                        usSpinnerService.stop('loading...');
                        $scope.submitBtn = "Try Again";
                    }
                    if(status == 508){
                        $scope.TargetAgeCondition = true;
                        usSpinnerService.stop('loading...');
                        $scope.submitBtn = "Try Again";
                    }
                    if(status == 509){
                        $scope.targetLocationNotChoose = true;
                        usSpinnerService.stop('loading...');
                        $scope.submitBtn = "Try Again";
                    }
                });
    }
});

minibean.service('locationService',function($resource){
    this.getAllDistricts = $resource(
            '/getAllDistricts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibean.service('articleCategoryService',function($resource){
    this.getAllArticleCategory = $resource(
            '/getAllArticleCategory',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});


minibean.service('allArticlesService',function($resource){
    this.AllArticles = $resource(
            '/get-all-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibean.service('getDescriptionService',function($resource){
    this.GetDescription = $resource(
            '/getDescriptionOdArticle/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteArticleService',function($resource){
    this.DeleteArticle = $resource(
            '/deleteArticle/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ShowArticleController',function($scope, $modal, deleteArticleService, allArticlesService, getDescriptionService){
    $scope.result = allArticlesService.AllArticles.get();
    
    $scope.open = function (id) {
        var modalInstance = $modal.open({
          templateUrl: 'myModalContent.html',
        });
        var msg = getDescriptionService.GetDescription.get({id:id}  , function(data) {
            console.log(data.description);
            $('#showDescription').html(data.description);
        });
        
      };

      $scope.deleteArticle = function (id){
          console.log($scope.deleteID);
          deleteArticleService.DeleteArticle.get({id :id}, function(data){
              $('#myModal').modal('hide');
              angular.forEach($scope.result, function(request, key){
                    if(request.id == id) {
                        $scope.result.splice($scope.result.indexOf(request),1);
                    }
                });
              
          });
      }
      
      $scope.assignDeleteId = function(id) {
          $scope.deleteID = id;
      }
      
});


minibean.service('ArticleService',function($resource){
    this.ArticleInfo = $resource(
            '/Article/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});


minibean.controller('EditArticleController',function($scope, $http, $routeParams, $location, $upload, ArticleService, articleCategoryService, locationService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.article = ArticleService.ArticleInfo.get({id:$routeParams.id});
    $scope.articleCategorys = articleCategoryService.getAllArticleCategory.get();
    $scope.targetLocations = locationService.getAllDistricts.get();
    
    var range = [];
    for(var i=0;i<100;i++) {
          range.push(i);
    }
    $scope.targetAge = range;
    
    $scope.tinymceOptions = {
            selector: "textarea",
            plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table contextmenu paste"
            ],
            toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
    }
    
    $scope.select_targetLocation = function(id, name) {
        $scope.article.targetLocation.id = id;
        $scope.article.targetLocation.displayName = name;
        $scope.isLocationChosen = true;
    }
    
    $scope.select_category = function(id, name, pn) {
        $scope.article.category.id = id;
        $scope.article.category.name = name;
        $scope.article.category.pictureName = pn;
        $scope.isChosen = true;
    }
    
    $scope.updateArticleData = function(data) {
        $scope.TargetAgeCondition = false;
        usSpinnerService.spin('loading...');
        return $http.post('/editArticle', $scope.article).success(function(data){
            $scope.submitBtn = "Done";
            usSpinnerService.stop('loading...');
            $location.path('/');
        }).error(function(data, status, headers, config) {
            if(status == 509){
                $scope.TargetAgeCondition = true;
                usSpinnerService.stop('loading...');
                $scope.submitBtn = "Try Again";
            }

        });
    }
    
    
    
    $scope.selectedFiles;
    $scope.dataUrls;
    $scope.path;
    $scope.tempSelectedFiles;
    $scope.onFileSelect = function($files) {
        alert($files);
        if($scope.selectedFiles == 0) {
            $scope.tempSelectedFiles = 0;
        }
        
        $scope.selectedFiles = $files;
        $scope.tempSelectedFiles = $files;
        var $file = $files;
        if (window.FileReader && $file.type > -1) {
            var fileReader = new FileReader();
            fileReader.readAsDataURL($files);
            var loadFile = function(fileReader, index) {
                fileReader.onload = function(e) {
                    $timeout(function() {
                        $scope.dataUrls = e.target.result;
                    });
                }
            }(fileReader, 0);
        }
        $upload.upload({
            url : '/image/scImage',
            method: $scope.httpMethod,
            file: $scope.tempSelectedFiles,
            fileFormDataName: 'url-photo'
        }).success(function(data, status, headers, config) {
            usSpinnerService.stop('loading...');
            alert("ASDFE :: "+data.URL);
            $scope.path = data.URL;
        });
    }
    
   
        
        
    
    
    
});

      




