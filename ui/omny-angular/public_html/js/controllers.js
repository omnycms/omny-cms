var omnyControllers = angular.module('omnyControllers', ['jsLoader', 'omnyConverters', 'omnyModuleRenderer']);

omnyControllers.directive('dynamic', function($compile) {
    return {
        restrict: 'A',
        replace: true,
        link: function(scope, ele, attrs) {
            scope.$watch(attrs.dynamic, function(html) {
                ele.html(html);
                $compile(ele.contents())(scope);
                
            });
        }
    };
});

omnyControllers.directive('omnyDialog', function() {
    return {
        restrict: 'E',
        link: function(scope, ele, attrs) {
            scope.$watch(attrs.currentText, function(text) {
                ele.dialog({autoOpen: false});
            });
            scope.openDialog = function() {
                ele.dialog('open');
            }
            scope.closeDialog = function() {
                ele.dialog('close');
            }
        },
        controller: function($scope, $element) {

        }
    };
});


omnyControllers.controller('OmnyDynamicController', ['$scope', '$http', 'Page',
    '$location', '$compile', 'directiveScopifier', 'mustacheApplier', 'dependencyLoader', 'moduleRenderer',
    function($scope, $http, Page, $location, $compile,
            directiveScopifier, mustacheApplier, dependencyLoader, renderer) {
        dependencyLoader.loadDependency("utilities/AuthTokenManager", function(tokenManager) {
            tokenManager.saveToken();
        });
        Page.setTitle("Omny");
        $scope.omny = {"html": ""};
        var pageName = $location.path().substring(1);
        if (pageName == "") {
            pageName = "default.html";
        }
        pageName = pageName.split(".");
        pageName = pageName[0];
        dependencyLoader.loadDependency("utilities/OmnyApiRequester", function(omnyApiRequester) {
            var ignoreSiteParameter = true;
            $http({
                method: 'GET',
                url: '/api/v1.0/pages/detailed',
                headers: {
                    'X-Origin': omnyApiRequester.getHostname(ignoreSiteParameter)
                },
                params: {
                    "page": pageName
                },
            })
                    .success(function(data) {
                        var port = window.location.port?":"+window.location.port:"";
                        var cssFile = "//"+window.location.hostname+port+"/themes/" + data.themeName + "/theme.css";
                        console.log(cssFile);
                        var head = document.getElementsByTagName('head')[0];
                        var link = document.createElement('link');
                        link.setAttribute("rel","stylesheet");
                        link.setAttribute("href",cssFile);
                        head.appendChild(link);
                        
                        console.log(data.themeHtml);
                        
                        var sectionData = {};
                        window.omnyData = $scope;
                        renderer.renderModules($scope, data['templateModules'], data['pageModules'], function(sectionData) {

                            omnyApiRequester.apiRequest("sites", omnyApiRequester.getHostname(), {
                                site: "www",
                                success: function(siteData) {
                                    sectionData.site = siteData;
                                    document.title = siteData.siteName +" - "+ data.page.title;
                                    $scope.$apply(function() {
                                        $scope.omny.html = mustacheApplier.replaceText(data.themeHtml, sectionData);
                                    });
                                    dependencyLoader.loadDependency("themes/" + data.themeName + "/theme.js", function(theme) {
                                        console.log(theme);
                                        $scope.$apply(function() {
                                            $scope.display = true;
                                        });
                                        if(typeof theme!="undefined"&& typeof theme.load!="undefined") {
                                            console.log("loading theme");
                                            theme.load();
                                        }
                                        $("#wrapper").show();
                                        //$(window).trigger("resize");
                                    });
                                }
                            });

                        });
                    });
        });
    }]);