import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "_DashboardLoginFilter", urlPatterns = "/*")
public class _DashboardLoginFilter implements Filter {

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

        if (httpRequest.getSession().getAttribute("employee") == null) {
            httpResponse.sendRedirect("_dashboard_login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        requestURI = requestURI.toLowerCase();

        return requestURI.contains("_dashboard_login") || !(requestURI.contains("_dashboard"));
    }

    public void init(FilterConfig fConfig) {
    }

    public void destroy() {
    }


}
