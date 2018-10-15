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

@WebServlet(name = "_DashboardAddMovie", urlPatterns = "/api/_dashboard_add_movie")
public class _DashboardAddMovie extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    private void sendError(HttpServletResponse response) {
        try {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "Could not add movie");
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
        String movie_title = request.getParameter("movie_title");
        String release_year = request.getParameter("release_year");
        String director = request.getParameter("director");
        String star_name = request.getParameter("star_name");
        String genre = request.getParameter("genre");
        
        /*System.out.println(movie_title);
        System.out.println(release_year);
        System.out.println(director);
        System.out.println(star_name);
        System.out.println(genre);*/

        Connection dbCon = null;
        PreparedStatement checkIfMovieExistsStmt = null;
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedbMaster");

            dbCon = ds.getConnection();

            String checkIfExistsStr = "SELECT * FROM movies WHERE title=? AND year=? AND director=?;";
            checkIfMovieExistsStmt = dbCon.prepareStatement(checkIfExistsStr);
            checkIfMovieExistsStmt.setString(1, movie_title);
            checkIfMovieExistsStmt.setString(2, release_year);
            checkIfMovieExistsStmt.setString(3, director);

	        ResultSet checkIfExists = checkIfMovieExistsStmt.executeQuery();
            
            if(checkIfExists.next()) {
    	        JsonObject responseJsonObject = new JsonObject();
    	        responseJsonObject.addProperty("status", "fail");
    	        responseJsonObject.addProperty("message", "Movie already exists!");
    	
    	        response.getWriter().write(responseJsonObject.toString());
    	        return;
            }
                        
            /*Generating new movie id*/
            String idMovieCountStr = "SELECT COUNT(*) FROM movies WHERE id LIKE \"em%\";";
            PreparedStatement movieIdCountStmt = dbCon.prepareStatement(idMovieCountStr);
	        ResultSet getMovieIdCount = movieIdCountStmt.executeQuery();
	        int count = 0;
            
            if(getMovieIdCount.next()) {
            	count = Integer.parseInt(getMovieIdCount.getString("count(*)"));
            }

            //probably would want to generate a random string prefix, but just hard coding for now
            String newMovieId = "em" + Integer.toString(count);
            
            /*Creating new movie*/
            /*String insertMovieStr = "INSERT INTO movies(id, title, year, director) VALUES(?,?,?,?);";
            PreparedStatement insertMovieStmt = dbCon.prepareStatement(insertMovieStr);
            insertMovieStmt.setString(1, newMovieId);
            insertMovieStmt.setString(2, movie_title);
            insertMovieStmt.setString(3, release_year);
            insertMovieStmt.setString(4, director);*/
            
            /*Creating new star (if necessary) and updating stars_in_movies*/
            String checkIfStarExistsStr = "SELECT * FROM stars WHERE name=?;";
            PreparedStatement checkIfStarExistsStmt = dbCon.prepareStatement(checkIfStarExistsStr);
            checkIfStarExistsStmt.setString(1, star_name);
            ResultSet checkIfStarExists = checkIfStarExistsStmt.executeQuery();
            
            String newStarId = "";
            int isNewStar = 0;
            if(!checkIfStarExists.next()) { //Create new star first
            	isNewStar = 1;
                String idStarCountStr = "SELECT COUNT(*) FROM stars WHERE id LIKE \"em%\"";
                PreparedStatement starIdCountStmt = dbCon.prepareStatement(idStarCountStr);
    	        ResultSet getStarIdCount = starIdCountStmt.executeQuery();
    	        count = 0;
                
                if(getStarIdCount.next()) {
                	count = Integer.parseInt(getStarIdCount.getString("count(*)"));
                }
                
                //probably would want to generate a random string prefix, but just hard coding for now
                newStarId = "em" + Integer.toString(count);
                /*String insertIntoStarsStr = "INSERT INTO stars(id, name, birthYear) VALUES(?,?,NULL);";
    	        PreparedStatement insertStarStmt = dbCon.prepareStatement(insertIntoStarsStr);
    	        insertStarStmt.setString(1, newStarId);
    	        insertStarStmt.setString(2, star_name);
    	        insertStarStmt.executeUpdate();*/       
            }
            else {
    	        newStarId = checkIfStarExists.getString("id"); //Just get the id of the first star with the provided name
            	System.out.println(newStarId);
            }
	        /*String insertIntoStarsInMoviesStr = "INSERT INTO stars_in_movies(starId, movieId) VALUES(?,?);";
	        PreparedStatement insertIntoStarsInMoviesStmt = dbCon.prepareStatement(insertIntoStarsInMoviesStr);
	        insertIntoStarsInMoviesStmt.setString(1, newStarId);
	        insertIntoStarsInMoviesStmt.setString(2, newMovieId);*/

            /*Creating new genre (if necessary) and updating stars_in_movies*/
            String checkIfGenreExistsStr = "SELECT * FROM genres WHERE name=?;";
            PreparedStatement checkIfGenreExistsStmt = dbCon.prepareStatement(checkIfGenreExistsStr);
            checkIfGenreExistsStmt.setString(1, genre);
            ResultSet checkIfGenreExists = checkIfGenreExistsStmt.executeQuery();
            
            String gId = "";
            int isNewGenre = 0;
            if(!checkIfGenreExists.next()) { //Create new genre first
            	isNewGenre = 1;
                String idGenreCountStr = "SELECT MAX(id) as max FROM genres;";
                PreparedStatement genreIdCountStmt = dbCon.prepareStatement(idGenreCountStr);
    	        ResultSet getGenreIdCount = genreIdCountStmt.executeQuery();
    	        count = 0;
                
                if(getGenreIdCount.next()) {
                	count = Integer.parseInt(getGenreIdCount.getString("max")) + 1;
                	System.out.println("count: " + count);
                }
                
                //probably would want to generate a random string prefix, but just hard coding for now
                gId = Integer.toString(count);
                /*String insertIntoGenresStr = "INSERT INTO genres(id, name) VALUES(?,?);";
    	        PreparedStatement insertGenreStmt = dbCon.prepareStatement(insertIntoGenresStr);
    	        insertGenreStmt.setString(1, newGenreId);
    	        insertGenreStmt.setString(2, genre);
    	        insertGenreStmt.executeUpdate();*/    	        
            }
            else {
            	gId = checkIfGenreExists.getString("id");
            }
	        /*String insertIntoGenresInMoviesStr = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?,?);";
	        PreparedStatement insertIntoGenresInMoviesStmt = dbCon.prepareStatement(insertIntoGenresInMoviesStr);
	        insertIntoGenresInMoviesStmt.setString(1, newGenreId);
	        insertIntoGenresInMoviesStmt.setString(2, newMovieId);*/
	        
            /*insertMovieStmt.executeUpdate();
	        insertIntoStarsInMoviesStmt.executeUpdate();
	        insertIntoGenresInMoviesStmt.executeUpdate();*/
        	System.out.println(isNewGenre);
        	System.out.println(isNewStar);

            
            String callAddMovieStr = "CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement callAddMovieStmt = dbCon.prepareStatement(callAddMovieStr);
            callAddMovieStmt.setString(1, Integer.toString(isNewStar));
            callAddMovieStmt.setString(2, Integer.toString(isNewGenre));
            callAddMovieStmt.setString(3, gId);
            callAddMovieStmt.setString(4, newStarId);
            callAddMovieStmt.setString(5, newMovieId);
            callAddMovieStmt.setString(6, movie_title);
            callAddMovieStmt.setString(7, release_year);
            callAddMovieStmt.setString(8, director);
            callAddMovieStmt.setString(9, star_name);
            callAddMovieStmt.setString(10, genre);
            callAddMovieStmt.executeQuery();

	        JsonObject responseJsonObject = new JsonObject();
	        responseJsonObject.addProperty("status", "success");
	        responseJsonObject.addProperty("message", "Added movie!");
	
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
