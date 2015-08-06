package ca.omny.auth.apis;

import ca.omny.request.api.OmnyApiRegistry;

public class RegisterApis {
   static {
       OmnyApiRegistry.registerApi(new GoogleLogin());
       OmnyApiRegistry.registerApi(new Health());
       OmnyApiRegistry.registerApi(new Permissions());
       OmnyApiRegistry.registerApi(new Sessions());
       OmnyApiRegistry.registerApi(new Users());
   } 
}
