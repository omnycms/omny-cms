var jsLoader = angular.module('jsLoader', []);

jsLoader.factory('dependencyLoader', function() {
   var loader = {};
   
   loader.loadDependency= function(dependency,callback) {
        require([dependency], function(util) {
           callback(util); 
        }, function(error) {
            console.log(error);
        });
   };
   
   return loader;
});