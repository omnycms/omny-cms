define(['utilities/QueryStringReader'],
    function(queryStringReader) {
        function Login() {
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
                return "<div ng-controller=\"OmnyLogin\" "+properties+" guid=\""+data.guid+"\"><omny-login></omny-login></div>"
            }
            
            this.getSample=function(guid) {
                var data = {"guid":guid};
                window.omnyData[guid] = data;
                return this.getDirective(data);
            }
        }
        
        directiveMaker.directive('omnyLogin', [function() {
            return {
                restrict: 'E',
                templateUrl: versioned("/js/modules/Login/Login.html"),
            };
        }]);
        
        controllerMaker.register("OmnyLogin",['$scope','$element',
            function($scope,$element) {
                var data = window.omnyData[$element.attr('guid')];
        }]);
        
        return Login;
    }
);

