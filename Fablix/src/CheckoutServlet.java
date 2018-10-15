import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.PreparedStatement;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


@WebServlet(name = "Checkout", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccNum = request.getParameter("ccNum");
        String expDate = request.getParameter("expDate");
        User user = (User)request.getSession().getAttribute("user");
        
        PrintWriter out = response.getWriter();
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedbMaster");

            Connection dbCon = ds.getConnection();
            //Statement checkUserInputs = dbCon.createStatement();
            
	        
            //String checkInputs = "SELECT * FROM creditcards as cc " + 
            //"WHERE cc.id = '"+ccNum+"' AND cc.firstName = '"+firstName+"' AND cc.lastName = '"+lastName+"' AND cc.expiration = '" + expDate + "';";
            
            String checkInputs = "SELECT * FROM creditcards as cc " + 
                    "WHERE cc.id =? AND cc.firstName =? AND cc.lastName =? AND cc.expiration =?;";
            
            PreparedStatement checkUserInputs = dbCon.prepareStatement(checkInputs);
            checkUserInputs.setString(1, ccNum);
            checkUserInputs.setString(2, firstName);
            checkUserInputs.setString(3, lastName);
            checkUserInputs.setString(4, expDate);
            
    		ResultSet checkInputResults = checkUserInputs.executeQuery();
    		
    		
    		
            JsonArray jsonArray = new JsonArray();

	        if (checkInputResults.next()) {
		        //String getMoviesInCartQuery = "SELECT m.title, sc.movieId, sc.quantity FROM shopping_cart as sc, movies as m WHERE m.id = sc.movieId AND sc.customerId = '" + user.getId() + "';";
		        String getMoviesInCartQuery = "SELECT m.title, sc.movieId, sc.quantity FROM shopping_cart as sc, movies as m WHERE m.id = sc.movieId AND sc.customerId =?;";
	        	
	        	
		        
//		        Statement getMoviesInCartStatement = dbCon.createStatement();
//		        Statement insertSalesStatement = dbCon.createStatement();
//        		Statement getSaleIdStatement = dbCon.createStatement();
        		
        		PreparedStatement getMoviesInCartStatement = dbCon.prepareStatement(getMoviesInCartQuery);
        		getMoviesInCartStatement.setString(1, user.getId());
        		
        		PreparedStatement insertSalesStatement = null;
        		PreparedStatement getSaleIdStatement = null;
        		
		        ResultSet getCartMoviesResult = getMoviesInCartStatement.executeQuery();
	            
	            String insertIntoSalesQuery, m_id, m_title, saleId;
	            int quantity;
	            String getSaleIdQuery = "SELECT COUNT(*) FROM sales;"; //id is auto-increment, so getting the count of the table gives us the id
	            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	            String formatted = df.format(new Date()); //Set the sale date to right now
	            
	            while(getCartMoviesResult.next()) {
	            	m_id = getCartMoviesResult.getString("movieId");
	            	m_title = getCartMoviesResult.getString("title");
	            	quantity = Integer.parseInt(getCartMoviesResult.getString("quantity"));
	    			
	            	JsonObject jsonObject = new JsonObject();
	            	
	            	//insertIntoSalesQuery = "INSERT INTO sales VALUES(NULL,'"+user.getId()+"','"+m_id+"','" +formatted+"');";
	            	insertIntoSalesQuery = "INSERT INTO sales (id, customerId, movieId, saleDate) VALUES(NULL,?,?,?);";
            		insertSalesStatement = dbCon.prepareStatement(insertIntoSalesQuery);
            		insertSalesStatement.setString(1, user.getId());
            		insertSalesStatement.setString(2, m_id);
            		insertSalesStatement.setString(3, formatted);
            			            	
            		//Insert a movie sale into sales corresponding to the quantity in the shopping cart
	            	for(int i = 0; i < quantity; i++) {
	            		insertSalesStatement.executeUpdate();
	            	}
	            	
	            	//Get the most recent saleId by computing the count of rows in the table
	            	getSaleIdStatement = dbCon.prepareStatement(getSaleIdQuery);
	            	ResultSet getSaleIdResults = getSaleIdStatement.executeQuery();
	            	if(getSaleIdResults.next()) {
	            		saleId = getSaleIdResults.getString("count(*)");
	            		if(quantity > 1) {
	            			saleId = Integer.toString((Integer.parseInt(saleId)-quantity+1))+"-"+saleId;
	            		}	            		
	            	}
	            	else {
	            		saleId = "0";
	            	}
	            	
	            	jsonObject.addProperty("title", m_title);
	            	jsonObject.addProperty("saleId", saleId);
	            	jsonObject.addProperty("quantity", Integer.toString(quantity));
					jsonArray.add(jsonObject);
		        }
	            
	            //String deleteFromCartQuery = "DELETE FROM shopping_cart WHERE customerId = '" + user.getId() + "';"; 
	            String deleteFromCartQuery = "DELETE FROM shopping_cart WHERE customerId = ?;"; 
	            
		        //Statement deleteFromCartStatement = dbCon.createStatement();
	            PreparedStatement deleteFromCartStatement = dbCon.prepareStatement(deleteFromCartQuery);
	            deleteFromCartStatement.setString(1, user.getId());
		        deleteFromCartStatement.executeUpdate();
	            	            
	
	            response.getWriter().write(jsonArray.toString());
	        } else {
	            response.getWriter().write(jsonArray.toString()); //Array result will be empty
	        }
        }catch(Exception e){
    		e.printStackTrace();
    		
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
        }
    }
}
