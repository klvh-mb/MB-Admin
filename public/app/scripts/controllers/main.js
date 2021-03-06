'use strict';

var minibean = angular.module('minibean');

minibean.service('campaignService',function($resource){
    this.latestCampaigns = $resource(
            '/get-latest-campaigns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.searchCampaigns = $resource(
            '/search-campaigns/:id/:name',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.getCampaign = $resource(
            '/get-campaign/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.campaignInfo = $resource(
            '/get-campaign-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getDescription = $resource(
            '/get-campaign-description/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.deleteCampaign = $resource(
            '/delete-campaign/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.campaignWinners = $resource(
            '/get-campaign-winners/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.searchCampaignWinners = $resource(
            '/search-campaign-winners/:id/:positions',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.notifyWinners = $resource(
            '/notify-winners/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('gameGiftService',function($resource){
    this.latestGameGifts = $resource(
            '/get-latest-game-gifts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.searchGameGifts = $resource(
            '/search-game-gifts/:id/:name',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.getGameGift = $resource(
            '/get-game-gift/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.gameGiftInfo = $resource(
            '/get-game-gift-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getDescription = $resource(
            '/get-game-gift-description/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.deleteGameGift = $resource(
            '/delete-game-gift/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageCampaignWinnersController',function($scope, $routeParams, $http, $modal, campaignService){

    $scope.positions = "";

    $scope.campaign = campaignService.campaignInfo.get({id:$routeParams.id});
    
    $scope.winners = campaignService.campaignWinners.get({id:$routeParams.id}, 
        function(data) {
            
        });

    $scope.searchCampaignWinners = function(positions) {
        $scope.positions = positions;
          
        if(angular.isUndefined($scope.positions) || $scope.positions == "") {
            $scope.positions = " ";
        }
          
        $scope.winners = campaignService.searchCampaignWinners.get({id:$routeParams.id,positions:$scope.positions});
    };
    
    $scope.popupChangeWinnerStateModal = function(winner) {
        $scope.winnerInModal = winner;
        $scope.winnerStateInModal = winner.winnerState;
        $scope.winnerNoteInModal = "";
    };
    
    $scope.changeWinnerState = function() {
        var formData = {
            "id" : $scope.winnerInModal.id,
            "userId" : $scope.winnerInModal.userId,
            "campaignId" : $scope.winnerInModal.campaignId,
            "winnerState" : $scope.winnerStateInModal,
            "note" : $scope.winnerNoteInModal
        };
        var thisFormData = formData;
        
        $http.post('/change-winner-state', formData).success(function(data){
            $('#changeWinnerStateModal').modal('hide');
            $scope.winnerInModal.id = data.id;
            $scope.winnerInModal.winnerState = data.winnerState;
            $scope.winnerInModal.note = data.note;
        }).error(function(data, status, headers, config) {
            alert('Failed to change state for winner ['+$scope.winnerInModal.id+'|'+$scope.winnerInModal.name+'] from '+$scope.winnerInModal.winnerState+' to '+$scope.winnerStateInModal);
        });
    };
});

minibean.controller('ManageCampaignsController',function($scope, $http, $modal, campaignService){
    $scope.searchById = "";
    $scope.searchByName = "";
    
    $scope.campaigns = campaignService.latestCampaigns.get();
    
    $scope.open = function (id) {
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
        });
        var msg = campaignService.getDescription.get({id:id}, 
            function(data) {
                $('#showDescription').html(data.description);
            });
    };

    $scope.searchCampaigns = function(searchById,searchByName) {
        $scope.searchById = searchById;
        $scope.searchByName = searchByName;
          
        if(angular.isUndefined($scope.searchById) || $scope.searchById=="") {
            $scope.searchById = " ";
        }
          
        if(angular.isUndefined($scope.searchByName) || $scope.searchByName=="") {
            $scope.searchByName = " ";
        }
          
        $scope.campaigns = campaignService.searchCampaigns.get({id:$scope.searchById,name:$scope.searchByName});
    };
    
    $scope.popupCampaignStateModal = function(campaign) {
        $scope.campaignInModal = campaign;
        $scope.campaignStateInModal = campaign.cs;
    };
    
    $scope.changeCampaignState = function() {
        var formData = {
            "id" : $scope.campaignInModal.id,
            "campaignState" : $scope.campaignStateInModal
        };
        var thisFormData = formData;
        
        $http.post('/change-campaign-state', formData).success(function(data){
            $('#changeCampaignStateModal').modal('hide');
            $scope.campaignInModal.cs = data.cs;
        }).error(function(data, status, headers, config) {
            alert('Failed to change state for campaign ['+$scope.campaignInModal.id+'|'+$scope.campaignInModal.nm+'] from '+$scope.campaignInModal.cs+' to '+$scope.campaignStateInModal);
        });
    };
    
    $scope.deleteCampaign = function(id) {
        campaignService.deleteCampaign.get({id:id}, function(data){
            $('#myModal').modal('hide');
            angular.forEach($scope.campaigns, function(request, key){
                if(request.id == id) {
                    $scope.campaigns.splice($scope.campaigns.indexOf(request),1);
                }
            });
        });
    }

    $scope.setDeleteId = function(id) {
        $scope.deleteId = id;
    }
    
    $scope.notifyWinners = function (id){
        campaignService.notifyWinners.get({id:id}, function(data){
            $('#notifyModal').modal('hide');
            alert(data.status+" - "+data.message);
        });
    }
      
    $scope.assignNotifyId = function(id) {
        $scope.notifyID = id;
    }
});

minibean.controller('CreateCampaignController', function($scope, $http, $location, usSpinnerService){
    $scope.submitBtn = "Save";
    
    //Refer to http://www.tinymce.com/
    $scope.tinymceOptions = {
            selector: "textarea.editor",
            plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table contextmenu paste"
            ],
            toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
    }
    
    $scope.createCampaign = function() {
        $scope.uniqueName = false;
        $scope.campaignTypeNotChoose = false;
        $scope.startEndDateNotEntered = false
        $scope.startEndDateCondition = false;
        usSpinnerService.spin('loading...');
                $http.post('/create-campaign', $scope.formData).success(function(data){
                    $scope.submitBtn = "Done";
                    usSpinnerService.stop('loading...');
                    $location.path('/manageCampaigns');
                }).error(function(data, status, headers, config) {
                    if(status == 505) {
                        $scope.uniqueName = true;
                        usSpinnerService.stop('loading...');
                    }  
                    if(status == 506) {
                        $scope.campaignTypeNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 507){
                        $scope.startEndDateNotEntered = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 508){
                        $scope.startEndDateCondition = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 509) {
                        $scope.announcementTypeNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                });
    }
});

minibean.controller('EditCampaignController',function($scope, $http, $filter, $routeParams, $location, $upload, campaignService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.campaign = campaignService.getCampaign.get({id:$routeParams.id},
        function(data) {
            //$scope.startDateStr = $filter('date')($scope.campaign.startDate, 'MM-dd-yyyy hh:mm a');
            //$scope.endDateStr = $filter('date')($scope.campaign.endDate, 'MM-dd-yyyy hh:mm a');
        });
    
    $scope.tinymceOptions = {
            selector: "textarea.editor",
            plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table contextmenu paste"
            ],
            toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
    }
    
    $scope.updateCampaignData = function(data) {
        $scope.startEndDateCondition = false;
        $scope.startEndDateNotEntered = false
        usSpinnerService.spin('loading...');
        return $http.post('/edit-campaign', $scope.campaign).success(function(data){
            $scope.submitBtn = "Done";
            usSpinnerService.stop('loading...');
            $location.path('/manageCampaigns');
        }).error(function(data, status, headers, config) {
            if(status == 507){
                $scope.startEndDateNotEntered = true;
                usSpinnerService.stop('loading...');
            }
            if(status == 508){
                $scope.startEndDateCondition = true;
                usSpinnerService.stop('loading...');
            }
        });
    }
    
    $scope.selectedFiles;
    $scope.dataUrls;
    $scope.path;
    $scope.tempSelectedFiles;
    $scope.onFileSelect = function($files) {
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
            url : '/image/campaign/upload',
            method: $scope.httpMethod,
            file: $scope.tempSelectedFiles,
            fileFormDataName: 'url-photo'
        }).success(function(data, status, headers, config) {
            usSpinnerService.stop('loading...');
            $scope.path = data.URL;
        });
    }
});

minibean.controller('ManageGameGiftsController',function($scope, $http, $modal, gameGiftService){
    $scope.searchById = "";
    $scope.searchByName = "";
    
    $scope.gameGifts = gameGiftService.latestGameGifts.get();
    
    $scope.open = function (id) {
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
        });
        var msg = gameGiftService.getDescription.get({id:id}, 
            function(data) {
                $('#showDescription').html(data.description);
            });
    };

    $scope.searchGameGifts = function(searchById,searchByName) {
        $scope.searchById = searchById;
        $scope.searchByName = searchByName;
          
        if(angular.isUndefined($scope.searchById) || $scope.searchById=="") {
            $scope.searchById = " ";
        }
          
        if(angular.isUndefined($scope.searchByName) || $scope.searchByName=="") {
            $scope.searchByName = " ";
        }
          
        $scope.gameGifts = gameGiftService.searchGameGifts.get({id:$scope.searchById,name:$scope.searchByName});
    };
    
    $scope.popupGameGiftStateModal = function(gameGift) {
        $scope.gameGiftInModal = gameGift;
        $scope.giftStateInModal = gameGift.gs;
    };
    
    $scope.changeGameGiftState = function() {
        var formData = {
            "id" : $scope.gameGiftInModal.id,
            "giftState" : $scope.giftStateInModal
        };
        var thisFormData = formData;
        
        $http.post('/change-game-gift-state', formData).success(function(data){
            $('#changeGameGiftStateModal').modal('hide');
            $scope.gameGiftInModal.gs = data.gs;
        }).error(function(data, status, headers, config) {
            alert('Failed to change state for gameGift ['+$scope.gameGiftInModal.id+'|'+$scope.gameGiftInModal.nm+'] from '+$scope.gameGiftInModal.gs+' to '+$scope.giftStateInModal);
        });
    };
    
    $scope.deleteGameGift = function(id) {
    	gameGiftService.deleteGameGift.get({id:id}, function(data){
            $('#myModal').modal('hide');
            angular.forEach($scope.gameGifts, function(request, key){
                if(request.id == id) {
                    $scope.gameGifts.splice($scope.gameGifts.indexOf(request),1);
                }
            });
        });
    }

    $scope.setDeleteId = function(id) {
        $scope.deleteId = id;
    }
    
    $scope.notifyWinners = function (id){
    	gameGiftService.notifyWinners.get({id:id}, function(data){
            $('#notifyModal').modal('hide');
            alert(data.status+" - "+data.message);
        });
    }
      
    $scope.assignNotifyId = function(id) {
        $scope.notifyID = id;
    }
});

minibean.controller('CreateGameGiftController', function($scope, $http, $location, usSpinnerService){
    $scope.submitBtn = "Save";
    
    $scope.createGameGift = function() {
        $scope.featureTypeNotChoose = false;
        $scope.redeemTypeNotChoose = false;
        $scope.giftTypeNotChoose = false;
        $scope.startEndDateNotEntered = false
        $scope.startEndDateCondition = false;
        usSpinnerService.spin('loading...');
                $http.post('/create-game-gift', $scope.formData).success(function(data){
                    $scope.submitBtn = "Done";
                    usSpinnerService.stop('loading...');
                    $location.path('/manageGameGifts');
                }).error(function(data, status, headers, config) {
                    if(status == 504) {
                    	$scope.featureTypeNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 505) {
                    	$scope.redeemTypeNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 506) {
                        $scope.giftTypeNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 507){
                        $scope.startEndDateNotEntered = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 508){
                        $scope.startEndDateCondition = true;
                        usSpinnerService.stop('loading...');
                    }
                });
    }
});

minibean.controller('EditGameGiftController',function($scope, $http, $filter, $routeParams, $location, $upload, gameGiftService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.gameGift = gameGiftService.getGameGift.get({id:$routeParams.id},
        function(data) {
            //$scope.startDateStr = $filter('date')($scope.gameGift.startDate, 'MM-dd-yyyy hh:mm a');
            //$scope.endDateStr = $filter('date')($scope.gameGift.endDate, 'MM-dd-yyyy hh:mm a');
        });
    
    $scope.updateGameGiftData = function(data) {
        $scope.startEndDateCondition = false;
        $scope.startEndDateNotEntered = false
        usSpinnerService.spin('loading...');
        return $http.post('/edit-game-gift', $scope.gameGift).success(function(data){
            $scope.submitBtn = "Done";
            usSpinnerService.stop('loading...');
            $location.path('/manageGameGifts');
        }).error(function(data, status, headers, config) {
            if(status == 507){
                $scope.startEndDateNotEntered = true;
                usSpinnerService.stop('loading...');
            }
            if(status == 508){
                $scope.startEndDateCondition = true;
                usSpinnerService.stop('loading...');
            }
        });
    }
    
    $scope.selectedFiles;
    $scope.dataUrls;
    $scope.path;
    $scope.tempSelectedFiles;
    $scope.onFileSelect = function($files) {
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
            url : '/image/game/upload',
            method: $scope.httpMethod,
            file: $scope.tempSelectedFiles,
            fileFormDataName: 'url-photo'
        }).success(function(data, status, headers, config) {
            usSpinnerService.stop('loading...');
            $scope.path = data.URL;
        });
    }
});

minibean.service('locationService',function($resource){
    this.getAllDistricts = $resource(
            '/get-all-districts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});
    
minibean.service('articleService',function($resource){
    this.getAllArticleCategories = $resource(
            '/get-all-article-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.latestArticles = $resource(
            '/get-latest-articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.searchArticles = $resource(
            '/search-articles/:id/:name',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.getArticle = $resource(
            '/get-article/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getDescription = $resource(
            '/get-article-description/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.deleteArticle = $resource(
            '/delete-article/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageArticlesController',function($scope, $modal, articleService){
	$scope.searchById = " ";
    $scope.searchByName = " ";
    
	$scope.result = articleService.latestArticles.get();
    
    $scope.open = function (id) {
        var modalInstance = $modal.open({
          templateUrl: 'myModalContent.html',
        });
        var msg = articleService.getDescription.get({id:id}, function(data) {
            $('#showDescription').html(data.description);
        });
      };

      $scope.searchArticles = function(searchById,searchByName) {
    	  $scope.searchById = searchById;
    	  $scope.searchByName = searchByName;
    	  
    	  if(angular.isUndefined($scope.searchById) || $scope.searchById=="") {
  			$scope.searchById = " ";
  		  }
    	  
    	  if(angular.isUndefined($scope.searchByName) || $scope.searchByName=="") {
    			$scope.searchByName = " ";
    	  }
    	  
    	  $scope.result = articleService.searchArticles.get({id:$scope.searchById,name:$scope.searchByName});
      };
      
      $scope.deleteArticle = function(id) {
          articleService.deleteArticle.get({id:id}, function(data){
              $('#myModal').modal('hide');
              angular.forEach($scope.result, function(request, key){
                    if(request.id == id) {
                        $scope.result.splice($scope.result.indexOf(request),1);
                    }
                });
              
          });
      }
      
      $scope.setDeleteId = function(id) {
          $scope.deleteId = id;
      }
});

minibean.controller('CreateArticleController', function($scope, $http, $location, articleService, locationService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.articleCategories = articleService.getAllArticleCategories.get();
    $scope.targetLocations = locationService.getAllDistricts.get();
    
    //Refer to http://www.tinymce.com/
    $scope.tinymceOptions = {
            selector: "textarea.editor",
            plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table contextmenu paste"
            ],
            toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
    }
    
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
                $http.post('/create-article', $scope.formData).success(function(data){
                    $scope.submitBtn = "Done";
                    $scope.uniqueName = false;
                    $scope.targetLocationNotChoose = false;
                    $scope.categoryNotChoose = false;
                    $scope.targetAgeNotSelected = false;
                    $scope.targetAgeCondition = false;
                    usSpinnerService.stop('loading...');
                    $location.path('/#/manageArticles');
                }).error(function(data, status, headers, config) {
                    if( status == 505 ) {
                        $scope.uniqueName = true;
                        usSpinnerService.stop('loading...');
                    }  
                    if( status == 506 ) {
                        $scope.categoryNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 507){
                        $scope.targetAgeNotSelected = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 508){
                        $scope.targetAgeCondition = true;
                        usSpinnerService.stop('loading...');
                    }
                    if(status == 509){
                        $scope.targetLocationNotChoose = true;
                        usSpinnerService.stop('loading...');
                    }
                });
    }
});

minibean.controller('EditArticleController',function($scope, $http, $routeParams, $location, $upload, articleService, locationService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.article = articleService.getArticle.get({id:$routeParams.id});
    $scope.articleCategories = articleService.getAllArticleCategories.get();
    $scope.targetLocations = locationService.getAllDistricts.get();
    
    var range = [];
    for(var i=0;i<100;i++) {
        range.push(i);
    }
    $scope.targetAge = range;
    
    $scope.tinymceOptions = {
            selector: "textarea.editor",
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
        $scope.targetAgeCondition = false;
        usSpinnerService.spin('loading...');
        return $http.post('/edit-article', $scope.article).success(function(data){
            $scope.submitBtn = "Done";
            usSpinnerService.stop('loading...');
            $location.path('/#/manageArticles');
        }).error(function(data, status, headers, config) {
            if(status == 509){
                $scope.targetAgeCondition = true;
                usSpinnerService.stop('loading...');
            }

        });
    }
    
    $scope.selectedFiles;
    $scope.dataUrls;
    $scope.path;
    $scope.tempSelectedFiles;
    $scope.onFileSelect = function($files) {
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
            url : '/image/article/upload',
            method: $scope.httpMethod,
            file: $scope.tempSelectedFiles,
            fileFormDataName: 'url-photo'
        }).success(function(data, status, headers, config) {
            usSpinnerService.stop('loading...');
            $scope.path = data.URL;
        });
    }
});

minibean.controller('ManagePKViewsController',function($scope, $modal, $http, $filter, pkViewsService){
    $scope.name = " ";
    $scope.formData = "";
    
    $scope.pkviews = pkViewsService.PKViews.get();
    
    $scope.setData = function(pkview) {
        $scope.pkviewData = pkview;
    };
    $scope.resetData = function() {
        $scope.formData = "";
    }
    $scope.setDeleteId = function(id) {
        $scope.deleteId = id;
    };
    $scope.savePKView = function() {
        $http.post('/savePKView', $scope.formData).success(function(data){
            $scope.pkviews = pkViewsService.PKViews.get();
            $('#myModal').modal('hide');
        }).error(function(data, status, headers, config) {
        });
    };
    
    $scope.updatePKView = function() {
        $http.post('/updatePKView', $scope.pkviewData).success(function(data){
            $scope.pkviews = pkViewsService.PKViews.get();
            $('#myModal2').modal('hide');
        }).error(function(data, status, headers, config) {
        });
    };
    
    $scope.deletePKView = function(id) {
        pkViewsService.DeletePKView.get({id:id}, function(data){
            $scope.pkviews = pkViewsService.PKViews.get();
            $('#myModal3').modal('hide');
        });    
    };
});

minibean.service('pkViewsService',function($resource){
    this.PKViews = $resource(
            '/getPKViews',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.DeletePKView = $resource(
            '/deletePKView/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageFrontPageTopicsController',function($scope, $modal, $http, $filter, frontPageTopicsService){
    $scope.name = " ";
    $scope.pageNumber;
    $scope.pageSize;
    $scope.formData = "";
    var currentPage = 1;
    var totalPages;
    $scope.isChosen = false;
    
    $scope.frontPageTopics = frontPageTopicsService.FrontPageTopics.get({name:$scope.name,currentPage:currentPage},function(response) {
        totalPages = $scope.frontPageTopics.totalPages;
        currentPage = $scope.frontPageTopics.currentPage;
        $scope.pageNumber = $scope.frontPageTopics.currentPage;
        $scope.pageSize = $scope.frontPageTopics.totalPages;
        if(totalPages == 0) {
            $scope.pageNumber = 0;
        }
    });
    
    $scope.searchFrontPageTopics = function(page) {
        if(angular.isUndefined($scope.name) || $scope.name=="") {
            $scope.name = " ";
        }
        currentPage = page;
        $scope.frontPageTopics = frontPageTopicsService.FrontPageTopics.get({name:$scope.name,currentPage:currentPage},function(response) {
            totalPages = $scope.frontPageTopics.totalPages;
            currentPage = $scope.frontPageTopics.currentPage;
            $scope.pageNumber = $scope.frontPageTopics.currentPage;
            $scope.pageSize = $scope.frontPageTopics.totalPages;
            if(totalPages == 0) {
                $scope.pageNumber = 0;
            }
        });
    };
    
    $scope.setData = function(frontPageTopic) {
        $scope.frontPageTopicData = frontPageTopic;
    };
    $scope.resetData = function() {
        $scope.formData = "";
        $scope.isChosen = false;
    }
    $scope.setDeleteId = function(id) {
        $scope.deleteId = id;
    };
    $scope.setToggleActiveId = function(id) {
        $scope.toggleActiveId = id;
    };
    $scope.saveFrontPageTopic = function() {
        $http.post('/saveFrontPageTopic', $scope.formData).success(function(data){
            $scope.searchFrontPageTopics(currentPage);
            $('#myModal').modal('hide');
        }).error(function(data, status, headers, config) {
        });
    };
    
    $scope.updateFrontPageTopic = function() {
        $http.post('/updateFrontPageTopic', $scope.frontPageTopicData).success(function(data){
            $scope.searchFrontPageTopics(currentPage);
            $('#myModal2').modal('hide');
        }).error(function(data, status, headers, config) {
        });
    };
    
    $scope.deleteFrontPageTopic = function(id) {
        frontPageTopicsService.DeleteFrontPageTopic.get({id:id}, function(data){
            $scope.searchFrontPageTopics(currentPage);
            $('#myModal3').modal('hide');
        });    
    };
    
    $scope.toggleActiveFrontPageTopic = function(id) {
        frontPageTopicsService.ToggleActiveFrontPageTopic.get({id:id}, function(data){
            $scope.searchFrontPageTopics(currentPage);
            $('#myModal4').modal('hide');
        });    
    };
    
    $scope.onNext = function() {
        if(currentPage < totalPages) {
            currentPage++;
            $scope.searchFrontPageTopics(currentPage);
        }
    };
    $scope.onPrev = function() {
        if(currentPage > 1) {
            currentPage--;
            $scope.searchFrontPageTopics(currentPage);
        }
    };
    
});

minibean.service('frontPageTopicsService',function($resource){
    this.FrontPageTopics = $resource(
            '/getFrontPageTopics/:name/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.DeleteFrontPageTopic = $resource(
            '/deleteFrontPageTopic/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.ToggleActiveFrontPageTopic = $resource(
            '/toggleActiveFrontPageTopic/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageAnnouncementsController',function($scope, $modal, $http, $filter, AnnouncementsService, deleteAnnouncementService, announcementIconService){
	$scope.title = " ";
	$scope.pageNumber;
	$scope.pageSize;
	$scope.formData = "";
	var currentPage = 1;
	var totalPages;
	$scope.isChosen = false;
	
	$scope.searchForm= {
        from : new Date(),
        to : new Date()
	}
	
	// this is workaround to close the dropdown
	$scope.$watch('searchForm.to', function(){
		$("li.dropdown").removeClass('open');
	});
	
	$scope.$watch('searchForm.from', function(){
		$("li.dropdown").removeClass('open');
	});
	
	$scope.Icons = announcementIconService.getAllIcons.get();
	
	$scope.select_icon = function(id, name, url) {
        $scope.icon_id = id;
        $scope.icon_url = url;
        $scope.icon_name = name;
        $scope.formData.icon = id;
        $scope.isChosen = true;
        $scope.ancmtData.ic.id = id;
    }
	
	$scope.announcements = AnnouncementsService.AnnouncementInfo.get({title:$scope.title,currentPage:currentPage},function(response) {
		totalPages = $scope.announcements.totalPages;
		currentPage = $scope.announcements.currentPage;
		$scope.pageNumber = $scope.announcements.currentPage;
		$scope.pageSize = $scope.announcements.totalPages;
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
		$scope.searchAnnouncements = function(page) {
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		currentPage = page;
		$scope.announcements = AnnouncementsService.AnnouncementInfo.get({title:$scope.title,currentPage:currentPage},function(response) {
			totalPages = $scope.announcements.totalPages;
			currentPage = $scope.announcements.currentPage;
			$scope.pageNumber = $scope.announcements.currentPage;
			$scope.pageSize = $scope.announcements.totalPages;
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	};
	
	$scope.setData = function(ancmt) {
		$scope.ancmtData = ancmt;
		$scope.icon_url = ancmt.ic.url;
        $scope.icon_name = ancmt.ic.name;
		$scope.searchForm.from = $filter('date')(new Date(ancmt.fd),'MMMM-dd-yyyy');
		$scope.searchForm.to = $filter('date')(new  Date(ancmt.td),'MMMM-dd-yyyy');
	};
	$scope.resetData = function() {
		$scope.searchForm.from = new Date();
		$scope.searchForm.to = new Date();
		$scope.icon_id = "";
        $scope.icon_url = "";
        $scope.icon_name = "";
		$scope.formData = "";
		$scope.isChosen = false;
	}
	$scope.setDeleteId = function(id) {
		$scope.deleteId = id;
	};
	$scope.saveAnnouncement = function() {
		$scope.formData.fromDate = $filter('date')(new Date($scope.searchForm.from),'yyyy-MM-dd');
		$scope.formData.toDate = $filter('date')(new Date($scope.searchForm.to),'yyyy-MM-dd');
		$http.post('/saveAnnouncement', $scope.formData).success(function(data){
			$scope.searchAnnouncements(currentPage);
			$('#myModal').modal('hide');
		}).error(function(data, status, headers, config) {
		});
	};
	
	$scope.updateAnnouncement = function() {
		$http.post('/updateAnnouncement', $scope.ancmtData).success(function(data){
			$scope.searchAnnouncements(currentPage);
			$('#myModal2').modal('hide');
		}).error(function(data, status, headers, config) {
		});
	};
	
	$scope.deleteAnnouncement = function(id) {
		deleteAnnouncementService.DeleteAnnouncement.get({id:id}, function(data){
			$scope.searchAnnouncements(currentPage);
            $('#myModal3').modal('hide');
		});    
	};
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.searchAnnouncements(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.searchAnnouncements(currentPage);
		}
	};
	
});

minibean.service('AnnouncementsService',function($resource){
    this.AnnouncementInfo = $resource(
            '/getAnnouncements/:title/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteAnnouncementService',function($resource){
    this.DeleteAnnouncement = $resource(
            '/deleteAnnouncement/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('announcementIconService',function($resource){
    this.getAllIcons = $resource(
            '/getAllAnnouncementIcons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibean.controller('ManageUsersController',function($scope, $modal, $http, $filter, ManageUsersService, suspendUserService, activateUserService){
	$scope.title = " ";
	$scope.userStatus = "-1";
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.usersData = ManageUsersService.UserInfo.get({title: $scope.title,userStatus: $scope.userStatus,currentPage :currentPage},function(response) {
		totalPages = $scope.usersData.totalPages;
		currentPage = $scope.usersData.currentPage;
		$scope.pageNumber = $scope.usersData.currentPage;
		$scope.pageSize = $scope.usersData.totalPages;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
			
	});
	
	$scope.searchUsers = function(page) {
		currentPage = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		
		$scope.usersData = ManageUsersService.UserInfo.get({title: $scope.title,userStatus: $scope.userStatus,currentPage :currentPage},function(response) {
			totalPages = $scope.usersData.totalPages;
			currentPage = $scope.usersData.currentPage;
			$scope.pageNumber = $scope.usersData.currentPage;
			$scope.pageSize = $scope.usersData.totalPages;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
	    });
	};
	
	$scope.suspendUser = function(user) {
		suspendUserService.suspendUser.get({id: user.id},function(response) {
			$scope.searchUsers(currentPage);
		});
	};
	
	$scope.activateUser = function(user) {
		activateUserService.activateUser.get({id: user.id},function(response) {
			$scope.searchUsers(currentPage);
		});
	};
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.searchUsers(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.searchUsers(currentPage);
		}
	};
	
});

minibean.service('ManageUsersService',function($resource){
    this.UserInfo = $resource(
            '/getUsers/:title/:userStatus/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('suspendUserService',function($resource){
    this.suspendUser = $resource(
            '/suspendUser/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('activateUserService',function($resource){
    this.activateUser = $resource(
            '/activateUser/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});


minibean.controller('ManagePostsController',function($scope, $modal, $http, $filter, reportedPostsService, deleteReportedObjectService, deletedPostsService, deletedInfoDeleteService, getAllPostsService, deletePostsStatusService){
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	$scope.title = " ";
	$scope.searchByCommunity = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	$scope.deletePostData;
	var currentPage3 = 1;
	var totalPages3;
	
	$scope.posts = reportedPostsService.getPosts.get({currentPage: currentPage},function(response) {
		totalPages = $scope.posts.totalPages;
		currentPage = $scope.posts.currentPage;
		$scope.pageNumber = $scope.posts.currentPage;
		$scope.pageSize = $scope.posts.totalPages;
		$scope.size = $scope.posts.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.deletedPosts = deletedPostsService.getPosts.get({currentPage:currentPage2,communityId: $scope.searchByCommunity},function(response) {
		totalPages2 = $scope.deletedPosts.totalPages;
		currentPage2 = $scope.deletedPosts.currentPage;
		$scope.pageNumber2 = $scope.deletedPosts.currentPage;
		$scope.pageSize2 = $scope.deletedPosts.totalPages;
		$scope.size2 = $scope.deletedPosts.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedInfoPosts = function(page) {
		currentPage2 = page;
		if(angular.isUndefined($scope.searchByCommunity) || $scope.searchByCommunity=="") {
			$scope.searchByCommunity = " ";
		}
		$scope.deletedPosts = deletedPostsService.getPosts.get({currentPage:currentPage2, communityId: $scope.searchByCommunity},function(response) {
			totalPages2 = $scope.deletedPosts.totalPages;
			currentPage2 = $scope.deletedPosts.currentPage;
			$scope.pageNumber2 = $scope.deletedPosts.currentPage;
			$scope.pageSize2 = $scope.deletedPosts.totalPages;
			$scope.size2 = $scope.deletedPosts.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
	};
	
	$scope.setData = function(post) {
		$scope.postId = post.socialObjectID;
		$scope.reportObjectId = post.id;
	};
	
	$scope.setDeletedInfoData = function(post) {
		$scope.InfoPostId = post.socialObjectID;
		$scope.deletedInfoId = post.id;
	};
	
	$scope.UnDeletePost = function() {
		deletedInfoDeleteService.DeleteInfo.get({id: $scope.deletedInfoId,postId: $scope.InfoPostId},function(response) {
			$('#myModal4').modal('hide');
			$scope.getDeletedInfoPosts(currentPage2);
		});
	};
	
	$scope.deletePost = function() {
		deleteReportedObjectService.DeleteReportedObject.get({id: $scope.reportObjectId,postId: $scope.postId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getPosts(currentPage);
			$scope.getDeletedInfoPosts(currentPage2);
		});
	};

	
	$scope.getPosts = function(page) {
		currentPage = page;
		$scope.posts = reportedPostsService.getPosts.get({currentPage: currentPage},function(response) {
			totalPages = $scope.posts.totalPages;
			currentPage = $scope.posts.currentPage;
			$scope.pageNumber = $scope.posts.currentPage;
			$scope.pageSize = $scope.posts.totalPages;
			$scope.size = $scope.posts.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	};
	
	$scope.searchPosts = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allPosts = getAllPostsService.getPosts.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allPosts.totalPages;
			currentPage3 = $scope.allPosts.currentPage;
			$scope.pageNumber3 = $scope.allPosts.currentPage;
			$scope.pageSize3 = $scope.allPosts.totalPages;
			$scope.size3 = $scope.allPosts.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});

	};
	
	$scope.setPostData = function(post) {
		$scope.deletePostData = post.id;
	}
	
	$scope.unDeletePostStatus = function() {
		deletePostsStatusService.unDeletePosts.get({id: $scope.InfoPostId},function(response) {
			$scope.searchPosts(currentPage3);
			$scope.getDeletedInfoPosts(currentPage2);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deletePostStatus = function() {
		deletePostsStatusService.deletePosts.get({id: $scope.InfoPostId},function(response) {
			$scope.searchPosts(currentPage3);
			$scope.getDeletedInfoPosts(currentPage2);
			$('#myModal6').modal('hide');
		});
	}
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getPosts(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getPosts(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedInfoPosts(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedInfoPosts(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchPosts(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchPosts(currentPage3);
		}
	};
	
});

minibean.service('reportedPostsService',function($resource){
    this.getPosts = $resource(
            '/getReportedPosts/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletePostsStatusService',function($resource){
    this.deletePosts = $resource(
            '/deletePosts/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeletePosts = $resource(
            '/unDeletePosts/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('getAllPostsService',function($resource){
    this.getPosts = $resource(
            '/getAllPosts/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectService',function($resource){
    this.DeleteReportedObject = $resource(
            '/deleteReportedObject/:id/:postId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedPostsService',function($resource){
    this.getPosts = $resource(
            '/getDeletedPosts/:currentPage/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedInfoDeleteService',function($resource){
    this.DeleteInfo = $resource(
            '/deletedInfoUnDelete/:id/:postId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});


minibean.controller('ManageQuestionsController',function($scope, $http, $routeParams, reportedQuestionsService, deleteReportedObjectQuestionService, deletedQuestionsService, deleteQuestionStatusService, getAllQuestionsService) {
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	
	$scope.searchByCommunity = " ";
	$scope.title = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	var currentPage3 = 1;
	var totalPages3;
	
	$scope.questions = reportedQuestionsService.getQuestions.get({currentPage: currentPage},function(response) {
		totalPages = $scope.questions.totalPages;
		currentPage = $scope.questions.currentPage;
		$scope.pageNumber = $scope.questions.currentPage;
		$scope.pageSize = $scope.questions.totalPages;
		$scope.size = $scope.questions.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.getQuestions = function(page) {
		currentPage = page
		$scope.questions = reportedQuestionsService.getQuestions.get({currentPage: currentPage},function(response) {
			totalPages = $scope.questions.totalPages;
			currentPage = $scope.questions.currentPage;
			$scope.pageNumber = $scope.questions.currentPage;
			$scope.pageSize = $scope.questions.totalPages;
			$scope.size = $scope.questions.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	};
	
	$scope.setData = function(question) {
		$scope.questionId = question.socialObjectID;
		$scope.reportObjectId = question.id;
	};
	
	$scope.deleteQuestion = function() {
		deleteReportedObjectQuestionService.DeleteReportedObjectQuestion.get({id: $scope.reportObjectId,questionId: $scope.questionId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getQuestions(currentPage);
			$scope.getDeletedInfoQuestions(currentPage2);
		});
	};
	
	$scope.deletedQuestions = deletedQuestionsService.getQuestions.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
		totalPages2 = $scope.deletedQuestions.totalPages;
		currentPage2 = $scope.deletedQuestions.currentPage;
		$scope.pageNumber2 = $scope.deletedQuestions.currentPage;
		$scope.pageSize2 = $scope.deletedQuestions.totalPages;
		$scope.size2 = $scope.deletedQuestions.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedInfoQuestions = function(page) {
		currentPage2 = page;
		if(angular.isUndefined($scope.searchByCommunity) || $scope.searchByCommunity=="") {
			$scope.searchByCommunity = " ";
		}
		$scope.deletedQuestions = deletedQuestionsService.getQuestions.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
			totalPages2 = $scope.deletedQuestions.totalPages;
			currentPage2 = $scope.deletedQuestions.currentPage;
			$scope.pageNumber2 = $scope.deletedQuestions.currentPage;
			$scope.pageSize2 = $scope.deletedQuestions.totalPages;
			$scope.size2 = $scope.deletedQuestions.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
		
	};
	
	$scope.setQuestionData = function(question) {
		$scope.deleteQuestionData = question.id;
	}
	
	$scope.setDeletedInfoData = function(question) {
		$scope.deleteQuestionData = question.socialObjectID;
	}
	
	$scope.unDeleteQuestionStatus = function() {
		deleteQuestionStatusService.unDeleteQuestion.get({id: $scope.deleteQuestionData},function(response) {
			$scope.getDeletedInfoQuestions(currentPage2);
			$scope.searchQuestions(currentPage3);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deleteQuestionStatus = function() {
		deleteQuestionStatusService.deleteQuestion.get({id: $scope.deleteQuestionData},function(response) {
			$scope.getDeletedInfoQuestions(currentPage2);
			$scope.searchQuestions(currentPage3);
			$('#myModal6').modal('hide');
		});
	}
	
	
	$scope.searchQuestions = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allQuestions = getAllQuestionsService.getAllQuestions.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allQuestions.totalPages;
			currentPage3 = $scope.allQuestions.currentPage;
			$scope.pageNumber3 = $scope.allQuestions.currentPage;
			$scope.pageSize3 = $scope.allQuestions.totalPages;
			$scope.size3 = $scope.allQuestions.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});
	};
	
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getQuestions(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getQuestions(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedInfoQuestions(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedInfoQuestions(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchQuestions(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchQuestions(currentPage3);
		}
	};
});	
	

minibean.service('reportedQuestionsService',function($resource){
    this.getQuestions = $resource(
            '/getReportedQuestions/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectQuestionService',function($resource){
    this.DeleteReportedObjectQuestion = $resource(
            '/deleteReportedObjectQuestion/:id/:questionId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedQuestionsService',function($resource){
    this.getQuestions = $resource(
            '/getDeletedQuestions/:currentPage/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteQuestionStatusService',function($resource){
    this.deleteQuestion = $resource(
            '/deleteQuestion/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeleteQuestion = $resource(
            '/unDeleteQuestion/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('getAllQuestionsService',function($resource){
    this.getAllQuestions = $resource(
            '/getAllQuestions/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});


minibean.controller('ManageCommentsController',function($scope, $http, $routeParams, reportedCommentsService, deleteReportedObjectCommentService, deletedCommentsService, getAllCommentsService, deleteCommentsStatusService) {
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	
	$scope.searchByCommunity = " ";
	$scope.title = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	var currentPage3 = 1;
	var totalPages3;
	
	$scope.comments = reportedCommentsService.getComments.get({currentPage: currentPage},function(response) {
		totalPages = $scope.comments.totalPages;
		currentPage = $scope.comments.currentPage;
		$scope.pageNumber = $scope.comments.currentPage;
		$scope.pageSize = $scope.comments.totalPages;
		$scope.size = $scope.comments.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.deletedComments = deletedCommentsService.getComments.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
		totalPages2 = $scope.deletedComments.totalPages;
		currentPage2 = $scope.deletedComments.currentPage;
		$scope.pageNumber2 = $scope.deletedComments.currentPage;
		$scope.pageSize2 = $scope.deletedComments.totalPages;
		$scope.size2 = $scope.deletedComments.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedComments = function(page) {
		currentPage2 = page;
		if(angular.isUndefined($scope.searchByCommunity) || $scope.searchByCommunity=="") {
			$scope.searchByCommunity = " ";
		}
		$scope.deletedComments = deletedCommentsService.getComments.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
			totalPages2 = $scope.deletedComments.totalPages;
			currentPage2 = $scope.deletedComments.currentPage;
			$scope.pageNumber2 = $scope.deletedComments.currentPage;
			$scope.pageSize2 = $scope.deletedComments.totalPages;
			$scope.size2 = $scope.deletedComments.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
	};
	
	$scope.getComments = function(page) {
		currentPage = page;
		$scope.comments = reportedCommentsService.getComments.get({currentPage: currentPage},function(response) {
			totalPages = $scope.comments.totalPages;
			currentPage = $scope.comments.currentPage;
			$scope.pageNumber = $scope.comments.currentPage;
			$scope.pageSize = $scope.comments.totalPages;
			$scope.size = $scope.comments.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	};
	
	$scope.searchComments = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allComments = getAllCommentsService.getAllComments.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allComments.totalPages;
			currentPage3 = $scope.allComments.currentPage;
			$scope.pageNumber3 = $scope.allComments.currentPage;
			$scope.pageSize3 = $scope.allComments.totalPages;
			$scope.size3 = $scope.allComments.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});

	};
	
	$scope.setData = function(comment) {
		$scope.commentId = comment.socialObjectID;
		$scope.reportObjectId = comment.id;
	};
	
	$scope.setCommentData = function(comment) {
		$scope.deleteCommentData = comment.id;
	}
	
	$scope.setDeletedInfoData = function(comment) {
		$scope.deleteCommentData = comment.socialObjectID;
	}
	
	$scope.unDeleteCommentStatus = function() {
		deleteCommentsStatusService.unDeleteComment.get({id: $scope.deleteCommentData},function(response) {
			$scope.searchComments(currentPage3);
			$scope.getDeletedComments(currentPage2);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deleteCommentStatus = function() {
		deleteCommentsStatusService.deleteComment.get({id: $scope.deleteCommentData},function(response) {
			$scope.searchComments(currentPage3);
			$scope.getDeletedComments(currentPage2);
			$('#myModal6').modal('hide');
		});
	}
	
	$scope.deleteComment = function() {
		deleteReportedObjectCommentService.DeleteReportedObject.get({id: $scope.reportObjectId,commentId: $scope.commentId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getComments(currentPage);
			$scope.getDeletedComments(currentPage2);
		});
	};
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getComments(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getComments(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedComments(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedComments(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchComments(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchComments(currentPage3);
		}
	};
	
});

minibean.service('reportedCommentsService',function($resource){
    this.getComments = $resource(
            '/getReportedComments/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectCommentService',function($resource){
    this.DeleteReportedObject = $resource(
            '/deleteReportedObjectComment/:id/:commentId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedCommentsService',function($resource){
    this.getComments = $resource(
            '/getDeletedComments/:currentPage/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('getAllCommentsService',function($resource){
    this.getAllComments = $resource(
            '/getAllComments/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteCommentsStatusService',function($resource){
    this.deleteComment = $resource(
            '/deleteComment/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeleteComment = $resource(
            '/unDeleteComment/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageAnswersController',function($scope, $http, $routeParams, reportedAnswersService, deleteReportedObjectAnswerService, deletedAnswersService, deleteAnswersStatusService, getAllAnswersService){
	
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.searchByCommunity = " ";
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	
	$scope.title = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	var currentPage3 = 1;
	var totalPages3;

	$scope.answers = reportedAnswersService.getAnswers.get({currentPage: currentPage},function(response) {
		totalPages = $scope.answers.totalPages;
		currentPage = $scope.answers.currentPage;
		$scope.pageNumber = $scope.answers.currentPage;
		$scope.pageSize = $scope.answers.totalPages;
		$scope.size = $scope.answers.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.getAnswers = function(page) {
		currentPage = page;
		$scope.answers = reportedAnswersService.getAnswers.get({currentPage: currentPage},function(response) {
			totalPages = $scope.answers.totalPages;
			currentPage = $scope.answers.currentPage;
			$scope.pageNumber = $scope.answers.currentPage;
			$scope.pageSize = $scope.answers.totalPages;
			$scope.size = $scope.answers.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
		
	};
	
	$scope.setData = function(answer) {
		$scope.answerId = answer.socialObjectID;
		$scope.reportObjectId = answer.id;
	};
	
	$scope.deleteAnswer = function() {
		deleteReportedObjectAnswerService.DeleteReportedObject.get({id: $scope.reportObjectId,answerId: $scope.answerId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getAnswers(currentPage);
			$scope.getDeletedAnswers(currentPage2);
		});
	};
	
	$scope.deletedAnswers = deletedAnswersService.getAnswers.get({currentPage:currentPage2,communityId: $scope.searchByCommunity},function(response) {
		totalPages2 = $scope.deletedAnswers.totalPages;
		currentPage2 = $scope.deletedAnswers.currentPage;
		$scope.pageNumber2 = $scope.deletedAnswers.currentPage;
		$scope.pageSize2 = $scope.deletedAnswers.totalPages;
		$scope.size2 = $scope.deletedAnswers.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedAnswers = function(page) {
		currentPage2 = page;
		if(angular.isUndefined($scope.searchByCommunity) || $scope.searchByCommunity=="") {
			$scope.searchByCommunity = " ";
		}
		$scope.deletedAnswers = deletedAnswersService.getAnswers.get({currentPage:currentPage2,communityId: $scope.searchByCommunity},function(response) {
			totalPages2 = $scope.deletedAnswers.totalPages;
			currentPage2 = $scope.deletedAnswers.currentPage;
			$scope.pageNumber2 = $scope.deletedAnswers.currentPage;
			$scope.pageSize2 = $scope.deletedAnswers.totalPages;
			$scope.size2 = $scope.deletedAnswers.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
	};
	
	
	$scope.setAnswerData = function(answer) {
		$scope.deleteAnswerData = answer.id;
	}
	
	$scope.setDeletedInfoData = function(answer) {
		$scope.deleteAnswerData = answer.socialObjectID;
	}
	
	$scope.unDeleteAnswerStatus = function() {
		deleteAnswersStatusService.unDeleteAnswer.get({id: $scope.deleteAnswerData},function(response) {
			$scope.getDeletedAnswers(currentPage2);
			$scope.searchAllAnswers(currentPage3);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deleteAnswerStatus = function() {
		deleteAnswersStatusService.deleteAnswer.get({id: $scope.deleteAnswerData},function(response) {
			$scope.getDeletedAnswers(currentPage2);
			$scope.searchAllAnswers(currentPage3);
			$('#myModal6').modal('hide');
		});
	}
	
	$scope.searchAllAnswers = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allAnswers = getAllAnswersService.getAllAnswers.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allAnswers.totalPages;
			currentPage3 = $scope.allAnswers.currentPage;
			$scope.pageNumber3 = $scope.allAnswers.currentPage;
			$scope.pageSize3 = $scope.allAnswers.totalPages;
			$scope.size3 = $scope.allAnswers.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});
	};
	
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getAnswers(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getAnswers(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedAnswers(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedAnswers(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchAllAnswers(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchAllAnswers(currentPage3);
		}
	};
	
});

minibean.service('reportedAnswersService',function($resource){
    this.getAnswers = $resource(
            '/getReportedAnswers/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectAnswerService',function($resource){
    this.DeleteReportedObject = $resource(
            '/deleteReportedObjectAnswer/:id/:answerId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedAnswersService',function($resource){
    this.getAnswers = $resource(
            '/getDeletedAnswers/:currentPage/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteAnswersStatusService',function($resource){
    this.deleteAnswer = $resource(
            '/deleteAnswer/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeleteAnswer = $resource(
            '/unDeleteAnswer/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('getAllAnswersService',function($resource){
    this.getAllAnswers = $resource(
            '/getAllAnswers/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageCommunitiesController',function($scope, $http, $routeParams, reportedCommunitiesService, deleteReportedObjectCommunityService, deletedCommunitiesService, deleteCommunityStatusService, getAllCommunitiesService){
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	
	$scope.title = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	var currentPage3 = 1;
	var totalPages3;
	
	$scope.communities = reportedCommunitiesService.getCommunities.get({currentPage: currentPage},function(response) {
		totalPages = $scope.communities.totalPages;
		currentPage = $scope.communities.currentPage;
		$scope.pageNumber = $scope.communities.currentPage;
		$scope.pageSize = $scope.communities.totalPages;
		$scope.size = $scope.communities.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.getCommunities = function(page) {
		currentPage = page;
		$scope.communities = reportedCommunitiesService.getCommunities.get({currentPage: currentPage},function(response) {
			totalPages = $scope.communities.totalPages;
			currentPage = $scope.communities.currentPage;
			$scope.pageNumber = $scope.communities.currentPage;
			$scope.pageSize = $scope.communities.totalPages;
			$scope.size = $scope.communities.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	};
	
	$scope.allCommunities = getAllCommunitiesService.getAllCommunities.get({currentPage: currentPage3,title: $scope.title},function(response) {
		totalPages3 = $scope.allCommunities.totalPages;
		currentPage3 = $scope.allCommunities.currentPage;
		$scope.pageNumber3 = $scope.allCommunities.currentPage;
		$scope.pageSize3 = $scope.allCommunities.totalPages;
		$scope.size3 = $scope.allCommunities.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	$scope.searchCommunities = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allCommunities = getAllCommunitiesService.getAllCommunities.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allCommunities.totalPages;
			currentPage3 = $scope.allCommunities.currentPage;
			$scope.pageNumber3 = $scope.allCommunities.currentPage;
			$scope.pageSize3 = $scope.allCommunities.totalPages;
			$scope.size3 = $scope.allCommunities.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});
	};
	
	$scope.setData = function(community) {
		$scope.communityId = community.socialObjectID;
		$scope.reportObjectId = community.id;
	};
	
	$scope.setDeletedInfoData = function(community) {
		$scope.deleteCommunityData = community.socialObjectID;
	}
	
	$scope.deleteCommunity = function() {
		deleteReportedObjectCommunityService.DeleteReportedObject.get({id: $scope.reportObjectId,communityId: $scope.communityId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getCommunities(currentPage);
			$scope.getDeletedCommunities(currentPage2);
		});
	};
	
	$scope.deletedCommunities = deletedCommunitiesService.getCommunities.get({currentPage:currentPage2},function(response) {
		totalPages2 = $scope.deletedCommunities.totalPages;
		currentPage2 = $scope.deletedCommunities.currentPage;
		$scope.pageNumber2 = $scope.deletedCommunities.currentPage;
		$scope.pageSize2 = $scope.deletedCommunities.totalPages;
		$scope.size2 = $scope.deletedCommunities.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedCommunities = function(page) {
		
		currentPage2 = page;
		$scope.deletedCommunities = deletedCommunitiesService.getCommunities.get({currentPage:currentPage2},function(response) {
			totalPages2 = $scope.deletedCommunities.totalPages;
			currentPage2 = $scope.deletedCommunities.currentPage;
			$scope.pageNumber2 = $scope.deletedCommunities.currentPage;
			$scope.pageSize2 = $scope.deletedCommunities.totalPages;
			$scope.size2 = $scope.deletedCommunities.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
	};
	
	$scope.setCommunityData = function(community) {
		$scope.deleteCommunityData = community.id;
	}
	
	$scope.unDeleteCommunityStatus = function() {
		deleteCommunityStatusService.unDeleteCommunity.get({id: $scope.deleteCommunityData},function(response) {
			$scope.getDeletedCommunities(currentPage2);
			$scope.searchCommunities(currentPage3);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deleteCommunityStatus = function() {
		deleteCommunityStatusService.deleteCommunity.get({id: $scope.deleteCommunityData},function(response) {
			$scope.getDeletedCommunities(currentPage2);
			$scope.searchCommunities(currentPage3);
			$('#myModal6').modal('hide');
		});
	}
	
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getCommunities(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getCommunities(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedCommunities(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedCommunities(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchCommunities(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchCommunities(currentPage3);
		}
	};
	
	
});

minibean.service('reportedCommunitiesService',function($resource){
    this.getCommunities = $resource(
            '/getReportedCommunities/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectCommunityService',function($resource){
    this.DeleteReportedObject = $resource(
            '/deleteReportedObjectCommunity/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedCommunitiesService',function($resource){
    this.getCommunities = $resource(
            '/getDeletedCommunities/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteCommunityStatusService',function($resource){
    this.deleteCommunity = $resource(
            '/deleteCommunity/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeleteCommunity = $resource(
            '/unDeleteCommunity/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('getAllCommunitiesService',function($resource){
    this.getAllCommunities = $resource(
            '/getAllCommunities/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});


minibean.controller('ManageUsers2Controller',function($scope, $http, $routeParams, reportedUsersService, deleteReportedObjectUserService, deletedUsersService, deleteUserStatusService, allUsersService){
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	
	$scope.pageNumber2;
	$scope.pageSize2;
	var currentPage2 = 1;
	var totalPages2;
	$scope.title = " ";
	$scope.pageNumber3;
	$scope.pageSize3;
	var currentPage3 = 1;
	var totalPages3;
	
	$scope.users = reportedUsersService.getUsers.get({currentPage: currentPage},function(response) {
		totalPages = $scope.users.totalPages;
		currentPage = $scope.users.currentPage;
		$scope.pageNumber = $scope.users.currentPage;
		$scope.pageSize = $scope.users.totalPages;
		$scope.size = $scope.users.size;
		
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.getReportedUsers = function(page) {
		currentPage = page;
		$scope.users = reportedUsersService.getUsers.get({currentPage: currentPage},function(response) {
			totalPages = $scope.users.totalPages;
			currentPage = $scope.users.currentPage;
			$scope.pageNumber = $scope.users.currentPage;
			$scope.pageSize = $scope.users.totalPages;
			$scope.size = $scope.users.size;
			
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
		
		
	};
	
	$scope.deletedUsers = deletedUsersService.getUsers.get({currentPage:currentPage2},function(response) {
		totalPages2 = $scope.deletedUsers.totalPages;
		currentPage2 = $scope.deletedUsers.currentPage;
		$scope.pageNumber2 = $scope.deletedUsers.currentPage;
		$scope.pageSize2 = $scope.deletedUsers.totalPages;
		$scope.size2 = $scope.deletedUsers.size;
		
		if(totalPages2 == 0) {
			$scope.pageNumber2 = 0;
		}
	});
	
	$scope.getDeletedUsers = function(page) {
		currentPage2 = page;
		$scope.deletedUsers = deletedUsersService.getUsers.get({currentPage:currentPage2},function(response) {
			totalPages2 = $scope.deletedUsers.totalPages;
			currentPage2 = $scope.deletedUsers.currentPage;
			$scope.pageNumber2 = $scope.deletedUsers.currentPage;
			$scope.pageSize2 = $scope.deletedUsers.totalPages;
			$scope.size2 = $scope.deletedUsers.size;
			
			if(totalPages2 == 0) {
				$scope.pageNumber2 = 0;
			}
		});
		
	};
	
	$scope.setDeletedInfoData = function(user) {
		$scope.deleteUserData = user.socialObjectID;
	}
	
	$scope.setData = function(user) {
		$scope.userId = user.socialObjectID;
		$scope.reportObjectId = user.id;
	};
	
	$scope.setUserData = function(user) {
		$scope.deleteUserData = user.id;
	}
	
	$scope.allUsers = allUsersService.getAllUsers.get({currentPage: currentPage3,title: $scope.title},function(response) {
		totalPages3 = $scope.allUsers.totalPages;
		currentPage3 = $scope.allUsers.currentPage;
		$scope.pageNumber3 = $scope.allUsers.currentPage;
		$scope.pageSize3 = $scope.allUsers.totalPages;
		$scope.size3 = $scope.allUsers.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	$scope.searchUsers = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		$scope.allUsers = allUsersService.getAllUsers.get({currentPage: currentPage3,title: $scope.title},function(response) {
			totalPages3 = $scope.allUsers.totalPages;
			currentPage3 = $scope.allUsers.currentPage;
			$scope.pageNumber3 = $scope.allUsers.currentPage;
			$scope.pageSize3 = $scope.allUsers.totalPages;
			$scope.size3 = $scope.allUsers.size;
			
			if(totalPages3 == 0) {
				$scope.pageNumber3 = 0;
			}
		});
	}
	
	
	$scope.deleteUser = function() {
		deleteReportedObjectUserService.DeleteReportedObject.get({id: $scope.reportObjectId,userId: $scope.userId},function(response) {
			$('#myModal3').modal('hide');
			$scope.getReportedUsers(currentPage);
			$scope.getDeletedUsers(currentPage2);
		});
	};
	
	
	$scope.unDeleteUserStatus = function() {
		deleteUserStatusService.unDeleteUser.get({id: $scope.deleteUserData},function(response) {
			$scope.getDeletedUsers(currentPage2);
			$scope.searchUsers(currentPage3);
			$('#myModal5').modal('hide');
		});
	}
	
	$scope.deleteUserStatus = function() {
		deleteUserStatusService.deleteUser.get({id: $scope.deleteUserData},function(response) {
			$scope.getDeletedUsers(currentPage2);
			$scope.searchUsers(currentPage3);
			$('#myModal6').modal('hide');
		});
	}
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.getReportedUsers(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.getReportedUsers(currentPage);
		}
	};
	
	$scope.onNext2 = function() {
		if(currentPage2 < totalPages2) {
			currentPage2++;
			$scope.getDeletedUsers(currentPage2);
		}
	};
	$scope.onPrev2 = function() {
		if(currentPage2 > 1) {
			currentPage2--;
			$scope.getDeletedUsers(currentPage2);
		}
	};
	
	$scope.onNext3 = function() {
		if(currentPage3 < totalPages3) {
			currentPage3++;
			$scope.searchUsers(currentPage3);
		}
	};
	$scope.onPrev3 = function() {
		if(currentPage3 > 1) {
			currentPage3--;
			$scope.searchUsers(currentPage3);
		}
	};
	
});

minibean.service('reportedUsersService',function($resource){
    this.getUsers = $resource(
            '/getReportedUsers/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteReportedObjectUserService',function($resource){
    this.DeleteReportedObject = $resource(
            '/deleteReportedObjectUser/:id/:userId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deletedUsersService',function($resource){
    this.getUsers = $resource(
            '/getDeletedUsers/:currentPage',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('deleteUserStatusService',function($resource){
    this.deleteUser = $resource(
            '/deleteUser/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.unDeleteUser = $resource(
            '/unDeleteUser/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('allUsersService',function($resource){
    this.getAllUsers = $resource(
            '/getAllUsers/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('ManageEdmController',function($scope, $http, $routeParams, locationService, edmService, getAllSubscribedUsersService, sendEmailsToSubscribedUsersService){
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	$scope.userIds;
	$scope.title = " ";
	$scope.gender = " ";
	$scope.location = " ";
	$scope.edmTemplateId = " ";
	$scope.isMailSent = false;
	
	$scope.allLocations = locationService.getAllDistricts.get();
	$scope.allEdmTemplates = edmService.allEdmTemplates.get();
	
	/*$scope.allUsers = getAllSubscribedUsersService.getAllUsers.get({currentPage: currentPage,title: $scope.title,gender: $scope.gender,location: $scope.location,edmTemplateId:$scope.edmTemplateId},function(response) {
		totalPages = $scope.allUsers.totalPages;
		currentPage = $scope.allUsers.currentPage;
		$scope.pageNumber = $scope.allUsers.currentPage;
		$scope.pageSize = $scope.allUsers.totalPages;
		$scope.userIds = $scope.allUsers.ids;
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	*/
	$scope.searchEdmTemplates = function(page) {
		currentPage = page;
		$scope.isMailSent = false;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			$scope.title = " ";
		}
		if(angular.isUndefined($scope.gender) || $scope.gender=="") {
			$scope.gender = " ";
		}
		if(angular.isUndefined($scope.location) || $scope.location=="") {
			$scope.location = " ";
		}
		if(angular.isUndefined($scope.edmTemplateId) || $scope.edmTemplateId=="") {
			$scope.edmTemplateId = " ";
		}
		$scope.allUsers = getAllSubscribedUsersService.getAllUsers.get({currentPage: currentPage,title: $scope.title,gender: $scope.gender,location: $scope.location,edmTemplateId:$scope.edmTemplateId},function(response) {
			totalPages = $scope.allUsers.totalPages;
			currentPage = $scope.allUsers.currentPage;
			$scope.pageNumber = $scope.allUsers.currentPage;
			$scope.pageSize = $scope.allUsers.totalPages;
			$scope.userIds = $scope.allUsers.ids;
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
		
		
	}
	
	$scope.myForm = {};
	
	$scope.sendEmails = function() {
		console.log($scope.myForm);
		$http.post('/sendTestEDM', $scope.myForm).success(function(data){
		}).error(function(data, status, headers, config) {
		});
	}
	
	$scope.sendMail = function(data) {
		console.log($scope.myForm);
		$http.post('/sendBulkEDM', data).success(function(data){
		}).error(function(data, status, headers, config) {
		});
	}
	
	$scope.onNext = function() {
		if(currentPage < totalPages) {
			currentPage++;
			$scope.searchEdmTemplates(currentPage);
		}
	};
	$scope.onPrev = function() {
		if(currentPage > 1) {
			currentPage--;
			$scope.searchEdmTemplates(currentPage);
		}
	};
	
});

minibean.service('edmService',function($resource){
    this.allEdmTemplates = $resource(
            '/get-all-edm-templates',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibean.service('getAllSubscribedUsersService',function($resource){
    this.getAllUsers = $resource(
            '/getAllSubscribedUsers/:currentPage/:title/:gender/:location/:edmTemplate',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('sendEmailsToSubscribedUsersService',function($resource){
    this.sendEmailTo = $resource(
            '/sendEmailsToSubscribedUsers/:userIds/:edmTemplate',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

////////////// Manage Game Account //////////////////

minibean.service('getUserGameAccountService',function($resource){
    this.getAllUsers = $resource(
            '/getGameAccountAllUsers',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibean.controller('ManageGameAccountController',function($scope, $http, $routeParams, getUserGameAccountService){
	$scope.pageNumber;
	$scope.pageSize;
	var currentPage = 1;
	var totalPages;
	$scope.userIds;
	
	$scope.allUsers = getUserGameAccountService.getAllUsers.get(function(response) {
		console.log(response);
		if(totalPages == 0) {
			$scope.pageNumber = 0;
		}
	});
	
	$scope.setUserData = function(user) {
		$scope.addPointsUser = user;
	}
	
	$scope.addBonus = function(user) {
		console.log(user);
		$http.post('/addbonus', user).success(function(data){
			$('#addBonus').modal('hide');
		}).error(function(data, status, headers, config) {
		});
	}
	
	$scope.addPenalty = function(user) {
		console.log(user);
		$http.post('/addPenalty', user).success(function(data){
			$('#addPenalty').modal('hide');
		}).error(function(data, status, headers, config) {
		});
	}
});

minibean.controller('ManagePhotoUploadController',function($scope, $http, $routeParams, $upload){

	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.isUpload = false;
	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = $files;
		$scope.formData.photo = 'cover-photo';
	}
	
	$scope.uploadPhoto = function() {
		
		$upload.upload({
			url: '/uploadPhoto',
			method: 'POST',
			file: $scope.selectedFiles[0],
			data: $scope.formData,
			fileFormDataName: 'cover-photo'
		}).progress(function(evt) {
			
	    }).success(function(data, status, headers, config) {
	    	$scope.path = data.URL;
	    	$scope.isUpload = true;
	    }).error(function(data, status, headers, config) {
	    	
	    });
	}
});
