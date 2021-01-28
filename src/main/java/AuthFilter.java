package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import pac1.AuthHttpServletRequest;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
	public void destroy() {
    }

    @Override
	public void doFilter(ServletRequest  req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new AuthHttpServletRequest((HttpServletRequest)req), res);
    }

    @Override
	public void init(FilterConfig config) throws ServletException {
    }
}
