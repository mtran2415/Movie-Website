import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String id, title, director;
	private int year;
	private List<String> genres = new ArrayList<String>();
	
	public Movie() {}
	
	public Movie(String id, String title, int year, String director) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = director;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getDirector() {
		return director;
	}
	
	public void setDirector(String dir) {
		this.director = dir;
	}
	
	public void addGenre(String genre) {
		genres.add(genre);
	}
	
	public List<String> getGenres() {
		return genres;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(".");
		
		return sb.toString();
	}
}
