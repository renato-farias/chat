package org.cometd.hello.db;

import java.util.ArrayList;

import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBSync {

	static final MainLogger _logger = new MainLogger(MongoDBSync.class.getName());

	private static MongoDBSync instance;
	MongoClient mongoClient;
	DB db;
	
	public MongoDBSync() {
		_logger.info("Loading MongoDB - API Wrapper");
		_logger.info("MONGO_HOST: " + Settings.getMongoHost());
		_logger.info("MONGO_PORT: " + Settings.getMongoPort());
		_logger.info("MONGO_DB: " + Settings.getMongoDb());
		_logger.info("MONGO_COLLECTION: " + Settings.getMongoCollection());
		_logger.info("MONGO_READONLY: " + Settings.getMongoReadOnly());
		
		try {
			_logger.info("Connecting on database");
			mongoClient = new MongoClient(Settings.getMongoHost() , Settings.getMongoPort());
			_logger.debug("Getting database");
			db = mongoClient.getDB(Settings.getMongoDb());
			_logger.debug("Database " + db.getName() + " selected");
		} catch (Exception e) {
			_logger.error("error connecting to database", e);
		}
	}
	
	private DBCollection getCollection(String collectionName) {
		return this.db.getCollection(collectionName);
	}
	
	private String cursorToString(String object) {
		return JSON.serialize(object);
	}
	
	private String cursorToString(DBObject object) {
		ArrayList<Object> jsonArray = new ArrayList<Object>();
		jsonArray.add(object);
		return JSON.serialize(jsonArray);
	}
	
	private String cursorToString(DBCursor cursor) {
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		try {
			while (cursor.hasNext()) {
				DBObject nextObject = cursor.next();
				DBObject element = (DBObject) nextObject.get("data");
				Object dbTimestamp = element.get("timestamp");
				String dbTimestampClass = dbTimestamp.getClass().getSimpleName();
				if ( dbTimestampClass.equals("Double") ) {
					Double dTime = (Double) dbTimestamp;
					Long timestamp = dTime.longValue();
					element.removeField("timestamp");
					element.put("timestamp", timestamp);
					nextObject.removeField("data");
					nextObject.put("data", element);
				}
				list.add(nextObject);
			}
		} finally {
			cursor.close();
		}
		return JSON.serialize(list);
	}
	
	public String getLatest(String channel, String collectionName) {
		BasicDBObject query = new BasicDBObject("channel", channel);
		BasicDBObject sort = new BasicDBObject();
		sort.put("_id", -1);
		
		DBCursor cursor = getCollection(collectionName).find(query).sort(sort).limit(1);
		
		if ( cursor.size() > 0) {
			return cursorToString(cursor.next());
		} else {
			return cursorToString("[]");
		}
		
	}
	
	public DBCursor getList(String channel, String collectionName) {
		BasicDBObject query = new BasicDBObject("channel", channel);
		return getCollection(collectionName).find(query);
	}	
	
	public int getCount(String channel, String collectionName) {
		return getList(channel, collectionName).count();
	}
	
	public String getApi(String channel, String collectionName) {
		return cursorToString(getList(channel, collectionName));
	}
	
	public void disconnect() {
		_logger.info("Disconnecting from database");
		try {
			mongoClient.close();
		} catch (Exception e) {
			_logger.error("error disconnecting from database", e);
		}
	}
	
	public static synchronized MongoDBSync getInstance() {
        if (instance == null) {
            instance = new MongoDBSync();
        }
        return instance;
    }


}
