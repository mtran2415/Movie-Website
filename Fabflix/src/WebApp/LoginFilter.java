import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect("/Fablix/login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        requestURI = requestURI.toLowerCase();
        
        return requestURI.endsWith("login.html") || requestURI.endsWith("login.js")
                || requestURI.endsWith("api/login") || requestURI.endsWith("background.css") 
                || requestURI.contains("_dashboard");
    }

    public void init(FilterConfig fConfig) {
    }

    public void destroy() {
    }


}
