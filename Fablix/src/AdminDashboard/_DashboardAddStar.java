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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "_DashboardAddStar", urlPatterns = "/api/_dashboard_add_star")
public class _DashboardAddStar extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    private void sendError(HttpServletResponse response) {
        try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Could not add star!");
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
        String star_name = request.getParameter("name");
        String birth_year = request.getParameter("birth_year");
        if(birth_year == "")
        	birth_year = null;
        System.out.print(star_name);
        System.out.print(birth_year);
        
        Connection dbCon = null;
        PreparedStatement idCountStmt = null;
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedbMaster");

            dbCon = ds.getConnection();

            String idCountStr = "SELECT COUNT(*) FROM stars WHERE id LIKE \"em%\"";
            idCountStmt = dbCon.prepareStatement(idCountStr);
	        ResultSet getIdCount = idCountStmt.executeQuery();
	        int count = 0;
            
            if(getIdCount.next()) {
            	count = Integer.parseInt(getIdCount.getString("count(*)"));
            }
            
            //probably would want to generate a random string prefix, but just hard coding for now
            String newId = "em" + Integer.toString(count);
            String insertIntoStarsStr = "INSERT INTO stars(id, name, birthYear) VALUES(?,?,?);";
	        PreparedStatement insertStarStmt = dbCon.prepareStatement(insertIntoStarsStr);
	        insertStarStmt.setString(1, newId);
	        insertStarStmt.setString(2, star_name);
	        insertStarStmt.setString(3, birth_year);
	        insertStarStmt.executeUpdate();

	        JsonObject responseJsonObject = new JsonObject();
	        responseJsonObject.addProperty("status", "success");
	        responseJsonObject.addProperty("message", "Added star!");
	
	        response.getWriter().write(responseJsonObject.toString());
        }catch(SQLException se){
        	sendError(response);
            se.printStackTrace();            
	    }catch(Exception e){
        	sendError(response);
    		e.printStackTrace();
        }
    }
}
