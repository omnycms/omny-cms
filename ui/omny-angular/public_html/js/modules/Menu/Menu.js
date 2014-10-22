define(['utilities/OmnyApiRequester', 'lib/jquery.dropotron'],
        function(apiRequester, dropotron) {

            var link = document.createElement("link");
            link.type = "text/css";
            link.rel = "stylesheet";
            link.href = "/js/modules/Menu/menu.css";
            document.getElementsByTagName("head")[0].appendChild(link);

            function Menu() {
                this.getDirectivePromise = function(data, $q) {
                    var deferred = $q.defer();

                    deferred.resolve(this.getDirective(data));
                    //deferred.resolve(data.Text);
                    return deferred.promise;
                };

                this.getDirective = function(data) {
                    var properties = "";
                    if (data.editable) {
                        properties += 'class="omny-editable-module"';
                    } else {
                        properties += 'class="notEditable"';
                    }
                    return "<div ng-controller=\"OmnyMenu\" " + properties + " guid=\"" + data.guid + "\"><ul omny-menu class=\"root omny-menu sm sm-clean\" menuitems=\"menuitems\"></ul></div>";
                }

                this.getSample = function(guid) {
                    var data = {"guid": guid, "editable": true};
                    window.omnyData[guid] = data;
                    return this.getDirective(data);
                }
            }

            directiveMaker.directive('omnyMenu', ['RecursionHelper', function(recursionHelper, $timeout) {
                    return {
                        restrict: 'AE',
                        templateUrl: versioned("/js/modules/Menu/Menu.html"),
                        scope: {
                            menuitems: "=menuitems"
                        },
                        link: function(scope, ele, attrs) {
                            scope.$watch(attrs.menuitems, function(menuitems) {
                                scope.$evalAsync( function(scope) {
                                    /*ele.dropotron({
                                        offsetY: -20,
                                        offsetX: -1,
                                        mode: 'fade',
                                        noOpenerFade: true,
                                        alignment: 'center',
                                        detach: true,
                                        cloneOnDetach: true,
                                        menuClass: 'dropotron'
                                    });*/
                                    
                                    ele.addClass("sm-vertical sm-clean-vertical");
                                    ele.smartmenus();
                                    
                                });
                            });
                            
                            scope.refresh = function() {
                                ele.smartmenus("refresh");
                            }
                        }
                    };
                }]);

            controllerMaker.register("OmnyMenuLinks", ['$scope','$element',
                function($scope, element) {
                    $scope.menuitems = $scope.menuItem.children;
                }]);

            controllerMaker.register("OmnyMenu", ['$scope', '$sce', '$route', '$element', '$compile',
                function($scope, $sce, $route, $element, $compile) {
                    var data = window.omnyData[$element.attr('guid')];
                    var menu = data.menu ? data.menu : "default";
                    if (data.editable) {
                        $scope.editable = data.editable;
                        window.editableModuleScopes[data.guid] = function() {
                            return {"omnyClass": "Omny.Menu"};
                        };
                    }
                    apiRequester.apiRequest("menus", "default/entries", {
                        type: "GET",
                        success: function(menuItems) {
                            $scope.$apply(function() {
                                $scope.menuitems = menuItems;
                            });
                        }
                    });
                }]);

            return Menu;
        }
);




