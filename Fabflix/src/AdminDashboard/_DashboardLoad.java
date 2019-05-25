import com.google.gson.JsonArray;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "_DashboardLoad", urlPatterns = "/api/_dashboard_load")
public class _DashboardLoad extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        PrintWriter out = response.getWriter();
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbCon = ds.getConnection();
            String getTablesStr = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.tables WHERE TABLE_SCHEMA=\"moviedb\"";
            PreparedStatement getTablesStmt = dbCon.prepareStatement(getTablesStr);
    		ResultSet tableResults = getTablesStmt.executeQuery(getTablesStr);
    		
            JsonArray jsonArray = new JsonArray();

	        while (tableResults.next()) {
				JsonObject jsonObject = new JsonObject();
	        	String tableName = tableResults.getString("TABLE_NAME");
				jsonObject.addProperty("table_name", tableName);		//Add name of table to jsonObject

	        	String getAttrsStr = "SELECT t.COLUMN_NAME, t.DATA_TYPE FROM (SELECT * FROM INFORMATION_SCHEMA.COLUMNS "+
	        	"WHERE table_name=?) AS t";
	        	PreparedStatement getAttrsStmt = dbCon.prepareStatement(getAttrsStr);
	        	getAttrsStmt.setString(1, tableName);
	    		ResultSet attrsResults = getAttrsStmt.executeQuery();
				
				String attrPair = "";
				int attrNum = 1;
				while(attrsResults.next()) {
					attrPair = attrsResults.getString("COLUMN_NAME") + "," + attrsResults.getString("DATA_TYPE");
					jsonObject.addProperty("attr"+Integer.toString(attrNum++), attrPair);
				}
				jsonArray.add(jsonObject);
	        } 
            response.getWriter().write(jsonArray.toString());
        }catch(Exception e){
    		e.printStackTrace();
    		
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
        }
    }
}
