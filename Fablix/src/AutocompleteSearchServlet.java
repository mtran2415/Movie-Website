import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "AutocompleteSearchServlet", urlPatterns = "/autocomplete-search")

public class AutocompleteSearchServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbCon = ds.getConnection();
            
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String query = request.getParameter("query");
			
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			// TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
			
			ArrayList<String> prefixList = new ArrayList<String>(Arrays.asList(query.split(" ")));
			String resultQuery = "SELECT title, id FROM movies WHERE MATCH(title) AGAINST(\"";
			for(String queryTerm : prefixList) {
				resultQuery += "+"+queryTerm+"* ";
			}
			resultQuery = resultQuery.trim()+"\"IN BOOLEAN MODE)  limit 10;";
			System.out.println(resultQuery);
            PreparedStatement resultStmt = dbCon.prepareStatement(resultQuery);
            ResultSet rs = resultStmt.executeQuery();
            
            while(rs.next()) {
            	JsonObject jsonObj = generateJsonObject(rs.getString("id"), rs.getString("title"), "Movie");
            	jsonArray.add(jsonObj);
            }
			
			response.getWriter().write(jsonArray.toString());
			return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "marvel", "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String movieId, String heroName, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", heroName);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("movieID", movieId);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
}

