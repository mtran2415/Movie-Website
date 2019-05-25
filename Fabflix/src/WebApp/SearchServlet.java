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
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

@WebServlet(name = "SearchServlet", urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public String fullText(ArrayList<String> prefixList) {
		String resultQuery = "";
		for(String queryTerm : prefixList) {
			resultQuery += "+"+queryTerm+"* ";
		}
		return resultQuery.trim();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

    	//String contextPath = getServletContext().getRealPath("/");
    	//String xmlFilePath = contextPath+"/Fablix-log";
    	//System.out.println(xmlFilePath);
    	File log = new File("/home/ubuntu/git-Projects/cs122b-spring18-team-42/Fablix-log.txt");

    	log.createNewFile();
    	
    	FileWriter fileWriter = new FileWriter(log.getAbsolutePath(), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
    	
    	
    	long tsStart = System.nanoTime();
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        long tjElapsed = 0;
        String titleSearch = null;
        try {
        	
        	long tjStart = System.nanoTime();
        	
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
            Connection dbCon = ds.getConnection();
            
    		//Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "ethanmarc", "122b42");


            
            
            
            //Statement statement = dbCon.createStatement();
            PreparedStatement searchStatement = null;
            
            
            String title = request.getParameter("title");
            titleSearch = title;
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
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
            		"FROM movies as m, stars as s, stars_in_movies as sim " + 
            		"WHERE m.id = sim.movieId and sim.starId = s.id %s) as sub LEFT JOIN ratings as r ON sub.id = r.movieId, stars as s, stars_in_movies as sim, genres as g, genres_in_movies as gim " + 
            		"WHERE sub.id = sim.movieId and sim.starId = s.id and sub.id = gim.movieId and gim.genreId = g.Id " + 
            		"GROUP BY sub.id, sub.title, sub.year, sub.director, r.rating " +
            		"ORDER BY " + sortParam +
            		" LIMIT " + limit + " OFFSET " + offset;
            
           
            
            
            /**
             * 
             * title
				year
				director
				star
				
				title, year
				title, director
				title, star
				year, director
				year, star
				star, director
				
				title, director, star
				title, year, director
				title, year, star
				year, director, star
				
				title, year, director, star
             */
            
                        
//            if(title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
//            	query = ""; // none
//            else if(!title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'"); // title
//            else if(!title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.director like '%" + director + "%'"); // title,dir
//            else if(title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.year = '" + year + "'"); // year
//            else if(title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.director like '%" + director + "%'"); // dir
//            else if(title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty())
//            	query = String.format(base, "and s.name like '%" + star + "%'"); // star
//            else if(title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
//            	query = String.format(base, "and m.director like '%" + director + "%'" + " and s.name like '%" + star + "%'"); // dir,star
//            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.year = '" + year + "'"); // title, year
//            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.year = '" + year + "'" + " and m.director like '%" + director + "%'"); // title,year,dir
//            else if(title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
//            	query = String.format(base, "and m.year = '" + year + "'" + " and m.director like '%" + director + "%'"); // year,dir
//            else if(title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
//            	query = String.format(base, "and m.year = '" + year + "'" + " and m.director like '%" + director + "%'" + " and s.name like '%" + star + "%'"); // year, dir, star
//            else if(!title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty()) 
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and s.name like '%" + star + "%'"); // title, star
//            else if(title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty()) 
//            	query = String.format(base, "and m.year = '" + year + "'" + " and s.name like '%" + star + "%'");    // year,star
//            else if(!title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty()) 
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.director like '%" + director + "%'" + " and s.name like '%" + star + "%'"); // title, dir, star
//            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.year = '" + year + "'" + " and s.name like '%" + star + "%'"); // title, year,star
//            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
//            	query = String.format(base, "and m.title like '%" + title + "%'" + " and m.director like '%" + director + "%'" + " and m.year = '" + year + "'" + " and s.name like '%" + star + "%'");
//                        //all
            
            
            //System.out.println(director);
            if(title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
            	query = ""; // none
            else if(!title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode)"); // title
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            }
            else if(!title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode) and m.director like ?"); // title,dir
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, "%" + director + "%");
            }
            else if(title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty())
            {
            	query = String.format(base, "and m.year = ?"); // year
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, year);
            }
            else if(title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
            {
            	//System.out.println(director);
            	query = String.format(base, "and m.director like ?"); // dir
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, "%" + director + "%");
            }
            else if(title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty())
            {
            	query = String.format(base, "and s.name like ?"); // star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, "%" + star + "%");
            }
            else if(title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
            {
            	query = String.format(base, "and m.director like ? and s.name like ?"); // dir,star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, "%" + director + "%");
            	searchStatement.setString(2, "%" + star + "%");
            }
            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && star.trim().isEmpty()) 
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode) and m.year = ?"); // title, year
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, year);
            }
            else if(!title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode) and m.year = ? and m.director like ?"); // title,year,dir
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, year);
            	searchStatement.setString(3, "%" + director + "%");
            }
            else if(title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && star.trim().isEmpty())
            {
            	query = String.format(base, "and m.year = ? and m.director like ?"); // year,dir
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, year);
            	searchStatement.setString(2, "%" + director + "%");
            }
            else if(title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
            {	
            	query = String.format(base, "and m.year = ? and m.director like ? and s.name like ?"); // year, dir, star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, year);
            	searchStatement.setString(2, "%" + director + "%");
            	searchStatement.setString(3, "%" + star + "%");
            }
            else if(!title.trim().isEmpty() && year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty()) 
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode) and s.name like ?"); // title, star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, "%" + star + "%");
            }
            else if(title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty()) 
            {	
            	query = String.format(base, "and m.year = ? and s.name like ?");    // year,star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, year);
            	searchStatement.setString(2, "%" + star + "%");
            }
            else if(!title.trim().isEmpty() && year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty()) 
            {
            	query = String.format(base, "and match(m.title) against(? in boolean mode) and m.director like ? and s.name like ?"); // title, dir, star
            	searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, "%" + director + "%");
            	searchStatement.setString(3, "%" + star + "%");
            }
           	else if(!title.trim().isEmpty() && !year.trim().isEmpty() && director.trim().isEmpty() && !star.trim().isEmpty())
           	{  
           		query = String.format(base, "and match(m.title) against(? in boolean mode) and m.year = ? and s.name like ?"); // title, year,star
           		searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, year);
            	searchStatement.setString(3, "%" + star + "%");
           	}
           	else if(!title.trim().isEmpty() && !year.trim().isEmpty() && !director.trim().isEmpty() && !star.trim().isEmpty())
           	{	
           		query = String.format(base, "and match(m.title) against(? in boolean mode) and m.director like ? and m.year = ? and s.name like ?");
                        //all
           		searchStatement = dbCon.prepareStatement(query);
            	searchStatement.setString(1, fullText(new ArrayList<String>(Arrays.asList(title.split(" ")))));
            	searchStatement.setString(2, "%" + director + "%");
            	searchStatement.setString(3, year);
            	searchStatement.setString(4, "%" + star + "%");
           	}
            
            //System.out.println(query);
            
            if(!query.isEmpty()) {
                

	            ResultSet rs = searchStatement.executeQuery();//statement.executeQuery(query);
	            long tjEnd = System.nanoTime();
	            tjElapsed = tjEnd - tjStart;
	            
	            JsonArray jsonArray = new JsonArray();
	
	            while (rs.next()) {
	            	//System.out.println("in while " + rs.getString("director"));
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
            }
            else {
            	JsonArray jsonArray = new JsonArray();
            	out.write(jsonArray.toString());
            }
            
            //statement.close();
            searchStatement.close();
            
            dbCon.close();
            

        } catch (Exception ex) {
        	JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
        }
        out.close();
        
        long tsEnd = System.nanoTime();
        long tsElapsed = tsEnd - tsStart;
        printWriter.print(titleSearch + ";");
        printWriter.print(tjElapsed + ";");
        printWriter.print(tsElapsed);
        printWriter.println();
        printWriter.close();
    }
}