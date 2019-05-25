import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");        
                
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbCon = ds.getConnection();
                        
            //String query = "SELECT * FROM customers AS c WHERE c.email = '"+username+"';";
	        String query = "SELECT * FROM customers AS c WHERE c.email = ?;";
	        PreparedStatement statement = dbCon.prepareStatement(query);
	        statement.setString(1, username);
    		ResultSet resultSet = statement.executeQuery();
    		boolean success = false;
    		
	        if (resultSet.next()) {
	        	// get the encrypted password from the database
				String encryptedPassword = resultSet.getString("password");
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
				System.out.println("success: " + success);
	        }
	        
	        if(success) {
	        	if (request.getHeader("User-Agent").toLowerCase().contains("android"))
	        		RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);
	        	else
	        		RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);

	            request.getSession().setAttribute("user", new User(resultSet.getString("id")));
	
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
	
	            response.getWriter().write(responseJsonObject.toString());
	        } else {
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "incorrect username/password combination");
	            response.getWriter().write(responseJsonObject.toString());
	        }
        }catch(Exception e){
    		e.printStackTrace();
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "incorrect username/password combination");
            response.getWriter().write(responseJsonObject.toString());

        }
    }
}
