import com.google.gson.JsonObject;

import javax.annotation.Resource;
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

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/loginAnd")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");        
                
        try {
	        Connection dbCon = dataSource.getConnection();
            //Statement statement = dbCon.createStatement();
            
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
	            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);

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
