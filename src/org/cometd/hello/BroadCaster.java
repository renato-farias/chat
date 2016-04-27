package org.cometd.hello;

import java.util.EventListener;
import java.util.List;
import java.util.Map;

public interface BroadCaster {
		
	public void start();
	
	public void stop();
	
	public List<Listener> getListeners();
	
	public void sendMessage(String string);
	
	public boolean isOkay();
	
	public static class Update {
        private final String message;

        public Update(String string) {
            this.message = string;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getChannel() {
        	String[] fields = {"channel"};
    		Map<String, Object> fieldData = MessageTools.getFields(message, fields);
            return fieldData.get("channel").toString();
        }

    }
	
	public interface Listener extends EventListener {
	    void onUpdates(List<Update> updates);
	}

}
