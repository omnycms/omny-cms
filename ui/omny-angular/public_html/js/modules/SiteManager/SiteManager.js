define(['utilities/OmnyApiRequester'],
        function(apiRequester) {
            function SiteManager() {
                this.getDirectivePromise = function(data, $q) {
                    var deferred = $q.defer();
                    deferred.resolve("<div ng-controller=\"OmnySite\" guid=\"" + data.guid + "\"><omny-site-manager></omny-site-manager></div>");
                    return deferred.promise;
                };
            }

            directiveMaker.directive('omnySiteManager', ['$location','$modal', function($location,$modal) {
                    return {
                        restrict: 'E',
                        templateUrl: versioned("/js/modules/SiteManager/Site.html"),
                        link: function(scope, ele, attrs) {
                            scope.openDialog = function() {
                                var modalInstance = $modal.open({
                                    templateUrl: versioned("/js/modules/SiteManager/NewSite.html"),
                                    controller: ModalInstanceCtrl,
                                    size: 'sm'
                                });

                                modalInstance.result.then(function(newSite) {
                                    apiRequester.apiRequest("sites", "", {
                                        type: "POST",
                                        data: JSON.stringify(newSite),
                                        contentType: 'application/json',
                                        success: function() {
                                            window.location = window.location.origin+('/themes.html?default=true&site=' + newSite.subdomain);
                                        }, error: function(err) {
                                            console.log(err);
                                        }
                                    });

                                });
                            }
                        }
                    };
                }]);

            var ModalInstanceCtrl = function($scope, $modalInstance) {
                $scope.newSite = {};
                $scope.create = function() {
                    $modalInstance.close($scope.newSite);
                };

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };
            };

            var SUBDOMAIN_REGEXP = /^[a-z-]+$/;
            directiveMaker.directive('subdomain', function() {
                return {
                    require: 'ngModel',
                    link: function(scope, elm, attrs, ctrl) {
                        ctrl.$parsers.unshift(function(viewValue) {
                            if (SUBDOMAIN_REGEXP.test(viewValue)) {
                                // it is valid
                                ctrl.$setValidity('subdomain', true);
                                return viewValue;
                            } else {
                                // it is invalid, return undefined (no model update)
                                ctrl.$setValidity('subdomain', false);
                                return undefined;
                            }
                        });
                    }
                };
            });

            controllerMaker.register("OmnySite", ['$scope', '$route', '$element', '$compile',
                function($scope, $route, $element, $compile) {
                    var data = window.omnyData[$element[0].getAttribute('guid')];
                    apiRequester.apiRequest("sites", "", {
                        success: function(siteData) {
                            $scope.$apply(function() {
                                $scope.sites = siteData;
                            });
                        }
                    });
                }]);

            return SiteManager;
        }
);