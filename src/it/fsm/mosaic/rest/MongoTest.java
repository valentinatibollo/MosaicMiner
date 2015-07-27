package it.fsm.mosaic.rest;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient("xx.xx.xx.xx", 27017);

			DB db = mongo.getDB("mosaic");
			DBCollection collection = db.getCollection("process");

			ObjectId id = new ObjectId("538dc2848ad07fbe7152480c");

			BasicDBObject query = new BasicDBObject();

			query.put("_id", id);

			DBObject dbObj = collection.findOne(query);
			
			System.out.println(dbObj.toString());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(mongo!=null)
				mongo.close();
		}

	}

}
