define(['lib/ckeditor/ckeditor','utilities/QueryStringReader'],
    function(ckeditor, queryStringReader) {
        function Youtube() {
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
                return "<div ng-controller=\"OmnyYoutube\" "+properties+" guid=\""+data.guid+"\"><omny-youtube></omny-youtube></div>"
            }
            
            this.getSample=function(guid) {
                var data = {"guid":guid, "video":null,"editable":true};
                window.omnyData[guid] = data;
                return this.getDirective(data);
            }
        }
        
        var ModalInstanceCtrl = function($scope, $modalInstance) {
            $scope.video = {"video":""};
            $scope.create = function() {
                $modalInstance.close($scope.video.video);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        function getSourceId(video) {
            return queryStringReader.getParameterFromUrl(video,"v");
        }
        
        function getSource(video) {
            return "https://www.youtube.com/embed/"+getSourceId(video);
        }
        
        directiveMaker.directive('omnyYoutube', ['$modal','$sce',function($modal,$sce) {
            return {
                restrict: 'E',
                templateUrl: versioned("/js/modules/Youtube/Youtube.html"),
                link: function(scope, ele, attrs) {
                    scope.changeUrl = function() {
                        var modalInstance = $modal.open({
                            templateUrl: versioned("/js/modules/Youtube/VideoDialog.html"),
                            controller: ModalInstanceCtrl,
                            size: 'sm'
                        });

                        modalInstance.result.then(function(video) {
                            scope.video = video;
                            scope.videoSource = $sce.trustAsResourceUrl(getSource(video));
                        });  
                    };
                }
            };
        }]);
        
        controllerMaker.register("OmnyYoutube",['$scope','$sce','$route','$element','$compile',
            function($scope,$sce,$route,$element,$compile) {
                var data = window.omnyData[$element.attr('guid')];
                //$scope.currentHtml = $sce.trustAsHtml(data.Html);
                $scope.video = data.video;
                if(data.editable) {
                    window.editableModuleScopes[data.guid] =  function() {
                        return {"data":{"video":$scope.video}, "omnyClass": "Omny.Youtube"};
                    };
                }
                
                $scope.editable = data.editable;
                $scope.currentHtml = data.Html;
        }]);
        
        return Youtube;
    }
);

