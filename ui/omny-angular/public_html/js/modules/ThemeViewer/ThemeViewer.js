define(['utilities/OmnyApiRequester', 'utilities/QueryStringReader'],
        function(apiRequester, queryStringReader) {

            function ThemeViewer() {
                this.getDirectivePromise = function(data, $q) {
                    var deferred = $q.defer();
                    deferred.resolve("<div ng-controller=\"OmnyThemeViewer\" guid=\"" + data.guid + "\"><omny-theme-viewer></omny-theme-viewer></div>");
                    return deferred.promise;
                };
            }

            directiveMaker.directive('omnyThemeViewer', ['$location',function($location) {
                    return {
                        restrict: 'E',
                        templateUrl: versioned("/js/modules/ThemeViewer/Themes.html"),
                        link: function(scope, ele, attrs) {
                            scope.setDefault = function(themeName) {
                                apiRequester.apiRequest("themes", themeName, {
                                    type: "POST",
                                    data: JSON.stringify({
                                        fromTheme: themeName
                                    }),
                                    contentType: "application/json",
                                    success: function(data) {
                                        apiRequester.apiRequest("themes","default", {
                                            type: "POST",
                                            data: JSON.stringify({
                                                name: themeName
                                            }),
                                            success: function(data) {
                                                window.location =( '/newPage.html?page=default&site='+queryStringReader.getParameter("site"));
                                            }
                                        });
                                    }
                                });
                                
                            }
                        },
                    };
                }
            ]);

            controllerMaker.register("OmnyThemeViewer", ['$scope', '$route', '$element',
                function($scope, $route, $element) {
                    var data = window.omnyData[$element.attr('guid')];
                    apiRequester.apiRequest("themes", "installable", {
                        success: function(themes) {
                            for (var i = 0; i < themes.length; i++) {
                                themes[i]['description'] = "This is a sample theme";
                            }
                            $scope.$apply(function() {
                                $scope.themes = themes;
                            });
                        }
                    });
                }
            ]);

            return ThemeViewer;
        }
);