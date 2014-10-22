var converters = angular.module('omnyConverters', ['omnyMainConverters']);

converters.factory('converterManager', ['mainConverter',function(mainConverter) {
  var manager = {};
  manager.converters = {};

  manager.getConverter=function(name) {
    return manager.converters[name];
  }
  
  manager.registerConverter = function(name,converter) {
      manager.converters[name] = converter;
  }
  
  mainConverter.registerConverters(manager);

  return manager;
}]);

converters.factory('converterRunner', ['converterManager',function(converterManager) {
  var runner = {};
  
  runner.getDirectivesText=function(modulesData,type) {
      var result ="";
      for(i=0; i<modulesData.length; i++) {
          var moduleData = modulesData[i];
          var converter = converterManager.getConverter(moduleData.omnyClass);
          if(typeof converter!="undefined") {
            result += converter(moduleData.data,type)+"\n";
          }
          else {
            console.log("warning: converted not found for "+moduleData.omnyClass);
          }
      }
      return result;
  }

  return runner;
}]);

converters.factory('mustacheApplier',[function() {
    var runner = {};
    runner.replaceText = function(text, data) {
        return text.replace(/{{([a-zA-Z.]*)}}/g, function(m, key) {
            return runner.findElement(data, key);
        });
    }

    runner.findElement = function(data, key) {
        //console.log(data+" "+key);
        if (key.indexOf(".") == 1) {
            return data.hasOwnProperty(key) ? data[key] : "";
        }
        var dataKeys = key.split(".");
        return runner.findValue(dataKeys, data);
    }

    runner.findValue = function(dataKeys, data) {
        if (data.hasOwnProperty(dataKeys[0])) {
            if (dataKeys.length == 1) {
                return data[dataKeys[0]];
            } else {
                var key = dataKeys[0];
                dataKeys.splice(0, 1);
                return runner.findValue(dataKeys, data[key]);
            }
        }
        return "";
    }
    return runner;
}]);

converters.factory('directiveScopifier', ['converterRunner',function(converterRunner) {
  var runner = {};
  
  runner.bringDirectivesIntoScope=function($scope,templateModules,pageModules) {
      var sections = {};
      for(var section in templateModules) {
          if(typeof sections[section]=="undefined") {
              sections[section] = "";
          }
          sections[section] += converterRunner.getDirectivesText(templateModules[section],'template');
      }
      
      for(var section in pageModules) {
          if(typeof sections[section]=="undefined") {
              sections[section] = "";
          }
          sections[section] += converterRunner.getDirectivesText(pageModules[section],'page');
      }
      for(var section in sections) {
          console.log("section "+section+" value "+sections[section]);
          $scope[section] = sections[section];
      }
  }

  return runner;
}]);

var mainConverters = angular.module('omnyMainConverters', []);

converters.factory('mainConverter', [function() {
  var manager = {};

  manager.registerConverters=function(converterManager) {
    console.log("registering");
    manager.registerText(converterManager);
  }
  
  manager.registerText = function(converterManager) {
      converterManager.registerConverter('Omny.Text',function(data,type) {
          return data.Text;
      });
  }
  
  return manager;
}]);
