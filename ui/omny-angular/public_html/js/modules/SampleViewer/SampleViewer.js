define(['utilities/OmnyApiRequester', 'utilities/QueryStringReader'],
    function(apiRequester, queryStringReader) {

        function ThemeViewer() {
            this.getDirectivePromise = function(data, $q) {
                var deferred = $q.defer();
                deferred.resolve("<div ng-controller=\"OmnySampleViewer\" guid=\"" + data.guid + "\"><omny-sample-viewer></omny-theme-viewer></div>");
                return deferred.promise;
            };
        }

        directiveMaker.directive('omnySampleViewer', ['$location', function($location) {
                return {
                    restrict: 'E',
                    templateUrl: versioned("/js/modules/SampleViewer/Samples.html"),
                    link: function(scope, ele, attrs) {
                        scope.createPage = function(sampleName) {
                            var pageTitle = queryStringReader.getParameter("title");
                            var pageName = queryStringReader.getParameter("page");
                            if(typeof pageTitle =="undefined") {
                                pageTitle = pageName;
                            }
                            
                            if(pageTitle=="default") {
                                pageTitle = "Home";
                            }
                            apiRequester.apiRequest("pages", "", {
                                type: "POST",
                                data: JSON.stringify({
                                    "name": pageName,
                                    "fromSample": {
                                        "sampleName": sampleName
                                    },
                                    "pageDetails": {
                                        "title": pageTitle
                                    }
                                }),
                                processData: false,
                                contentType: 'application/json',
                                success: function(data) {
                                    window.location = window.location.origin+"/site/page.html?site="+queryStringReader.getParameter("site")+"&page="+pageName;
                                }
                            });
                        }
                    },
                };
            }
        ]);
        
        controllerMaker.register("OmnySampleViewer", ['$scope', '$route', '$element',
            function($scope, $route, $element) {
                var data = window.omnyData[$element.attr('guid')];
                apiRequester.apiRequest("themes", "default/samples", {
                    success: function(samples) {
                        $scope.$apply(function() {
                            $scope.samples = samples;
                        });
                    }
                });
            }
        ]);
        return ThemeViewer;
    }
);