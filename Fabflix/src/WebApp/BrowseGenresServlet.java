import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "BrowseGenresServlet", urlPatterns = "/browse-genres")
public class BrowseGenresServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbCon = ds.getConnection();
            Statement statement = dbCon.createStatement();
            
            String query = "SELECT name from genres;";
            
                                    
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
            	String gname = rs.getString("name");
            	

    			JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("gname", gname);
				jsonArray.add(jsonObject);
            }
            
            out.write(jsonArray.toString());
            response.setStatus(200);
                        
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
        	JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
        }
        out.close();
    }
}