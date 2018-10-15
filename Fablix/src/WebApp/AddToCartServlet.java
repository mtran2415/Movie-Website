import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;


@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/add-cart")
public class AddToCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    private void sendError(HttpServletResponse response) {
        try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Could not add to shopping cart.");
			response.getWriter().write(responseJsonObject.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movie_id = request.getParameter("movie_id");
        PrintWriter out = response.getWriter();
        
        Connection dbCon = null;
        Statement statement = null;
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedbMaster");

            dbCon = ds.getConnection();
            statement = dbCon.createStatement();
            User user = (User)request.getSession().getAttribute("user");

            
            String query = "INSERT INTO shopping_cart(customerId, movieId, quantity) "+
            		"VALUES('"+user.getId()+"','"+movie_id+"','1')" +
            		"ON DUPLICATE KEY UPDATE quantity = quantity + 1;";
    		statement.executeUpdate(query);
    		
		
	        JsonObject responseJsonObject = new JsonObject();
	        responseJsonObject.addProperty("status", "success");
	        responseJsonObject.addProperty("message", "Added movie to shopping cart!");
	
	        response.getWriter().write(responseJsonObject.toString());
        }catch(SQLException se){
        	sendError(response);
            se.printStackTrace();
        }catch(Exception e){
        	sendError(response);
    		e.printStackTrace();
    		
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
        }finally {
            try{
            	if(statement!=null)
            		dbCon.close();
            }catch(SQLException se){
            }
            
            try{
               if(dbCon!=null)
                  dbCon.close();
            }catch(SQLException se){
               se.printStackTrace();
            }
        }
    }
}
