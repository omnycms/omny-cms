define(['lib/ckeditor/ckeditor'],
    function(ckeditor) {
        function Html() {
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
                var customAttributes = "";
                if(data.customAttributes) {
                    for(var key in data.customAttributes) {
                        customAttributes+= key+'="'+data.customAttributes[key]+'" ';
                    }
                }
                return "<div ng-controller=\"OmnyWebComponent\" "+properties+" guid=\""+data.guid+"\"><link rel=\"import\" href=\""+data.url+"\" /><"+data.tag+" "+customAttributes+"></"+data.tag+"></div>"
            }
            
            this.getSample=function(guid) {
                var data = {"guid":guid, "url":"http://h3manth.com/demo/custom-elements/xkcd-img/src/xkcd-img.html","tag":"xkcd-img","editable":true};
                window.omnyData[guid] = data;
                return this.getDirective(data);
            }
        }
        
        controllerMaker.register("OmnyWebComponent",['$scope','$element',
            function($scope,$element) {
                var data = window.omnyData[$element.attr('guid')];
                if(data.editable) {
                    window.editableModuleScopes[data.guid] =  function() {
                        return {"data":{"url":$scope.url}, "tag": $scope.tag};
                    };
                }
                
                $scope.editable = data.editable;
        }]);
        
        return Html;
    }
);

