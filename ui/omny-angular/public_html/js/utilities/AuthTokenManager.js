define(['utilities/QueryStringReader'],
    function(queryStringReader) {
        function AuthTokenManager() {
            this.getToken = function() {
                return this.getCookie("access_token");
            };
            
            this.getCookie = function(cname)
            {
                var name = cname + "=";
                var ca = document.cookie.split(';');
                for (var i = 0; i < ca.length; i++)
                {
                    var c = ca[i].trim();
                    if (c.indexOf(name) == 0)
                        return c.substring(name.length, c.length);
                }
                return "";
            }
            
            this.setCookie =function(cname, cvalue, exdays)
            {
                var d = new Date();
                d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
                var expires = "expires=" + d.toGMTString();
                document.cookie = cname + "=" + cvalue + "; " + expires;
            }
            
            this.saveToken = function() {
                var token = queryStringReader.getParameter("access_token")
                if (typeof token!="undefined") {
                    setCookie("access_token", token, 365);
                }
            }
        }
        
        //session scoped return object
        return new AuthTokenManager();
    }
);

