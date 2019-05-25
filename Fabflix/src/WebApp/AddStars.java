import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class AddStars {
	
	//private Hashtable<String, Star> starHash = new Hashtable<String, Star>();	
	
	String loginUser = "ethanmarc";
    String loginPasswd = "122b42";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	
	public void add(Hashtable<String, Star> starHash, Hashtable<String, String> movieHash) {
		try {

			PrintWriter starsWriter = new PrintWriter("stars.txt", "UTF-8");
			PrintWriter stars_in_moviesWriter = new PrintWriter("stars_in_movies.txt", "UTF-8");
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
    		for(String key : starHash.keySet()) {
    			
    		
			
            	Star star = starHash.get(key);
            	

            	
            	//Add the star to stars table 
        		String base = "%s,%s,%s";
        		String query = String.format(base, star.getId().trim(), star.getName().trim(), String.valueOf(star.getBirthYear()));
        		starsWriter.println(query);


        		
        		Iterator<String> movieIt = star.getMovies().iterator();
            	while(movieIt.hasNext())
            	{
            		String movieId = movieIt.next();
            		
            		if(movieHash.containsKey(movieId)) {
            			base = "%s,%s";
                		query = String.format(base, star.getId().trim(), movieId.trim());
                		stars_in_moviesWriter.println(query);
            		}
            		else {
            			System.out.println("Error: fid not in mains.xml: " + movieId);
            		}
	        	}
        	}
            
            starsWriter.close();
			stars_in_moviesWriter.close();

			String addStarsQuery = "LOAD DATA LOCAL INFILE 'stars.txt' INTO TABLE moviedb.stars FIELDS TERMINATED BY ','  LINES STARTING BY '';";
			PreparedStatement addStarsStatement = dbCon.prepareStatement(addStarsQuery);
			addStarsStatement.executeUpdate();
			
			String addStarsInMoviesQuery = "LOAD DATA LOCAL INFILE 'stars_in_movies.txt' INTO TABLE moviedb.stars_in_movies FIELDS TERMINATED BY ','  LINES STARTING BY '';";
			PreparedStatement addStarsInMoviesStatement = dbCon.prepareStatement(addStarsInMoviesQuery);
			addStarsInMoviesStatement.executeUpdate();

						
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException | FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
