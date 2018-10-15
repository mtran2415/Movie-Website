import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json"); 

		String id = request.getParameter("id");

		PrintWriter out = response.getWriter();

		try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();

			
			
			String query = "SELECT m.id, m.title, m.year, m.director, " + 
					"group_concat(DISTINCT g.name SEPARATOR ', ') AS genres, " +
					"group_concat(DISTINCT g.id SEPARATOR ', ') AS genreIds, " + 
					"group_concat(DISTINCT s.name SEPARATOR ', ') AS stars, " +
					"group_concat(DISTINCT s.id ORDER BY s.name SEPARATOR ' ') AS starIds " +
					"FROM movies AS m, stars AS s, genres AS g, stars_in_movies AS sim, genres_in_movies AS gim " + 
					"WHERE m.id = ? AND m.id = sim.movieId AND sim.starId = s.id AND m.id = gim.movieId AND gim.genreId = g.Id " + 
					"GROUP BY m.id, m.title, m.year, m.director;";

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);

			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			while (rs.next()) {
				
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movieStars = rs.getString("stars");
				String movieGenres = rs.getString("genres");
				String genreIds = rs.getString("genreIds");
				String starIds = rs.getString("starIds");


				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_stars", movieStars);
				jsonObject.addProperty("movie_genres", movieGenres);
				jsonObject.addProperty("genre_ids", genreIds);
				jsonObject.addProperty("star_ids", starIds);

				jsonArray.add(jsonObject);
			}
			
            out.write(jsonArray.toString());
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();

	}

}
