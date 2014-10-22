define([],
    function() {
        function QueryStringReader() {
            var parameters = {};
            
            this.parseParameters = function(queryString) {
                var params = queryString.split('&');
                var results = {};
                for (var i = 0; i < params.length; i++)
                {
                    var param = params[i].split('=');
                    results[param[0]] =  decodeURIComponent(param[1]);
                }
                return results;
            };
            
            this.readQueryString = function() {
                var queryString = window.location.search.substring(1);
                this.parameters = this.parseParameters(queryString);
            };
            
            this.getParameter = function(name) {
                this.readQueryString();
                return this.parameters[name];
            }
            
            this.getParameterFromUrl = function(url,name) {
                var queryString = url.substring(url.indexOf("?")+1);
                var parameters = this.parseParameters(queryString);
                return parameters[name];
            }
            
            //this.readQueryString();
        }
        
        //page scoped return constructor
        return new QueryStringReader();
    }
);


