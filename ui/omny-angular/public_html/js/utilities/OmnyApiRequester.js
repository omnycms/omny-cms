define(['jquery','utilities/AuthTokenManager','utilities/QueryStringReader'],
        function($, authTokenManager, queryStringReader) {
            var ApiRequester = {};
            ApiRequester.ajaxRequest = function(request) {
                var requestInfo = {
                    url: request.url,
                    type: "GET",
                    success: function(data) {
                        if (typeof data == "string") {
                            try {
                                data = JSON.parse(data);
                            } catch (error) {

                            }
                        }
                       
                        request.success(data);
                        
                    }, error: function(data, t, e) {
                        if (typeof request.error == "function") {
                            request.error(data);
                        } else {
                            //alert("An error has occurred");
                        }
                    }
                };

                if (typeof request.data != "undefined") {
                    requestInfo.data = request.data;
                }
                if (typeof request.type != "undefined") {
                    requestInfo.type = request.type;
                }
                if (typeof request.processData != "undefined") {
                    requestInfo.processData = request.processData;
                }
                if (typeof request.contentType != "undefined") {
                    requestInfo.contentType = request.contentType;
                }            

                if (typeof request.site != "undefined") {
                    requestInfo.headers = {"X-Origin": request.site};
                } else {
                    requestInfo.headers = {"X-Origin": ApiRequester.getHostname()};
                }
                $.ajax(requestInfo);
            }

            ApiRequester.getHostname = function(ignoreSiteParameter) {
                if (!ignoreSiteParameter&&typeof queryStringReader.getParameter("site") != "undefined") {
                    return queryStringReader.getParameter("site");
                } else if (window.location.hostname.indexOf(".omny.me") > 0) {
                    return window.location.hostname.substring(0, window.location.hostname.indexOf(".omny.me"));
                } else if (window.location.hostname.indexOf("local")==0) { 
                    return "www";
                } else if(window.location.hostname =="www.omny.ca") {
                    return "www";
                }else {
                    return window.location.hostname;
                }
            }
            
            ApiRequester.getFullHostname = function(ignoreSiteParameter) {
                var port = (window.location.port!=80&&window.location.port!=443)?":"+window.location.port:"";
                if (!ignoreSiteParameter&&typeof queryStringReader.getParameter("site") != "undefined") {
                    var site = queryStringReader.getParameter("site");
                    if(site.indexOf(".")<0) {
                        return site+".omny.me"+port;
                    }
                } else if (window.location.hostname.indexOf("local")==0) { 
                    return "www.omny.me"+port;
                } else if(window.location.hostname =="www.omny.ca") {
                    return "www.omny.me"+port;
                }else {
                    return window.location.hostname+port;
                }
            }

            ApiRequester.apiRequest = function(module, path, request, version) {
                var url = window.location.origin+"/api/v1.0/" + module;
                if(path!="") {
                    url +="/"+path;
                }
                if (url.indexOf("?") == -1) {
                    url += "?access_token=" + authTokenManager.getToken();
                } else {
                    url += "&access_token=" + authTokenManager.getToken();
                }
                request.url = url;
                ApiRequester.ajaxRequest(request);
            }

            return ApiRequester;
        }
);

