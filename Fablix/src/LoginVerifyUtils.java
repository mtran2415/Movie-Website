import com.google.gson.JsonObject;

public class LoginVerifyUtils {
    
    public static JsonObject verifyUsernamePassword(String username, String password) {
        // after recatpcha verfication, then verify username and password
        if (username.equals("anteater") && password.equals("123456")) {
            // login success:

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            
            return responseJsonObject;

        } else {
            // login fail
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            if (!username.equals("anteater")) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else if (!password.equals("123456")) {
                responseJsonObject.addProperty("message", "incorrect password");
            }

            return responseJsonObject;
        }
    }

}