import java.util.ArrayList;

public class Star {
	private String id, name;
	private int birthYear;
	private ArrayList<String> movies = new ArrayList<String>();
	
	public Star() {}
	
	public Star(String id, String name, int birthYear) {
		this.id = id;
		this.name = name;
		this.birthYear = birthYear;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getBirthYear() {
		return birthYear;
	}
	
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	
	public void addMovie(String m) {
		movies.add(m);
	}
	
	public ArrayList<String> getMovies(){
		return movies;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("Birth Year:" + getBirthYear());
		sb.append(", ");
		sb.append("Id:" + getId());
		sb.append(".");
		
		return sb.toString();
	}
}
