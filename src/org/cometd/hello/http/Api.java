package org.cometd.hello.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;
import org.cometd.hello.db.MongoDBSync;

@SuppressWarnings("serial")
public class Api extends HttpServlet {
	
	static final MainLogger _logger = new MainLogger(Api.class.getName());
	
    private static String getChannel(String url) {
    	String[] url_split = url.split("\\/");
		try {
			return URLDecoder.decode(url_split[2], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null; // ??????
		}
    }
    
    private static String getLatest(String url) {
    	MongoDBSync mongo = MongoDBSync.getInstance();
    	String channel = getChannel(url);
    	return mongo.getLatest(channel, Settings.getMongoCollection());
    }
    
    private static int getCount(String url) {
    	MongoDBSync mongo = MongoDBSync.getInstance();
    	String channel = getChannel(url);
    	return mongo.getCount(channel, Settings.getMongoCollection());
    }
    
    private static String getApi(String url) {
    	MongoDBSync mongo = MongoDBSync.getInstance();
    	String channel = getChannel(url);
    	return mongo.getApi(channel, Settings.getMongoCollection());
    }
    
    private static String notFound() {
    	return "Not Found =[";
    }
    
    private String detectMethod(String url) {
    	if ( url.matches("\\/api\\/.+\\/LATEST\\/?") ) {
    		_logger.debug("Getting the last registry of: " + url);
    		return getLatest(url);
    	} else if ( url.matches("\\/api\\/.+\\/COUNT\\/?") ) {
    		_logger.debug("Getting the amount of registry of: " + url);
    		return String.valueOf(getCount(url));
    	} else if ( url.matches("\\/api\\/.+\\/?") ) {
    		_logger.debug("Getting the all registry of: " + url);
    		return getApi(url);
    	}
		return notFound();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        _logger.debug("Handling with URL requested: " + request.getRequestURI());
        try {
        	response.getWriter().println(detectMethod(request.getRequestURI()));
        } catch (Exception e) {
        	response.getWriter().println(e.getMessage()); // what?
        	_logger.error("error writing response", e);
        }
    }
    
    
}