package org.cometd.hello.http.headers;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class Api implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response,
    		FilterChain filterChain) throws IOException, ServletException {

        if (response instanceof HttpServletResponse){
        	HttpServletResponse alteredResponse = ((HttpServletResponse)response);
        	addHeadersFor200Response(alteredResponse);
        }

        filterChain.doFilter(request, response);
    }

    private void addHeadersFor200Response(HttpServletResponse response){
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Accept, Content-Type, Pragma, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Max-Age", "86400");
    }

    public void destroy() {
    	
    }

    public void init(FilterConfig filterConfig)throws ServletException{
    	
    }
}