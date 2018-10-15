import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

@WebServlet("/servlet/AjaxTest")
public class AjaxTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AjaxTest() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Calendar c = Calendar.getInstance();

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println(c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
    }
}
