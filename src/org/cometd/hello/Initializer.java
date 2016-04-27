package org.cometd.hello;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;

import org.cometd.hello.db.MongoDB;

@SuppressWarnings("serial")
public class Initializer extends GenericServlet {
	
	static final MainLogger _logger = new MainLogger(Initializer.class.getName());
	
	private BroadCaster bc;
	private MongoDB mongo;
	
	public Initializer() {
		_logger.info("Initializing HelloChatEngine - BackEnd");
		this.mongo = MongoDB.getInstance();
	}
	
    @Override
    public void init() throws ServletException {

        // Retrieve the CometD service instantiated by AnnotationCometdServlet
    	MessagingService service = (MessagingService)getServletContext().getAttribute(MessagingService.class.getName());
    	bc = service.getBC();
        bc.getListeners().add(service);
        
        // Start the broadcaster
        try {
       		bc.start();
		} catch (Exception e) {
			_logger.error("error starting bradcaster", e);
		}
    }

	@Override
    public void destroy() {
        bc.stop();
        mongo.disconnect();
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        throw new UnavailableException("Initializer");
    }

}