package org.cometd.hello.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cometd.hello.BroadCaster;
import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;
import org.cometd.hello.db.MongoDB;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class Probe extends HttpServlet {
	
	static final MainLogger _logger = new MainLogger(Probe.class.getName());
	
	private static boolean checkMongo() {
		try {
			MongoDB mongo = MongoDB.getInstance();
			return mongo.isOkay();
		} catch(Exception e) {
			_logger.error("error checking mongo: " + e.getMessage(), e);
			return false;
		}
		
	}
	
	private static boolean checkBackEnd() {
		try {
			BroadCaster bc = (BroadCaster) Class.forName(Settings.getBroadcastClass()).newInstance();
			return bc.isOkay();
		} catch(Exception e) {
			_logger.error("error checking backend: " + e.getMessage(), e);
			return false;
		}
	}
	
	private String checkAllServices() {
		boolean mongoStatus = checkMongo();
		boolean backendStatus = checkBackEnd();
		boolean evtStatus = false;
		
		if ( mongoStatus && backendStatus) {
			evtStatus = true;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("mongo", mongoStatus);
		result.put("backend", backendStatus);
		result.put("status", evtStatus);

		return JSONObject.toJSONString(result);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        response.getWriter().println(checkAllServices());
    }
	
}
