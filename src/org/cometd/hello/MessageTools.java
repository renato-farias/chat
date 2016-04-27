package org.cometd.hello;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.ServerMessageImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MessageTools {
	
	private static final MainLogger _logger = new MainLogger(MessageTools.class.getName());
	
	public static Long getTimeStamp() {
		Date date = new Date();
		return date.getTime();		
	}
	
	public static String formatMessageFromFE(ServerMessage message, ServerSession session) {
		
		_logger.debug("Formating message to BackEnd: " + message.getJSON());

		message.remove("id");
		message.put("timestamp", getTimeStamp());

        return message.getJSON();
    }
	
	public static ServerMessage formatMessageFromBE(String message) {
		
		_logger.debug("Formating message to FrontEnd: " + message);
		
		ServerMessage sMessage = new ServerMessageImpl();

		String[] fields = {"data", "timestamp"};
		Map<String, Object> fieldData = getFields(message, fields);
		
		sMessage.remove("id");
    	sMessage.remove("data");
    	sMessage.put("timestamp", getTimeStamp());
    	sMessage.put("data", fieldData.get("data"));
        
        return sMessage;
    }
	
	public static JSONObject parseMsg(String message) {
    	JSONParser jsonParser = new JSONParser();
    	JSONObject jsonObject = null;
    	try {
        	jsonObject = (JSONObject) jsonParser.parse(message);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	jsonParser = null;
        	       	
        return jsonObject;
    }
	
	
	public static Map<String, Object> getFields(String message, String[] fields) {
		
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		
       	JSONObject jsonObject = parseMsg(message);
		for(String field : fields ) {
			dataMap.put(field, jsonObject.get(field));
		}
    	
    	return dataMap;
    }

	public static String getUTF8(byte[] bytes) {
		String string;
		try {
			string = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			string = new String(bytes);
			_logger.error("error formating string: " + string, e);
		}
		return string;
	}

}
