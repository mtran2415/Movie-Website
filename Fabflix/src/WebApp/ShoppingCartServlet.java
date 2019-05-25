import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
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
import java.sql.PreparedStatement;

@WebServlet(name = "ShoppingCart", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ShoppingCartServlet() {
        super();
    }
     
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); 

        PrintWriter out = response.getWriter();

        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
            //Statement statement = dbcon.createStatement();
            
            User user = (User)request.getSession().getAttribute("user");

//            String query = "SELECT m.id, m.title, m.director, sc.quantity " + 
//    				"FROM movies as m, shopping_cart as sc " +
//            		"WHERE m.id = sc.movieId AND sc.customerId = '"+ user.getId() + "' " +
//     				"GROUP BY m.id, m.title, m.director ";
            
            String query = "SELECT m.id, m.title, m.director, sc.quantity " + 
    				"FROM movies as m, shopping_cart as sc " +
            		"WHERE m.id = sc.movieId AND sc.customerId = ?" +
     				"GROUP BY m.id, m.title, m.director ";
            
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, user.getId());
            
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String director = rs.getString("director");
                String quantity = rs.getString("quantity");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("quantity", quantity);

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
