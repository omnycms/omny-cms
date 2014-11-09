if(typeof console == "undefined") {
    console = {log:function(){}};
}

function versioned(url) {
    if(!url.indexOf("/")==0) {
        url = "/"+url;
    }
    return "/version/"+version+url;
}

var omnyApp = angular.module('omnyApp', [
  'ngRoute',
  'omnyControllers',
  'recursionHelper',
  'ui.bootstrap'
]);

omnyApp.config(['$routeProvider', '$locationProvider', '$compileProvider', '$controllerProvider',
  function($routeProvider, $locationProvider, $compileProvider, $controllerProvider) {
    window.directiveMaker = $compileProvider;
    window.controllerMaker = $controllerProvider;
    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix = '!';
    $routeProvider.
      otherwise({
        controller: 'OmnyDynamicController',
        templateUrl: 'templates/omny.html',
      });
  }]);
  
omnyApp.factory('Page', function() {
   var title = ' ';
   return {
     title: function() { return title; },
     setTitle: function(newTitle) { title = newTitle }
   };
});

omnyApp.filter('unsafe', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
});

requirejs.config({
    baseUrl: '/js',
    urlArgs: "v="+version,
    waitSeconds: 20,
    paths: {
        jquery: 'lib/jquery',
        themes: 'themes',
        ext: "https://modules.omny.ca"
    },
    shim: {
        "skel": {
            exports: "skel"
        },
        "skel-panels": {
            exports: "skel-panels"
        }
    }
});

function MainCtrl($scope, Page) {
  $scope.Page = Page;
}

