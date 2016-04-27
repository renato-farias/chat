package org.cometd.hello.db;

import java.io.IOException;

import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.json.Json;

public class MongoDB {
	
	static final MainLogger _logger = new MainLogger(MongoDB.class.getName());

	private static MongoDB instance;	
	MongoClientConfiguration config = new MongoClientConfiguration();
	MongoClient mongoClient;
	MongoDatabase db;
	
	public MongoDB() {
		_logger.info("Loading MongoDB");
		_logger.info("MONGO_HOST: " + Settings.getMongoHost());
		_logger.info("MONGO_PORT: " + Settings.getMongoPort());
		_logger.info("MONGO_DB: " + Settings.getMongoDb());
		_logger.info("MONGO_COLLECTION: " + Settings.getMongoCollection());
		_logger.info("MONGO_READONLY: " + Settings.getMongoReadOnly());
		
		this.config = new MongoClientConfiguration();
		
		try {
			_logger.info("Connecting on database");
			this.config.addServer(Settings.getMongoHost() + ":" + Settings.getMongoPort());
			mongoClient = MongoFactory.createClient(config);
			_logger.debug("Getting database");
			db = mongoClient.getDatabase(Settings.getMongoDb());
			_logger.debug("Database " + db.getName() + " selected");
		} catch (Exception e) {
			_logger.error("error connecting to database", e);
		}
	}
	
	private MongoCollection getCollection(String collectionName) {
		return this.db.getCollection(collectionName);
	}
	
	public boolean isOkay() {
		Document ping = db.runCommand("ping");
		if ( ping.get("ok").getValueAsString().equals("1.0") ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private DocumentAssignable parseJSON(String jsonString) {
		Document document = Json.parse(jsonString);
		_logger.debug("Parsing JSON from string: " + jsonString);
		return document;
	}

	
	public void insertJSON(String jsonString, String collectionName) {
		try {
			_logger.debug("Trying to insert message [" + jsonString + "] into colection [" + collectionName + "]");
			getCollection(collectionName).insertAsync(parseJSON(jsonString));
			_logger.debug("Message [" + jsonString + "] inserted into colection [" + collectionName + "]");
		} catch (Exception e) {
			_logger.error("Message [" + jsonString + "] not inserted into colection [" + collectionName + "]", e);
		}
	}
	
	public void disconnect() {
		_logger.info("Disconnecting from database");
		try {
			mongoClient.close();
		} catch (IOException e) {
			_logger.error("error disconnecting from database", e);
		}
	}
	
	public static synchronized MongoDB getInstance() {
        if (instance == null) {
            instance = new MongoDB();
        }
        return instance;
    }

}
