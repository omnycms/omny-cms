define(['lib/ckeditor/ckeditor','utilities/QueryStringReader'],
    function(ckeditor, queryStringReader) {
        function Photo() {
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
                return "<div ng-controller=\"OmnyPhoto\" "+properties+" guid=\""+data.guid+"\"><omny-photo></omny-photo></div>"
            }
            
            this.getSample=function(guid) {
                var data = {"guid":guid, "video":null,"editable":true};
                window.omnyData[guid] = data;
                return this.getDirective(data);
            }
        }
        
        var ModalInstanceCtrl = function($scope, $modalInstance) {
            $scope.photo = {"src":""};
            $scope.create = function() {
                $modalInstance.close($scope.photo);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        directiveMaker.directive('omnyPhoto', ['$modal','$sce',function($modal,$sce) {
            return {
                restrict: 'E',
                templateUrl: versioned("/js/modules/Photo/Photo.html"),
                link: function(scope, ele, attrs) {
                    scope.changeUrl = function() {
                        var modalInstance = $modal.open({
                            templateUrl: versioned("/js/modules/Photo/PhotoDialog.html"),
                            controller: ModalInstanceCtrl,
                            size: 'sm'
                        });

                        modalInstance.result.then(function(photo) {
                            scope.photo = photo;
                            scope.src = $sce.trustAsResourceUrl(photo.src);
                        });  
                    };
                }
            };
        }]);
        
        controllerMaker.register("OmnyPhoto",['$scope','$sce','$route','$element','$compile',
            function($scope,$sce,$route,$element,$compile) {
                var data = window.omnyData[$element.attr('guid')];
                //$scope.currentHtml = $sce.trustAsHtml(data.Html);
                $scope.video = data.video;
                if(data.editable) {
                    window.editableModuleScopes[data.guid] =  function() {
                        return {"data":{"src":$scope.src}, "omnyClass": "Omny.Photo"};
                    };
                }
                
                $scope.editable = data.editable;
        }]);
        
        return Photo;
    }
);

