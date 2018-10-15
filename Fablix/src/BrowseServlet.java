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

@WebServlet(name = "BrowseServlet", urlPatterns = "/browse")
public class BrowseServlet extends HttpServlet {
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
            
            String genre = request.getParameter("genre");   
            String letter = request.getParameter("letter");  
            String limit = request.getParameter("limit");
            String offset = request.getParameter("offset");
            String header = request.getParameter("header");
            String sort = request.getParameter("sort");
            
            String sortParam = null;
            if(header.equals("title"))
            	sortParam = "sub.title " + sort;
            else
            	sortParam = "r.rating " + sort;
            
            String query = null;          
            String base = "SELECT sub.id, sub.title, sub.year, sub.director, r.rating, group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, group_concat(DISTINCT s.name SEPARATOR ', ') AS stars, group_concat(DISTINCT s.id ORDER BY s.name SEPARATOR ' ') AS starIds " + 
            		"FROM (SELECT m.id, m.title, m.year, m.director " + 
            		"FROM movies as m, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim " + 
            		"WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.Id %s) as sub LEFT JOIN ratings as r ON sub.id = r.movieId, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim " + 
            		"WHERE sub.id = sim.movieId and sim.starId = s.id and sub.id = gim.movieId and gim.genreId = g.Id " + 
            		"GROUP BY sub.id, sub.title, sub.year, sub.director, r.rating " +
            		"ORDER BY " + sortParam +
            		" LIMIT " + limit + " OFFSET " + offset;
            
            if(!genre.trim().isEmpty() && letter.trim().isEmpty())
            	query = String.format(base, "and g.name = '" + genre + "'");
            else if(genre.trim().isEmpty() && !letter.trim().isEmpty())
            	query = String.format(base, "and m.title like '" + letter + "%'");
                                    
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
            	String m_id = rs.getString("id");
            	String m_title = rs.getString("title");
    			String m_year = rs.getString("year");
    			String m_dir = rs.getString("director");
    			String m_rating = rs.getString("rating");
    			String m_genres = rs.getString("genres");
    			String m_stars = rs.getString("stars");
    			String m_starIds = rs.getString("starIds");

    			JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("id", m_id);
				jsonObject.addProperty("title", m_title);
				jsonObject.addProperty("year", m_year);
				jsonObject.addProperty("director", m_dir);
				jsonObject.addProperty("genres", m_genres);
				jsonObject.addProperty("rating", m_rating);
				jsonObject.addProperty("stars", m_stars);
				jsonObject.addProperty("starIds", m_starIds);
				
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