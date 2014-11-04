function deleteModule(button) {
    var module = $(button).parent().parent();
    module.remove();
}

define(['utilities/OmnyApiRequester', 'utilities/QueryStringReader','lib/notify.min','jquery'],
        function(apiRequester, queryStringReader,notify,$) {
            function PageMaster() {
                this.getDirectivePromise = function(data, $q) {
                    var deferred = $q.defer();
                    deferred.resolve("<div style=\"height: 100%\" ng-controller=\"PageMaster\" guid=\"" + data.guid + "\"><omny-page-master></omny-page-master></div>");
                    return deferred.promise;
                };
            }
            
            var dragHandle = '<div style="display: none;" ng-if="editable" class="omny-module-options"><span class="omny-drag-handle glyphicon glyphicon-move"></span><span onclick="deleteModule(this)" class="omny-delete-module glyphicon glyphicon-remove"></span></div>';
            
            directiveMaker.directive('omnyPageMaster', ['$compile', '$modal', function($compile, $modal) {
                    return {
                        restrict: 'E',
                        templateUrl: versioned("/js/modules/PageMaster/PageMaster.html"),
                        link: function(scope, ele, attrs) {
                            scope.toggleSidebar = function() {
                                var moduleBlockMenu = $(".omny-module-menu");
                                if (moduleBlockMenu.hasClass("omny-open-sidebar")) {
                                    moduleBlockMenu.removeClass("omny-open-sidebar");
                                    $("#omny-module-sidebar").hide();
                                } else {
                                    moduleBlockMenu.addClass("omny-open-sidebar");
                                    $("#omny-module-sidebar").show();
                                }
                            }
                            scope.init = function() {
                                $(".omny-module-section").sortable({
                                    items: ".omny-editable-module",
                                    placeholder: "ui-state-highlight",
                                    connectWith: ".omny-module-section",
                                    dropOnEmpty: true,
                                    forcePlaceholderSize: true,
                                    forceHelperSize: true,
                                    tolerance: "pointer",
                                    cancel: '[contenteditable]',
                                    stop: function(event, ui) {
                                        if (ui.item.hasClass("omny-module")) {
                                            var guid = "pre" + window.simpleguid();
                                            var moduleText = new window.moduleBuilders[ui.item.attr("module")]().getSample(guid);

                                            var x = document.createElement("div");
                                            $(x).html(moduleText);
                                            var module = $(x).find(".omny-editable-module");
                                            module.prepend(dragHandle);
                                            $compile(x)(window.omnyData);
                                            ui.item.replaceWith(x);
                                            
                                        }
                                    }
                                });
                                //$(".moduleSection").disableSelection();

                                $(".omny-module").draggable({
                                    connectToSortable: ".omny-module-section",
                                    helper: 'clone',
                                    revert: "invalid",
                                    start: function() {
                                        //$("#sidebar").hide();
                                    },
                                    stop: function(event, ui) {
                                        $("#sidebar").show();
                                    }
                                });
                            }
                            scope.viewPage = function() {
                                var port = window.location.port;
                                port = (port==80||port==443)?"":":"+port;
                                window.location = "http://"+queryStringReader.getParameter("site")+".omny.me"+port+"/"+queryStringReader.getParameter("page")+".html";
                            };
                            scope.savePage = function() {
                                var moduleData = {};
                                $(".omny-module-section").each(function() {
                                    var section = $(this).attr("section");
                                    moduleData[section] = [];
                                    $(this).find(".omny-editable-module").each(function() {
                                       var id = $(this).attr("guid");
                                       var data = window.editableModuleScopes[id]();
                                       moduleData[section].push(data);
                                    });
                                });
                                var pageName = queryStringReader.getParameter("page");
                                apiRequester.apiRequest("pages","modules", {
                                   type: "PUT",
                                   processData: false,
                                   contentType: 'application/json',
                                   data : JSON.stringify({
                                      pageModules: moduleData,
                                      pageName: pageName
                                   }),
                                   success: function(data) {
                                       $.notify("Page Saved",{position: "top center", className: "success", autoHideDelay: 3000});
                                   }
                                });
                            }
                        }
                    };
                }]);

            controllerMaker.register("PageMaster", ['$scope', '$route', '$element', '$compile', 'moduleRenderer', '$http', 'mustacheApplier', 'dependencyLoader',
                function($scope, $route, $element, $compile, renderer, $http, mustacheApplier, dependencyLoader) {
                    window.mustacheApplier = mustacheApplier;
                    var data = window.omnyData[$element[0].getAttribute('guid')];
                    $(".omny-module-section").removeClass("omny-module-section");
                    window.moduleBuilders = {};
                    window.editableModuleScopes = {};
                    var port = window.location.port;
                    port = (port==80||port==443)?"":":"+port;
                    $scope.pageLink = "http://"+queryStringReader.getParameter("site")+".omny.me"+port+"/"+queryStringReader.getParameter("page")+".html";
                    $http({
                        method: 'GET',
                        url: versioned('/js/modules/PageMaster/MasterModules.json'),
                    })
                            .success(function(modules) {
                                $scope.showSidebar = false;
                                $scope.modules = modules;
                                for (var i = 0; i < modules.length; i++) {
                                    var module = modules[i];

                                    var callbackBuilder = function(module) {
                                        return function(moduleConstructor) {
                                            window.moduleBuilders[module.module] = moduleConstructor;
                                        }
                                    };
                                    dependencyLoader.loadDependency(renderer.getFileName(module.module), callbackBuilder(module));
                                }
                            });
                    $http({
                        method: 'GET',
                        url: '/api/v1.0/pages/detailed',
                        headers: {
                            'X-Origin': queryStringReader.getParameter("site")
                        },
                        params: {
                            "page": queryStringReader.getParameter("page")
                        },
                    })
                            .success(function(data) {
                                
                                var cssFile = "http://"+apiRequester.getFullHostname()+"/themes/" + data.themeName + "/theme.css";
                                console.log(cssFile);
                                var head = document.getElementsByTagName('head')[0];
                                var link = document.createElement('link');
                                link.setAttribute("rel","stylesheet");
                                link.setAttribute("href",cssFile);
                                head.appendChild(link);
                                
                                console.log(data.themeHtml);
                                var sectionData = {};
                                window.omnyData = $scope;
                                window.editMode = "page";

                                renderer.renderModules($scope, data['templateModules'], data['pageModules'], function(sectionData, modules) {
                                    apiRequester.apiRequest("sites", apiRequester.getHostname(), {
                                        success: function(siteData) {
                                            sectionData.site = siteData;
                                            $scope.$apply(function() {
                                                $scope.pageHtml = mustacheApplier.replaceText(data.themeHtml, sectionData);
                                            });
                                            dependencyLoader.loadDependency("//"+apiRequester.getFullHostname()+"/themes/" + data.themeName + "/theme.js", function(theme) {
                                                $scope.$apply(function() {
                                                    $scope.init();
                                                });
                                                if(theme&&theme.load) {
                                                    theme.load();
                                                }
                                                $("#omny-sidebar").show();
                                            })
                                            console.log(modules);
                                            $(".omny-editable-module").prepend(dragHandle);
                                        }
                                    });
                                });
                            });
                }]);

            return PageMaster;
        }
);