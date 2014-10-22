define([],
    function() {
        function Text() {
            this.getDirectivePromise = function(data,$q) {
                var deferred = $q.defer();
                deferred.resolve("<div ng-controller=\"OmnyText\" guid=\""+data.guid+"\"><omny-text-manager></omny-text-manager></div>");
                //deferred.resolve(data.Text);
                return deferred.promise;
            };
        }
        
        directiveMaker.directive('omnyTextManager', function() {
            return {
                restrict: 'E',
                templateUrl: versioned("/js/modules/Text/Text.html"),
            };
        });
        
        controllerMaker.register("OmnyText",['$scope','$sce','$route','$element','$compile',
            function($scope,$sce,$route,$element,$compile) {
                var data = window.omnyData[$element.attr('guid')];
                //$scope.currentText = $sce.trustAsHtml(data.Text);
                $scope.currentText = data.Text;
        }]);
        
        return Text;
    }
);

