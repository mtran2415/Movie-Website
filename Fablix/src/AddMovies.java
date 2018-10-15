import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class AddMovies {
	
	public AddMovies() {
		
	}
	
	private Hashtable<String, String> movieHash = new Hashtable<String, String>();	
	private Hashtable<String, String> genreHash = new Hashtable<String, String>();	
	
	String loginUser = "ethanmarc";
    String loginPasswd = "122b42";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	
	public void createMovieHash() {
		System.out.println("creating movie hash");
    	try {
    		//System.out.println(dataSource.toString());
    		
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
    		// movie count
    		String cQuery = "SELECT count(*) FROM movies;";
			PreparedStatement c = dbCon.prepareStatement(cQuery);
			ResultSet r = c.executeQuery();
			r.next();
			System.out.println("count: " + r.getInt(1));
			
    		
			String moviesQuery = "SELECT title,id FROM movies;";
			PreparedStatement movieStatement = dbCon.prepareStatement(moviesQuery);
			
			ResultSet rs = movieStatement.executeQuery();
			int i = 0;
			while(rs.next()) {
				//System.out.println(rs.getString("title"));
				movieHash.put(rs.getString("id"), rs.getString("title"));
				i++;
			}
			System.out.println(i);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
	public Hashtable<String, String> getMovieHash() {
		return movieHash;
	}
	
	public void createGenreHash() {
    	try {
    		//System.out.println(dataSource.toString());
    		
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
    		
			String moviesQuery = "SELECT id, name FROM genres";
			PreparedStatement movieStatement = dbCon.prepareStatement(moviesQuery);
			
			ResultSet rs = movieStatement.executeQuery();
			while(rs.next()) {
				//System.out.println(rs.getString("title"));
				//genreHash.put(rs.getInt("id"), rs.getString("name").trim());
				genreHash.put(rs.getString("name").trim(), "0");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
	public void batchAdd(List<Movie> movies) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		System.out.println("hash size: " + movieHash.size());
		
		Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        try {
        	conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement addStatement=null;
        String addQuery=null;

        int[] iNoRows=null;

        String idQuery = "SELECT nextId FROM movie_helper";
        String nextQuery = "UPDATE movie_helper SET nextId = nextId + 1;";
        String addGenre = "INSERT INTO (name) genres VALUES(?)";
        addQuery = "INSERT INTO movies VALUES(?,?,?,?);";
        try {
			conn.setAutoCommit(false);

			addStatement=conn.prepareStatement(addQuery);


			Iterator<Movie> it = movies.iterator();
            while (it.hasNext()) {
            	Movie mov = it.next();
            	if(!movieHash.containsValue(mov.getTitle()))
            	{
	            	PreparedStatement idStatement = conn.prepareStatement(idQuery);
	            	ResultSet rs = idStatement.executeQuery();
	            	rs.next();
	            	String id = "xm" + rs.getString("nextId");
	            	
	            	
	            	
	            	addStatement.setString(1, id);
	            	addStatement.setString(2, mov.getTitle());
	            	addStatement.setInt(3, mov.getYear());
	            	addStatement.setString(4, mov.getDirector());
	        		addStatement.addBatch();
	        		
	        		//genres
	        		List<String> genres = mov.getGenres();
	        		for(int i = 0; i < genres.size(); i++) {
	        			if(!genreHash.containsValue(genres.get(i))) {
		        			PreparedStatement genreStatement = conn.prepareStatement(addGenre);
		        			genreStatement.setString(1, genres.get(i));
		        			genreStatement.executeUpdate();
	        			}
	        		}
	        		
	        		PreparedStatement nextStatement = conn.prepareStatement(nextQuery);
	        		nextStatement.executeUpdate();
            	}
            }

			iNoRows=addStatement.executeBatch();
			conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(addStatement!=null) addStatement.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
		
	}
	
	public void add(List<Movie> movies) {
		try {
			System.out.println("adding movies");
			PrintWriter writer = new PrintWriter("movies.txt", "UTF-8");
			PrintWriter genreWriter = new PrintWriter("genres.txt", "UTF-8");
			PrintWriter gimWriter = new PrintWriter("gim.txt", "UTF-8");
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			String idQuery = "SELECT nextId FROM movie_helper";
			String nextQuery = "UPDATE movie_helper SET nextId = nextId + 1;";
			
			PreparedStatement maxStatement = dbCon.prepareStatement("SELECT MAX(id) as max from genres;");
			ResultSet max = maxStatement.executeQuery();
			max.next();
			int maxId = max.getInt("max") + 1;
			
			Iterator<Movie> it = movies.iterator();
            while (it.hasNext()) {
            	Movie mov = it.next();
            	
            	PreparedStatement idStatement = dbCon.prepareStatement(idQuery);
            	ResultSet rs = idStatement.executeQuery();
            	rs.next();
            	String id = "xm" + rs.getString("nextId");
            	String actualId;
            	
            	if(!movieHash.containsValue(mov.getTitle()))
            	{
            		// movies
            		
            		if(mov.getId() == null)
            		{
            			movieHash.put(id, mov.getTitle());
            			actualId = id;
            		}
            		else
            		{
            			movieHash.put(mov.getId(), mov.getTitle());
            			actualId = mov.getId();
            		}
            		
            	
            		
            		String base = "%s^%s^%s^%s";
            		String query = String.format(base, actualId.trim(), mov.getTitle().trim(), String.valueOf(mov.getYear()), mov.getDirector().trim());
            		
            		
            		writer.println(query);
            		
            		PreparedStatement nextStatement = dbCon.prepareStatement(nextQuery);
            		nextStatement.executeUpdate();
            		
            		System.out.println("adding genres for: " + mov.getTitle());
            		
            		// genres
            		
	        		List<String> genres = mov.getGenres();
	        		String genreBase = "NULL,%s";
	        		String gimBase = "%s,%s";
	        		for(int i = 0; i < genres.size(); i++) {
	        			//System.out.println("GENRES: " + genres.get(i));
	        			if(!genreHash.containsKey(genres.get(i))) {
	                		//String genreQuery = String.format(genreBase, genres.get(i));
	                		//genreWriter.println(genreQuery);
	                		
	                		PreparedStatement genState = dbCon.prepareStatement("INSERT INTO genres VALUES (null,?);");
	                		
	                		genState.setString(1, genres.get(i));
		        			genState.executeUpdate();
	                		   		
		        			PreparedStatement idState = dbCon.prepareStatement("SELECT id FROM genres WHERE name=?");
		        			idState.setString(1, genres.get(i));
		        			ResultSet is = idState.executeQuery();
		        			is.next();
		        			
		        			//System.out.println(genres.get(i));
		        			//genreHash.put(Integer.parseInt(is.getString("id")), genres.get(i));
		        			genreHash.put(genres.get(i), "0");
	                		//maxId++;
		        			//System.out.println(genreHash.get(i));
	        			}
	        			
	        			PreparedStatement gimState = dbCon.prepareStatement("SELECT id FROM genres WHERE name=?");
	        			gimState.setString(1, genres.get(i));
	        			ResultSet gs = gimState.executeQuery();
	        			gs.next();
	        			
	        			String gimQuery = String.format(gimBase, Integer.parseInt(gs.getString("id")), actualId);
                		gimWriter.println(gimQuery);
	        		}
	        		
	        		//System.out.println(genreHash);
	        		
            	}
            }
            writer.close();
            genreWriter.close();
            gimWriter.close();
			
			String addQuery = "LOAD DATA LOCAL INFILE 'movies.txt' INTO TABLE moviedb.movies FIELDS TERMINATED BY '^'  LINES STARTING BY '';";
			PreparedStatement addStatement = dbCon.prepareStatement(addQuery);
			addStatement.executeUpdate();
			
//			String genreQuery = "LOAD DATA LOCAL INFILE 'genres.txt' INTO TABLE moviedb.genres FIELDS TERMINATED BY ','  LINES STARTING BY '';";
//			PreparedStatement genreStatement = dbCon.prepareStatement(genreQuery);
//			genreStatement.executeUpdate();
			
			String gimQuery = "LOAD DATA LOCAL INFILE 'gim.txt' INTO TABLE moviedb.genres_in_movies FIELDS TERMINATED BY ','  LINES STARTING BY '';";
			PreparedStatement gimStatement = dbCon.prepareStatement(gimQuery);
			gimStatement.executeUpdate();
			
//			String gimQuery = "INSERT INTO genres_in_movies VALUES (?,?);";
//			Iterator<Movie> it2 = movies.iterator();
//            while (it2.hasNext()) {
//            	Movie m = it2.next();
//            	List<String> genres = m.getGenres();
//            	for(int i = 0; i < genres.size(); i++) {
//					PreparedStatement gimStatement = dbCon.prepareStatement(gimQuery);
//					
//					System.out.println("gim: " + start + " " + m.getId());
//					
//		    		gimStatement.setInt(1, start);
//		    		gimStatement.setString(2, m.getId());
//		    		gimStatement.executeUpdate();
//		    		start++;
//            	}
//            }
			
			
			
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException | FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
