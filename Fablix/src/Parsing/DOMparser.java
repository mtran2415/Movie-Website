import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOMparser {

    static List<Movie> movies;
    static List<Star> stars;
    static Hashtable<String,Star> starsHash;
    Document dom, castDom, actorDom;

   
    
    public DOMparser() {
        movies = new ArrayList<>();
        stars = new ArrayList<>();
        starsHash = new Hashtable<String, Star>();
    }

    public void runExample() {

        parseXmlFile();

        parseDocument();
        parseActors();
        parseCasts();

        printData();

    }

    private void parseXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse("mains243.xml");
            castDom = db.parse("casts124.xml");
            actorDom = db.parse("actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        Element docEle = dom.getDocumentElement();
        
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);

                Iterator<Movie> it = getMovies(el).iterator();
                while (it.hasNext()) {
                    movies.add(it.next());
                } 
            }
        }
      
    }
    
    private void fillStarsHash() {
    	try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "ethanmarc", "122b42");
			
			PreparedStatement fill = dbCon.prepareStatement("SELECT * FROM stars;");
			ResultSet rs = fill.executeQuery();
			while(rs.next()) {
				Star s = new Star(rs.getString("id"), rs.getString("name"), rs.getInt("birthYear"));
				starsHash.put(rs.getString("name"), s);
			}
			
			dbCon.close();
			
    	}catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void parseActors() {
    	fillStarsHash();
    	try {
    	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?&useSSL=false", "ethanmarc", "122b42");
    		
	    	Element docEle = actorDom.getDocumentElement();
	    	
	    	NodeList nl = docEle.getElementsByTagName("actor");
	    	if (nl != null && nl.getLength() > 0) {
	    		for (int i = 0; i < nl.getLength(); i++) {
	    			Element el = (Element) nl.item(i);
	            	String name = getTextValue(el, "stagename");
	            	String birth = getTextValue(el, "dob");
	            	
	            	if(birth == null || !birth.matches("[0-9]+")) {
	            		System.out.println("(Actors.xml) Stagename: " + name + " Tag dob is: " + birth);
	            		birth = "-1";
	            		
	            	}
	            	
	            	
	            	PreparedStatement idStatement = dbCon.prepareStatement("SELECT nextId FROM star_helper;");
					ResultSet rs = idStatement.executeQuery();
     		    	rs.next();
     		    	String sid = "xs" + rs.getString("nextId");
	            	
     		    	if(!starsHash.containsKey(name)){
	     		    	Star s = new Star(sid, name, Integer.parseInt(birth));
	     		    	starsHash.put(name, s);
     		    	}
     		    	else if(starsHash.containsKey(name) && starsHash.get(name).getBirthYear() != Integer.parseInt(birth)) {
     		    		Star s = new Star(sid, name, Integer.parseInt(birth));
	     		    	starsHash.put(name, s);
     		    	}
     		    	else
     		    		System.out.println("(Actors.xml) Star already added: " + name + " " + birth);
	            	
	            	
	            	PreparedStatement nextStatement = dbCon.prepareStatement("UPDATE star_helper SET nextId = nextId + 1;");
            		nextStatement.executeUpdate();

            		
	            }
	    		dbCon.close();
	    	}
    	}catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
    }
    
    private void parseCasts() {
    	Connection dbCon = null;
    	try {
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
		dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?&useSSL=false", "ethanmarc", "122b42");
		
    	Element docEle = castDom.getDocumentElement();
	        
        NodeList nl = docEle.getElementsByTagName("dirfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);
//                System.out.println("Bcasts: " + i);
//                getStars(el);
//                System.out.println("casts: " + i);
                
            	
                	
            		
        				    	
        	    	NodeList filmsNl = el.getElementsByTagName("filmc");
        	    	if (filmsNl != null && filmsNl.getLength() > 0) {
        	    		for (int k = 0; k < filmsNl.getLength(); k++) {
        	    			Element film = (Element) filmsNl.item(k);
        	             	NodeList filmNl = film.getElementsByTagName("m");
        	             	if (filmNl != null && filmNl.getLength() > 0) {
        	             		for (int j = 0; j < filmNl.getLength(); j++) {
        	             			Element mov = (Element) filmNl.item(j);
        	             			String fid = getTextValue(mov, "f");
        	             			String name = getTextValue(mov, "a");
        	             		
        	             			if(fid == null)
        	             				System.out.println("(Casts.xml) Error with f: " + fid);
        	             			
        	             			if(name != null && starsHash.containsKey(name))	{
        	             				starsHash.get(name).addMovie(fid);
        	             			}
        	             			else if(name != null) {
        		             				PreparedStatement idStatement = dbCon.prepareStatement("SELECT nextId FROM star_helper;");
        		        					ResultSet rs = idStatement.executeQuery();
        		             		    	rs.next();
        		             		    	String sid = "xs" + rs.getString("nextId");
        		             			
        		             				Star s = new Star(sid, name, -1);
        		             				starsHash.put(name, s);
        		             				starsHash.get(name).addMovie(fid);	
        		             				
        		             				PreparedStatement nextStatement = dbCon.prepareStatement("UPDATE star_helper SET nextId = nextId + 1;");
        		                    		nextStatement.executeUpdate();
        	             				}
        	             			else {
        	             				System.out.println("(Casts.xml) Error with a: " + name);
        	             			}
        	             		}
        	             	}
        	    		}
        	    	}
            }
        }
    	}catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(dbCon != null)
			{
				try {
					dbCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        	             			
        
    }
    
    private void getStars(Element el) {
    	Connection dbCon = null;
    	try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?&useSSL=false", "ethanmarc", "122b42");
				    	
	    	NodeList filmsNl = el.getElementsByTagName("filmc");
	    	if (filmsNl != null && filmsNl.getLength() > 0) {
	    		for (int i = 0; i < filmsNl.getLength(); i++) {
	    			Element film = (Element) filmsNl.item(i);
	             	NodeList filmNl = film.getElementsByTagName("m");
	             	if (filmNl != null && filmNl.getLength() > 0) {
	             		for (int j = 0; j < filmNl.getLength(); j++) {
	             			Element mov = (Element) filmNl.item(j);
	             			String fid = getTextValue(mov, "f");
	             			String name = getTextValue(mov, "a");
	             		
	             			if(fid == null)
	             				System.out.println("(Casts.xml) Error with f: " + fid);
	             			
	             			if(name != null && starsHash.containsKey(name))	{
	             				starsHash.get(name).addMovie(fid);
	             			}
	             			else if(name != null) {
		             				PreparedStatement idStatement = dbCon.prepareStatement("SELECT nextId FROM star_helper;");
		        					ResultSet rs = idStatement.executeQuery();
		             		    	rs.next();
		             		    	String sid = "xs" + rs.getString("nextId");
		             			
		             				Star s = new Star(sid, name, -1);
		             				starsHash.put(name, s);
		             				starsHash.get(name).addMovie(fid);	
		             				
		             				PreparedStatement nextStatement = dbCon.prepareStatement("UPDATE star_helper SET nextId = nextId + 1;");
		                    		nextStatement.executeUpdate();
	             				}
	             			else {
	             				System.out.println("(Casts.xml) Error with actor name (a tag): " + name);
	             			}
	             			
	             			  
	             		 }
	             	 }
	             }
	    	 }
    	}catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(dbCon != null)
			{
				try {
					dbCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }

    /**
     * I take an employee element and read the values in, create
     * an Employee object and return it
     * 
     * @param empEl
     * @return
     */
    private List<Movie> getMovies(Element movEl) {

    	List<Movie> tempMovies = new ArrayList<Movie>();
    	
        //String director = getDirector(movEl);

        NodeList nl = movEl.getElementsByTagName("films");
        Element filmsEl = (Element) nl.item(0);
        NodeList filmsNl = filmsEl.getElementsByTagName("film");
        if (filmsNl != null && filmsNl.getLength() > 0) {
            for (int i = 0; i < filmsNl.getLength(); i++) {
            	
            	Element film = (Element) filmsNl.item(i);
            	String fid = getTextValue(film, "fid");
            	
            	String director = getTextValue(film, "dirn");
            	
            	String title = getTextValue(film, "t");
            	int year = getIntValue(film, "year");
            	
            	
            	
            	if(fid == null)
            		System.out.println("(main.xml) Error with fid: Movie name=" + title + " Value=" + fid);
            	if(title == null)
            		System.out.println("(main.xml) Error with t: Movie name=" + title + " Value=" + title);
            	if(director == null)
            		System.out.println("(main.xml) Error with dirname: Movie name=" + title + " Value=" + director);
            	
            	if(title != null && director != null) {
            		Movie mov = new Movie(fid, title, year, director);
            		
            		
            		
            		NodeList filmNl = film.getElementsByTagName("cats");
                	if (filmNl != null && filmNl.getLength() > 0) {
                        for (int j = 0; j < filmNl.getLength(); j++) {
                        	Element cat = (Element) filmNl.item(j);
                        	String genre = getTextValue(cat, "cat");
                        	if(genre != null)
                        		mov.addGenre(genre.trim());
                        	else
                        		System.out.println("(main.xml) Error with cat: Movie name=" + title + " Value=" + genre);
                        }
                	}
            		
	            	
	            	tempMovies.add(mov);
            	}
            }
        }        

        return tempMovies;
    }
    
    
    
    private String getDirector(Element ele) {
    	NodeList dirNl = ele.getElementsByTagName("director");
    	Element dir = (Element) dirNl.item(0);
    	return getTextValue(dir, "dirname");
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
        	
            Element el = (Element) nl.item(0);
            
            if(el.getFirstChild() != null)
            	textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
    	String n = getTextValue(ele, tagName);
    	if(n == null || !n.matches("[0-9]+"))
    	{
    		System.out.println("(main.xml) Error with year: Element=" + tagName + " Value=" + n);
    		return 0;
    	}
    	
        return Integer.parseInt(n);
    }

    private void printData() {
        Iterator<Star> it = stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        DOMparser dpe = new DOMparser();

        dpe.runExample();
        AddMovies am = new AddMovies();
        am.createMovieHash();
        am.createGenreHash();
        am.add(movies);
        
        AddStars as = new AddStars();
        as.add(starsHash, am.getMovieHash());
    }

}