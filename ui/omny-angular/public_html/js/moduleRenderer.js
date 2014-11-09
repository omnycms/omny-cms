var moduleRenderer = angular.module('omnyModuleRenderer', ['jsLoader']);

moduleRenderer.factory('moduleRenderer', ['$q','dependencyLoader',function($q,dependencyLoader) {
  var renderer = {};
  
  var sectionPromiseGenerator = function(section, $scope, modules) {
        return function(results) {
            var result = "";
            for(var i=0; i<results.length; i++) {
                for(var j=0; j<results[i].length; j++) {
                    result += results[i][j][0];
                    modules.push(results[i][j][1]);
                }
            }
            $scope[section] = result;
            
        }
    };
    
    renderer.getFileName=function(omnyClass) {
        if(omnyClass.indexOf("Omny.")==0) {
            omnyClass = omnyClass.substring(5);
        }
        if(omnyClass.indexOf("ext.")==0) {
            omnyClass = omnyClass.substring(4);
            return "ext/"+omnyClass;
        } 
        
        return "modules/"+omnyClass+"/"+omnyClass;
    }

  renderer.renderModules=function($scope,templateModules,pageModules, callback) {
      var sections = {};
      var sectionPromises = {};
      var sectionData = {};
      for(var section in templateModules) {
          if(typeof sections[section]=="undefined") {
              sections[section] = "";
              sectionPromises[section] = [];
          }
          var result = renderer.renderSectionModules(templateModules[section],'template', $scope);
          sectionPromises[section].push(result);
      }
      
      for(var section in pageModules) {
          if(typeof sections[section]=="undefined") {
              sections[section] = "";
              sectionPromises[section] = [];
          }
          var result = renderer.renderSectionModules(pageModules[section],'page', $scope);
          sectionPromises[section].push(result);
      }
      var allPromises = [];
      var modules = [];
      for(var section in sections) {
          var sectionPromise = $q.all(sectionPromises[section]);
          sectionPromise.then(sectionPromiseGenerator(section,sectionData,modules));
          allPromises.push(sectionPromise);
      }
      $q.all(allPromises).then(function() {
         for(var section in sections) {
             if(sectionData[section]) {
                 sectionData[section] ='<div class="omny-module-section" section="'+section+'">'+sectionData[section]+'</div>';
             }
         } 
         callback(sectionData,modules);
         console.log($scope); 
      });
  }
  
  renderer.renderSectionModules = function(modules, type, $scope) {
    var promises = [];
    var results = [];
    for(var i=0; i<modules.length; i++) {
        if(typeof modules[i].data =="undefined") {
            modules[i].data = {};
        }
        modules[i].data.guid = "pre"+simpleguid();
        $scope[modules[i].data.guid] = modules[i].data;
        promises[i] = renderer.renderModule(modules[i],type);
    }

    return $q.all(promises).then(function(results) {
        return results;
    });
      
  }
  
  renderer.renderModule = function(module,type) {
      var deferred = $q.defer();
      if(window.editMode&&window.editMode==type) {
          module.data.editable = true;
      }
      dependencyLoader.loadDependency(renderer.getFileName(module.omnyClass), function(moduleConstructor) {
          var moduleObject = new moduleConstructor();
          moduleObject.getDirectivePromise(module.data,$q).then(function(result) {
                deferred.resolve([result,moduleObject]);
          });
      });
      
      return deferred.promise;
  }
  
  return renderer;
}]);



