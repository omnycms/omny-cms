define(['utilities/QueryStringReader'],
    function(queryStringReader) {
        
        directiveMaker.directive('omnyDevModule', function() {
            return {
                restrict: 'E',
                link: function(scope, ele, attrs) {
                    var data = scope.data;
                    if(data.editable) {
                        //default save
                        window.editableModuleScopes[data.guid] =  function() {
                            return {"data":{}, "omnyClass": "Omny.DevModule"};
                        };
                    }
                
                    if(data.editable|| queryStringReader.getParameter("devtest")) {
                        require([data.url], function(moduleConstructor) {
                            var module = new moduleConstructor();
                            if(module.getDirective) {
                                $(ele).html(module.getDirective());
                            } else if (module.render) {
                                module.render(ele[0]);
                            }
                            if(data.editable) {
                                //override save if module loads
                                window.editableModuleScopes[data.guid] =  function() {
                                        var moduleData = {};
                                        if(module.getData) {
                                            moduleData = module.getData();
                                        }
                                    return {"data":moduleData, "omnyClass": "Omny.DevModule"};
                                };
                            }
                        });
                    }
                }
            };
        });
        
        controllerMaker.register("OmnyDevModule",['$scope','$sce','$route','$element','$compile',
            function($scope,$sce,$route,$element,$compile) {
                var data = window.omnyData[$element.attr('guid')];
                $scope.data = data;
                
                $scope.editable = data.editable;
                $scope.currentHtml = data.Html;
        }]);
        
        function DevModule() {
            this.data = {"editable":true};
            var moduleInstance = this;

            this.getDirectivePromise = function(data,$q) {
                var deferred = $q.defer();
                
                deferred.resolve(this.getDirective(data));
                //deferred.resolve(data.Html);
                return deferred.promise;
            };
            
            this.getDirective = function(data) {
                var properties = "";
                if(data.editable) {
                    properties+= 'class="omny-editable-module"';
                }
                return "<div ng-controller=\"OmnyDevModule\" "+properties+" guid=\""+data.guid+"\"><omny-dev-module></omny-dev-module></div>"
            }
            
            this.afterAdded = function() {
                console.log("updating");
            };

            this.getSample=function(guid,$q) {
                this.data.guid = guid;
                window.omnyData[guid] = this.data;
                return this.getDirectivePromise(this.data,$q);
            }
        }
        return DevModule;
    }
);

