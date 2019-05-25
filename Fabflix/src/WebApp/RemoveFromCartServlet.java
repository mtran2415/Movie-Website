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

//
@WebServlet(name = "RemoveFromCartServlet", urlPatterns = "/api/remove-cart")
public class RemoveFromCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    private void sendError(HttpServletResponse response) {
        try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Could not remove from shopping cart.");
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
            
            String query = "DELETE FROM shopping_cart " +
            		"WHERE customerId='"+user.getId()+"' AND movieId='"+movie_id+"';";
    		statement.executeUpdate(query);
    		
		
	        JsonObject responseJsonObject = new JsonObject();
	        responseJsonObject.addProperty("status", "success");
	        responseJsonObject.addProperty("message", "Removed movie from shopping cart.");
	
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
