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
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "_DashboardLogin", urlPatterns = "/api/_dashboard_login")
public class _DashboardLogin extends HttpServlet {
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
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbCon = ds.getConnection();
            Statement statement = dbCon.createStatement();
            
            String query = "SELECT * FROM employees AS c WHERE c.email = '"+username+"';";
    		ResultSet resultSet = statement.executeQuery(query);
    		boolean success = false;
    		
	        if (resultSet.next()) {
	        	// get the encrypted password from the database
				String encryptedPassword = resultSet.getString("password");
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	        }
	        
	        if(success) {
	            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);

	            request.getSession().setAttribute("employee", new Employee(resultSet.getString("fullname")));
	
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
