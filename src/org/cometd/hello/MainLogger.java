package org.cometd.hello;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MainLogger {
	
	private Logger logger;
	
	public MainLogger(String className) {
        this.logger = Logger.getLogger(className);
        PropertyConfigurator.configure(Main.getParamLog4j());
    }
	
	public void info(String logMessage) {
		this.logger.info(logMessage);
	}
	
	public void warn(String logMessage) {
		this.logger.warn(logMessage);
	}
	
	public void warn(String logMessage, Throwable e) {
		this.logger.warn(logMessage, e);
	}
	
	public void error(String logMessage) {
		this.logger.error(logMessage);
	}
	
	public void error(String logMessage, Throwable e) {
		this.logger.error(logMessage, e);
	}
	
	public void debug(String logMessage) {
		this.logger.debug(logMessage);
	}
	
	public void startUpLogger() {
		this.logger.info("============================");
		this.logger.info("=                          =");
		this.logger.info("=      HelloChatEngine     =");
		this.logger.info("=                          =");
		this.logger.info("============================");
	}

}
