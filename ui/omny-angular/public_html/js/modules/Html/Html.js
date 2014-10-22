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
                return "<div ng-controller=\"OmnyHtml\" "+properties+" guid=\""+data.guid+"\"><omny-html-manager></omny-html-manager></div>"
            }
            
            this.getSample=function(guid) {
                var data = {"guid":guid, "Html":"Sample Data","editable":true};
                window.omnyData[guid] = data;
                return this.getDirective(data);
            }
        }
        
        directiveMaker.directive('omnyHtmlManager', function() {
            return {
                restrict: 'E',
                templateUrl: versioned("/js/modules/Html/Html.html"),
                link: function(scope, ele, attrs) {
                    if(scope.editable) {
                        scope.$evalAsync( function(scope) {
                            //scope.edit = scope.openDialog;
                            var contentSection = ele.find(".omny-content");
                            contentSection.attr("contenteditable","true");
                            contentSection.html(scope.currentHtml);

                            window.CKEDITOR.disableAutoInline = true;
                            var toolBarItems = ["Bold","Italic","Underline","Undo","Redo","Sourcedialog"];
                            var toolbar = {"toolbar":[toolBarItems]};
                            var editor = window.CKEDITOR.inline(contentSection[0],toolbar);
                            contentSection.attr("contenteditable","true");
                            scope.editor = editor;
                            editor.on("instanceReady", function() { 
                                editor.setReadOnly(false);
                            });
                        });
                    } else {
                        scope.edit = function(){};
                    }
                }
            };
        });
        
        controllerMaker.register("OmnyHtml",['$scope','$sce','$route','$element','$compile',
            function($scope,$sce,$route,$element,$compile) {
                var data = window.omnyData[$element.attr('guid')];
                //$scope.currentHtml = $sce.trustAsHtml(data.Html);
                if(data.editable) {
                    window.editableModuleScopes[data.guid] =  function() {
                        return {"data":{"Html":$scope.editor.getData()}, "omnyClass": "Omny.Html"};
                    };
                }
                
                $scope.editable = data.editable;
                $scope.currentHtml = data.Html;
        }]);
        
        return Html;
    }
);

