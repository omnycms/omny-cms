define(['utilities/OmnyApiRequester', 'utilities/QueryStringReader'],
        function(apiRequester, queryStringReader) {
            function PageManager() {
                this.getDirectivePromise = function(data, $q) {
                    var deferred = $q.defer();
                    var properties = "";
                    if (data.editable) {
                        properties += 'class="omny-editable-module"';
                    } else {
                        properties += 'class="notEditable"';
                    }
                    deferred.resolve("<div ng-controller=\"OmnyPageManager\" " + properties + " guid=\"" + data.guid + "\"><omny-page-manager ></omny-page-manager></div>");
                    return deferred.promise;
                };
            }

            directiveMaker.directive('omnyPageManager', ['RecursionHelper', '$modal', function(recursionHelper, $modal) {
                    return {
                        restrict: 'AE',
                        templateUrl: versioned("/js/modules/PageManager/PageManager.html"),
                        link: function(scope, ele, attrs) {
                            scope.cancelPage = function() {
                                scope.newPage = {};
                            };
                            scope.createPage = function() {
                                apiRequester.apiRequest("sites", "", {
                                    type: "POST",
                                    data: scope.newSite,
                                    success: function() {
                                        window.location = ('/themes.html?default=true&site=' + scope.newSite.subdomain);
                                    }, error: function(err) {
                                        console.log(err);
                                    }
                                });
                            };
                            scope.openDialog = function() {
                                var modalInstance = $modal.open({
                                    templateUrl: versioned("/js/modules/PageManager/NewPageDialog.html"),
                                    controller: ModalInstanceCtrl,
                                    size: 'sm'
                                });

                                modalInstance.result.then(function(newPage) {

                                    if (newPage.automaticUrl) {
                                        newPage.url = getPageUrl(newPage.pageName);
                                    }

                                    window.location = "/newPage.html?site=" + queryStringReader.getParameter("site") + "&page=" + newPage.url + "&title=" + newPage.pageName;
                                }, function() {
                                    console.log('Modal dismissed at: ' + new Date());
                                });
                            };
                        }
                    };
                }]);

            directiveMaker.directive('omnyPageManagerLinks', ['RecursionHelper', '$modal', function(recursionHelper, $modal) {
                    return {
                        restrict: 'AE',
                        scope: {
                            pages: "=pages",
                            site: "=site"
                        },
                        templateUrl: versioned("/js/modules/PageManager/Links.html"),
                        link: function(scope, ele, attrs) {
                            console.log("linking");
                            scope.openDialog = function(page) {
                                console.log("test");
                                var modalInstance = $modal.open({
                                    templateUrl: versioned("/js/modules/PageManager/NewPageDialog.html"),
                                    controller: ModalInstanceCtrl,
                                    size: 'sm'
                                });

                                modalInstance.result.then(function(newPage) {

                                    if (newPage.automaticUrl) {
                                        newPage.url = getPageUrl(newPage.pageName);
                                    }
                                    if (page) {
                                        var linkParts = page.link.substring(1).split("/");
                                        if (linkParts.length > 0) {
                                            var prefix = "";
                                            for (var i = 0; i < linkParts.length; i++) {
                                                prefix += linkParts[i].replace(".html", "") + "/";
                                            }
                                            newPage.url = prefix + newPage.url;
                                        }
                                    }
                                    //newPage.url+=".html";
                                    console.log(newPage);
                                    console.log(page);
                                    window.location = "/newPage.html?site=" + queryStringReader.getParameter("site") + "&page=" + newPage.url + "&title=" + newPage.pageName;
                                }, function() {
                                    console.log('Modal dismissed at: ' + new Date());
                                });
                            };
                        }
                    };
                }]);

            function getPageUrl(name) {
                return name.replace(/\s/g, "-").replace(/[^A-Za-z0-9-]/g, "");
            }

            function sortPages(firstPage, secondPage) {
                var firstLength = firstPage.link.split("/").length;
                var secondLength = secondPage.link.split("/").length;
                return firstLength - secondLength;
            }

            function getStructuredPages(pages) {
                pages.sort(sortPages);
                var newPages = [];
                var pageIndex = {};
                for (var i = 0; i < pages.length; i++) {
                    var page = pages[i];
                    var parts = page.link.substring(1).split("/");
                    page.children = [];
                    pageIndex[page.link] = page;
                    if (parts.length == 1) {
                        newPages.push(page);
                    } else {
                        var parentLink = "";
                        for (var j = 0; j < parts.length - 1; j++) {
                            if (j > 0) {
                                parentLink += "/";
                            }
                            parentLink += parts[j];
                        }
                        var parent = pageIndex[parentLink];
                        parent.children.push(page);
                    }
                }
                return newPages;
            }

            var ModalInstanceCtrl = function($scope, $modalInstance) {
                $scope.newPage = {"automaticUrl": true};
                $scope.create = function() {
                    $modalInstance.close($scope.newPage);
                };

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };
            };

            controllerMaker.register("OmnyChildLinks", ['$scope',
                function($scope) {

                    $scope.pages = $scope.page.children;
                }]);

            function addEditLink(pages) {
                for (var i = 0; i < pages.length; i++) {
                    pages[i].editLink = pages[i].link.substring(1).replace(".html", "");
                    if (pages[i].children) {
                        addEditLink(pages[i].children);
                    }
                }
                return pages;
            }

            controllerMaker.register("OmnyPageManager", ['$scope', '$route', '$element', '$compile',
                function($scope, $route, $element, $compile) {
                    var data = window.omnyData[$element.attr('guid')];
                    apiRequester.apiRequest("menus", "default/entries", {
                        success: function(pages) {
                            $scope.$apply(function() {
                                var site = queryStringReader.getParameter("site");
                                $scope.site = site;
                                //var newPages = getStructuredPages(pages)
                                $scope.pages = addEditLink(pages);
                            });
                        }
                    });
                    
                    
                }]);

            return PageManager;
        }
);