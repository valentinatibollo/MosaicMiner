package it.fsm.mosaic.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Properties;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.bson.types.ObjectId;

@Path("/matlabservice")
public class MatlabService {

    private boolean bool;
   
    // This method is called if HTML is requested
    @POST
    @Produces({MediaType.APPLICATION_JSON, "text/json"})
    @Path("/runprocessminer")
    public String runProcessMiner(String input) throws IOException, InterruptedException {
        
        int cont = input.lastIndexOf("cont");
        int ths = input.lastIndexOf("ths");
        int length = input.lastIndexOf("length");
        int path = input.lastIndexOf("path");
        int l_par = input.length();
        String newS = input.substring(cont + 5, ths - 1);
        String newS2 = input.substring(ths + 4, length - 1);
        String newS3 = input.substring(length + 7, path - 1);
        String newS4 = input.substring(path + 5, l_par);

        System.out.println("cont  : " + newS);
        System.out.println("ths   : " + newS2);
        System.out.println("length: " + newS3);
        System.out.println("path  : " + newS4);
        
        Properties prop = new Properties();
        prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        
        System.out.println("*** properties loaded");

        // load a properties file
        String db1 = (prop.getProperty("mongodb_db1"));
        String collName = (prop.getProperty("mongodb_collection"));
        String pathJIn = (prop.getProperty("pathJIn"));
        String pathSh = (prop.getProperty("pathSh"));
        String server = (prop.getProperty("mongodb_server"));
        String server_port = (prop.getProperty("mongodb_server_port"));
        
        System.out.println("db        : " + db1);
        System.out.println("collName  : " + collName);
        System.out.println("pathJIn   : " + pathJIn);
        System.out.println("pathSh    :" + pathSh);
        System.out.println("server    : " + server);
        System.out.println("server_port: " + server_port);

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
        MongoClient mongo = new MongoClient(server, Integer.valueOf(server_port));
        DB db = mongo.getDB(db1);
        DBCollection collection = db.getCollection(collName);

        ObjectId id = new ObjectId(newS4);

        BasicDBObject query = new BasicDBObject();

        query.put("_id", id);

        DBObject dbObj = collection.findOne(query);
        
        System.out.println("*** mongo obj found");
        
        dbObj.removeField("results");
      
        //scrivo file
        long datelong = System.currentTimeMillis();
        String pathFile = pathJIn + datelong + ".txt";
        
        System.out.println("*** writing file: " + pathFile);
        
        PrintWriter writer = new PrintWriter(pathFile, "UTF-8");
        writer.println(dbObj);
        writer.close();
        
        System.out.println("*** file written: " + pathFile);
        
        File f1 = new File(pathFile);
        System.out.println("*** reading   permissions: " + f1.setReadable(true, false));
        System.out.println("*** writing   permissions: " + f1.setWritable(true, false));
        System.out.println("*** executing permissions: " + f1.setExecutable(true, false));

        //richiamo file sh
        ProcessBuilder pb = new ProcessBuilder(pathSh, newS, newS2, newS3, pathFile);
        Process p = pb.start();
        
        //process output
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        System.out.println("*** *** process error stream START*** ***");
        while ( (line = br.readLine()) != null) {
                   System.out.println(line);	
        	//builder.append(line);
          // builder.append(System.getProperty("line.separator"));
        }
//        String result = builder.toString();
        System.out.println("*** *** process error stream END*** ***");
        
        //leggi file creato da matlab
        int flag = 0;
        String pathFileMat = pathJIn + datelong + "_mat.txt";
        String pathFileEnd = pathJIn + datelong + "_fine.txt";

        while (flag == 0) {
        	System.out.println("*** checkin is file exists: " + pathFileEnd);
            File e_f = new File(pathFileEnd);
            bool = e_f.exists();
            if (bool) {
            	System.out.println("*** file found");   
            	System.out.println("*** reading matlab file:" + pathFileMat);
                File f = new File(pathFileMat);
                FileReader reader = new FileReader(f);

                BufferedReader in = new BufferedReader(reader);
                String strJ = in.readLine();
                strJ = strJ.replace("[,{", "[{").replace("}{", "},{");
                DBObject dbObject = (DBObject) JSON.parse(strJ);
                      
                System.out.println("*** adding results to mongodb obj");
                
                dbObj.put("results", dbObject);

                collection.save(dbObj);

                flag = 1;
                return strJ;
            }
            Thread.currentThread().sleep(1000l);
        }

//inserisci file in collezione solo dopo che lo ha scritto il .sh
        return "SUCCESS";
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, "text/json"})
    @Path("/runprocessminertest")
    public String runProcessMinerTest(String input){
    	String results = "{'histories':[{'label':'story_Stable','steps': [{'label':'Stable', 'id':'event', 'n_pts':10, 'time':4391,'25prctile':1322,'75prctile':6035,'min':382,'max':12218,'h':4274.874727,'num_classes':4.333333,'patients': [{'idcod':5279,'duration':4957},{'idcod':5983,'duration':6035},{'idcod':18756,'duration':3447},{'idcod':18777,'duration':3824},{'idcod':19287,'duration':943},{'idcod':19627,'duration':10103},{'idcod':19973,'duration':1322},{'idcod':20736,'duration':5370},{'idcod':20767,'duration':382},{'idcod':21006,'duration':12218}]}]},{'label':'story_Stable_1stLevel','steps': [{'label':'Stable', 'id':'event', 'n_pts':1, 'time':6504,'25prctile':6504,'75prctile':6504,'min':6504,'max':6504,'h':0.000000,'num_classes':1.000000,'patients': [{'idcod':20345,'duration':6504}]},{'label':'1stLevel', 'id':'event', 'n_pts':1, 'time':690,'25prctile':690,'75prctile':690,'min':690,'max':690,'h':0.000000,'num_classes':1.000000,'patients': [{'idcod':20345,'duration':690}]}]},{'label':'story_Stable_1stLevel_2ndLevel','steps': [{'label':'Stable', 'id':'event', 'n_pts':2, 'time':6677,'25prctile':5525,'75prctile':7829,'min':5525,'max':7829,'h':4032.000000,'num_classes':2.003433,'patients': [{'idcod':8520,'duration':5525},{'idcod':20627,'duration':7829}]},{'label':'1stLevel', 'id':'event', 'n_pts':2, 'time':162,'25prctile':68,'75prctile':256,'min':68,'max':256,'h':329.000000,'num_classes':2.003433,'patients': [{'idcod':8520,'duration':68},{'idcod':20627,'duration':256}]},{'label':'2ndLevel', 'id':'event', 'n_pts':2, 'time':262,'25prctile':134,'75prctile':390,'min':134,'max':390,'h':448.000000,'num_classes':2.003433,'patients': [{'idcod':8520,'duration':134},{'idcod':20627,'duration':390}]}]},{'label':'story_Stable_1stLevel_3rdLevel','steps': [{'label':'Stable', 'id':'event', 'n_pts':4, 'time':5161,'25prctile':4796,'75prctile':9310,'min':4748,'max':13142,'h':7126.408264,'num_classes':3.006867,'patients': [{'idcod':5796,'duration':4844},{'idcod':18183,'duration':5478},{'idcod':19261,'duration':13142},{'idcod':20869,'duration':4748}]},{'label':'1stLevel', 'id':'event', 'n_pts':4, 'time':415,'25prctile':206,'75prctile':1476,'min':2,'max':2531,'h':2001.187745,'num_classes':3.006867,'patients': [{'idcod':5796,'duration':420},{'idcod':18183,'duration':2},{'idcod':19261,'duration':410},{'idcod':20869,'duration':2531}]},{'label':'3rdLevel', 'id':'event', 'n_pts':4, 'time':1259,'25prctile':463,'75prctile':2236,'min':434,'max':2445,'h':1816.669148,'num_classes':3.006867,'patients': [{'idcod':5796,'duration':434},{'idcod':18183,'duration':2445},{'idcod':19261,'duration':491},{'idcod':20869,'duration':2026}]}]}]}";
    	
    	DateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
    	
    	System.out.println("RunProcessMiner test service called - ".concat(df.format(new GregorianCalendar().getTime())));
    	
    	return results;
    }
    
}
