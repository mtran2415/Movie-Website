import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXparser extends DefaultHandler {
	List<Movie> movies;
	String tempVal;
	private Movie tempMovie;
	
	public SAXparser() {
		movies = new ArrayList<Movie>();
	}
	
	private void parseDoc() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
        	tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("Employee")) {
            //add it to the list
            movies.add(tempMovie);

        } else if (qName.equalsIgnoreCase("Name")) {
        	tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("Id")) {
        	tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("Age")) {
        	tempMovie.setYear(Integer.parseInt(tempVal));
        }

    }

    public static void main(String[] args) {
    	SAXparser spe = new SAXparser();
    }
}
