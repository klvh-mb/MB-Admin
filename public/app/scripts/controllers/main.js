'use strict';

var minibean = angular.module('minibean');

minibean.controller('CreateArticleController', function($scope, $http, $location, articleCategoryService, locationService, usSpinnerService){
    $scope.article;
    $scope.submitBtn = "Save";
    
    $scope.targetLocations = locationService.getAllDistricts.get();
    
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

minibean.service('allUsersService',function($resource){
    this.AllUsers = $resource(
            '/get-all-Users',
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

minibean.controller('ShowArticleController',function($scope, $modal, deleteArticleService, allUsersService, allArticlesService, getDescriptionService){
    $scope.result = allArticlesService.AllArticles.get();
    
    //$scope.userResult = allUsersService.AllUsers.get();
    
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

minibean.controller('ShowAnnouncementController',function($scope, $modal, $http, $filter, AnnouncementsService, deleteAnnouncementService, announcementIconService){
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
	
	$scope.Icons = announcementIconService.getAllIcons.get();
	console.log($scope.Icons);
	
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
	
	//$scope.locations = LocationService.LocationInfo.get();
	//console.log($scope.locations);
		$scope.searchAnnouncements = function(page) {
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			console.log('inside function');
			$scope.title = " ";
		}
		currentPage = page;
		console.log($scope.title);
		console.log(currentPage);
		$scope.announcements = AnnouncementsService.AnnouncementInfo.get({title:$scope.title,currentPage:currentPage},function(response) {
			console.log($scope.announcements.totalPages);
			totalPages = $scope.announcements.totalPages;
			currentPage = $scope.announcements.currentPage;
			$scope.pageNumber = $scope.announcements.currentPage;
			$scope.pageSize = $scope.announcements.totalPages;
			if(totalPages == 0) {
				$scope.pageNumber = 0;
			}
		});
	    console.log($scope.announcements);
	};
	console.log($scope.title);
	console.log($scope.announcements);
	console.log('mfjfjfjfjf');
	
	$scope.setData = function(ancmt) {
		$scope.ancmtData = ancmt;
		$scope.icon_url = ancmt.ic.url;
        $scope.icon_name = ancmt.ic.name;
		$scope.searchForm.from = $filter('date')(new Date(ancmt.fd),'MMMM-dd-yyyy');
		$scope.searchForm.to = $filter('date')(new  Date(ancmt.td),'MMMM-dd-yyyy');
		console.log($scope.ancmtData);
	};
	$scope.setDates = function() {
		$scope.searchForm.from = new Date();
		$scope.searchForm.to = new Date();
		$scope.icon_id = "";
        $scope.icon_url = "";
        $scope.icon_name = "";
		$scope.formData = "";
		$scope.isChosen = false;
	}
	$scope.setDeleteId = function(Id) {
		$scope.deleteId = Id;
	};
	$scope.saveAnnouncement = function() {
		$scope.formData.fromDate = $filter('date')(new Date($scope.searchForm.from),'yyyy-MM-dd');
		$scope.formData.toDate = $filter('date')(new Date($scope.searchForm.to),'yyyy-MM-dd');
		console.log($scope.formData);
		$http.post('/saveAnnouncement', $scope.formData).success(function(data){
			console.log('success');
			$scope.searchAnnouncements(currentPage);
			$('#myModal').modal('hide');
		}).error(function(data, status, headers, config) {
			console.log('ERROR');
		});
	};
	
	$scope.updateAnnouncement = function() {
		$scope.ancmtData.fd = $filter('date')(new Date($scope.searchForm.from),'yyyy-MM-dd');
		$scope.ancmtData.td = $filter('date')(new Date($scope.searchForm.to),'yyyy-MM-dd');
		console.log($scope.ancmtData);
		$http.post('/updateAnnouncement', $scope.ancmtData).success(function(data){
			console.log('success');
			$scope.searchAnnouncements(currentPage);
			$('#myModal2').modal('hide');
		}).error(function(data, status, headers, config) {
			console.log('ERROR');
		});
	};
	
	$scope.deleteAnnouncement = function(idData) {
		console.log(idData);
		deleteAnnouncementService.DeleteAnnouncement.get({id :idData.id}, function(data){
			//$scope.announcements.results.splice($scope.announcements.results.indexOf(idData),1);
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
		console.log($scope.usersData);
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
			console.log('inside function');
			$scope.title = " ";
		}
		
		$scope.usersData = ManageUsersService.UserInfo.get({title: $scope.title,userStatus: $scope.userStatus,currentPage :currentPage},function(response) {
			console.log($scope.usersData);
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
		console.log($scope.posts);
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
		console.log($scope.deletedPosts);
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
			console.log('inside function');
			$scope.searchByCommunity = " ";
		}
		$scope.deletedPosts = deletedPostsService.getPosts.get({currentPage:currentPage2, communityId: $scope.searchByCommunity},function(response) {
			console.log($scope.deletedPosts);
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
			console.log('success');
			$('#myModal4').modal('hide');
			$scope.getDeletedInfoPosts(currentPage2);
		});
	};
	
	$scope.deletePost = function() {
		deleteReportedObjectService.DeleteReportedObject.get({id: $scope.reportObjectId,postId: $scope.postId},function(response) {
			console.log('success');
			$('#myModal3').modal('hide');
			$scope.getPosts(currentPage);
			$scope.getDeletedInfoPosts(currentPage2);
		});
	};

	
	$scope.getPosts = function(page) {
		currentPage = page;
		$scope.posts = reportedPostsService.getPosts.get({currentPage: currentPage},function(response) {
			console.log($scope.posts);
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
	
	$scope.allPosts = getAllPostsService.getPosts.get({currentPage: currentPage3,title: $scope.title},function(response) {
		console.log($scope.allPosts);
		totalPages3 = $scope.allPosts.totalPages;
		currentPage3 = $scope.allPosts.currentPage;
		$scope.pageNumber3 = $scope.allPosts.currentPage;
		$scope.pageSize3 = $scope.allPosts.totalPages;
		$scope.size3 = $scope.allPosts.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	$scope.searchPosts = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allPosts = getAllPostsService.getPosts.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allPosts);
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
			console.log('success');
		});
	}
	
	$scope.deletePostStatus = function() {
		console.log($scope.InfoPostId);
		deletePostsStatusService.deletePosts.get({id: $scope.InfoPostId},function(response) {
			$scope.searchPosts(currentPage3);
			$scope.getDeletedInfoPosts(currentPage2);
			$('#myModal6').modal('hide');
			console.log('success');
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
		console.log($scope.questions);
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
			console.log($scope.questions);
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
			console.log('success');
			$('#myModal3').modal('hide');
			$scope.getQuestions(currentPage);
			$scope.getDeletedInfoQuestions(currentPage2);
		});
	};
	
	$scope.deletedQuestions = deletedQuestionsService.getQuestions.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
		console.log($scope.deletedQuestions);
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
			console.log('inside function');
			$scope.searchByCommunity = " ";
		}
		$scope.deletedQuestions = deletedQuestionsService.getQuestions.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
			console.log($scope.deletedQuestions);
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
			console.log('success');
		});
	}
	
	$scope.deleteQuestionStatus = function() {
		deleteQuestionStatusService.deleteQuestion.get({id: $scope.deleteQuestionData},function(response) {
			$scope.getDeletedInfoQuestions(currentPage2);
			$scope.searchQuestions(currentPage3);
			$('#myModal6').modal('hide');
			console.log('success');
		});
	}
	
	
	$scope.allQuestions = getAllQuestionsService.getAllQuestions.get({currentPage: currentPage3,title: $scope.title},function(response) {
		console.log($scope.allQuestions);
		totalPages3 = $scope.allQuestions.totalPages;
		currentPage3 = $scope.allQuestions.currentPage;
		$scope.pageNumber3 = $scope.allQuestions.currentPage;
		$scope.pageSize3 = $scope.allQuestions.totalPages;
		$scope.size3 = $scope.allQuestions.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	$scope.searchQuestions = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allQuestions = getAllQuestionsService.getAllQuestions.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allQuestions);
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
		console.log($scope.comments);
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
		console.log($scope.deletedComments);
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
			console.log('inside function');
			$scope.searchByCommunity = " ";
		}
		$scope.deletedComments = deletedCommentsService.getComments.get({currentPage:currentPage2,communityId:$scope.searchByCommunity},function(response) {
			console.log($scope.deletedComments);
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
			console.log($scope.comments);
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
	
	$scope.allComments = getAllCommentsService.getAllComments.get({currentPage: currentPage3,title: $scope.title},function(response) {
		console.log($scope.allComments);
		totalPages3 = $scope.allComments.totalPages;
		currentPage3 = $scope.allComments.currentPage;
		$scope.pageNumber3 = $scope.allComments.currentPage;
		$scope.pageSize3 = $scope.allComments.totalPages;
		$scope.size3 = $scope.allComments.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	$scope.searchComments = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allComments = getAllCommentsService.getAllComments.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allComments);
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
			console.log('success');
		});
	}
	
	$scope.deleteCommentStatus = function() {
		deleteCommentsStatusService.deleteComment.get({id: $scope.deleteCommentData},function(response) {
			$scope.searchComments(currentPage3);
			$scope.getDeletedComments(currentPage2);
			$('#myModal6').modal('hide');
			console.log('success');
		});
	}
	
	$scope.deleteComment = function() {
		deleteReportedObjectCommentService.DeleteReportedObject.get({id: $scope.reportObjectId,commentId: $scope.commentId},function(response) {
			console.log('success');
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
		console.log($scope.answers);
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
			console.log($scope.answers);
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
			console.log('success');
			$('#myModal3').modal('hide');
			$scope.getAnswers(currentPage);
			$scope.getDeletedAnswers(currentPage2);
		});
	};
	
	$scope.deletedAnswers = deletedAnswersService.getAnswers.get({currentPage:currentPage2,communityId: $scope.searchByCommunity},function(response) {
		console.log($scope.deletedAnswers);
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
			console.log('inside function');
			$scope.searchByCommunity = " ";
		}
		$scope.deletedAnswers = deletedAnswersService.getAnswers.get({currentPage:currentPage2,communityId: $scope.searchByCommunity},function(response) {
			console.log($scope.deletedAnswers);
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
			console.log('success');
		});
	}
	
	$scope.deleteAnswerStatus = function() {
		deleteAnswersStatusService.deleteAnswer.get({id: $scope.deleteAnswerData},function(response) {
			$scope.getDeletedAnswers(currentPage2);
			$scope.searchAllAnswers(currentPage3);
			$('#myModal6').modal('hide');
			console.log('success');
		});
	}
	
	$scope.allAnswers = getAllAnswersService.getAllAnswers.get({currentPage: currentPage3,title: $scope.title},function(response) {
		console.log($scope.allAnswers);
		totalPages3 = $scope.allAnswers.totalPages;
		currentPage3 = $scope.allAnswers.currentPage;
		$scope.pageNumber3 = $scope.allAnswers.currentPage;
		$scope.pageSize3 = $scope.allAnswers.totalPages;
		$scope.size3 = $scope.allAnswers.size;
		
		if(totalPages3 == 0) {
			$scope.pageNumber3 = 0;
		}
	});
	
	
	$scope.searchAllAnswers = function(page) {
		currentPage3 = page;
		if(angular.isUndefined($scope.title) || $scope.title=="") {
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allAnswers = getAllAnswersService.getAllAnswers.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allAnswers);
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
		console.log($scope.communities);
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
			console.log($scope.communities);
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
		console.log($scope.allCommunities);
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
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allCommunities = getAllCommunitiesService.getAllCommunities.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allCommunities);
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
			console.log('success');
			$('#myModal3').modal('hide');
			$scope.getCommunities(currentPage);
			$scope.getDeletedCommunities(currentPage2);
		});
	};
	
	$scope.deletedCommunities = deletedCommunitiesService.getCommunities.get({currentPage:currentPage2},function(response) {
		console.log($scope.deletedCommunities);
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
			console.log($scope.deletedCommunities);
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
			console.log('success');
		});
	}
	
	$scope.deleteCommunityStatus = function() {
		deleteCommunityStatusService.deleteCommunity.get({id: $scope.deleteCommunityData},function(response) {
			$scope.getDeletedCommunities(currentPage2);
			$scope.searchCommunities(currentPage3);
			$('#myModal6').modal('hide');
			console.log('success');
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


minibean.controller('ManageUsers2Controller',function($scope, $http, $routeParams, reportedUsersService, deleteReportedObjectUserService, deletedUsersService, deleteUserStatusService, getAllUsersService){
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
		console.log($scope.users);
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
			console.log($scope.users);
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
		console.log($scope.deletedUsers);
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
			console.log($scope.deletedUsers);
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
	
	$scope.allUsers = getAllUsersService.getAllUsers.get({currentPage: currentPage3,title: $scope.title},function(response) {
		console.log($scope.allUsers);
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
			console.log('inside function');
			$scope.title = " ";
		}
		$scope.allUsers = getAllUsersService.getAllUsers.get({currentPage: currentPage3,title: $scope.title},function(response) {
			console.log($scope.allUsers);
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
			console.log('success');
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
			console.log('success');
		});
	}
	
	$scope.deleteUserStatus = function() {
		deleteUserStatusService.deleteUser.get({id: $scope.deleteUserData},function(response) {
			$scope.getDeletedUsers(currentPage2);
			$scope.searchUsers(currentPage3);
			$('#myModal6').modal('hide');
			console.log('success');
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

minibean.service('getAllUsersService',function($resource){
    this.getAllUsers = $resource(
            '/getAllUsers/:currentPage/:title',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});



minibean.controller('EditArticleController',function($scope, $http, $routeParams, $location, $upload, ArticleService, articleCategoryService, usSpinnerService){
    $scope.submitBtn = "Save";
    $scope.article = ArticleService.ArticleInfo.get({id:$routeParams.id});
    $scope.articleCategorys = articleCategoryService.getAllArticleCategory.get();
    
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

      




