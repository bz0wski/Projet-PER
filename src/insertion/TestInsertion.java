package insertion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;

/**
 * @author Salim AHMED
 *
 */
public class TestInsertion {

	/**
	 * @param args
	 */
	static final int PRETTY_INDENT_FACTOR = 6;
	public static void main(String[] args) {

		try(InputStream inputStream = new FileInputStream("/Users/limi/Desktop/test/paper_1.xml"); 
			OutputStream output = new FileOutputStream("/Users/limi/Desktop/test/paper_1SAXON.json");
			Writer outputORGJSON = new FileWriter("/Users/limi/Desktop/test/paper_1ORGJSON.json");
			ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			
			/*
			 * Copy input stream so I read the file again.
			 */
			IOUtils.copy(inputStream, baos);
			byte[] bytes = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			
			/*
			 * Using the first method, org.json 
			 */
			long startTimeORGJSON = System.currentTimeMillis();
			JSONObject jsonObject = XML.toJSONObject(IOUtils.toString((InputStream)bais));
			
			//jsonObject.write(outputORGJSON);
			outputORGJSON.append(jsonObject.toString(PRETTY_INDENT_FACTOR));
		//	System.out.println("JSONObject represntation\n"+jsonObject.toString(4));
			System.out.printf("Time elapsed %d\n",System.currentTimeMillis() - startTimeORGJSON);
			
			/*
			 * EOF first method
			 */
			
			if(bais.markSupported()){
				/*
				 * Reset reading mark to beginning of file.
				 */
				bais.reset();
			}
			
			/*
			 * Using the second method, saxon
			 */
			long startTimeSAXON = System.currentTimeMillis();
			/*
			 * Specify the configuration for the XML file.
			 */
			JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).autoPrimitive(true).prettyPrint(true).build();
			/*
			 * Create source (XML).
			 */
			
			XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader((InputStream)bais);
			Source source = new StAXSource(reader);

			/*
			 * Create result (JSON).
			 */
			XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(output);
			Result result = new StAXResult(writer);

			/*
			 * Copy source to result via "identity transform".
			 */
			TransformerFactory.newInstance().newTransformer().transform(source, result);
			
			System.out.printf("Time elapsed SAXON %d\n",System.currentTimeMillis() - startTimeSAXON);

			/*
			 * EOF first method
			 */
			
			try(BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/limi/Desktop/test/paper_1.json"))) {
				/*
				 * Converting file to string
				 */
			//	String wholeDocument = IOUtils.toString(bufferedReader);
				/*
				 * Create mongodb interfacing instance.
				 */
			//	MongoClient mongoClient = new MongoClient("localhost");

			//	DB bdPER = mongoClient.getDB("dbPER");

			//	DBCollection collection =  bdPER.getCollection("papers");
				//BasicDBObject dbObject = new BasicDBObject();
				
			//	DBObject dbObj = (DBObject)JSON.parse(wholeDocument);
				/*insert into the collection
				 * 
				 */
			//	collection.insert(dbObj);
				/*
				 * Close the mongoClient instance and clean up resources
				 */
			//	mongoClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				bais.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/limi/Desktop/test/paper_1.xml"))) {
			// FileInputStream inputStream = new FileInputStream("foo.txt");

		//	String wholeDocument = IOUtils.toString(br);
		//	System.out.println(wholeDocument);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//JSONObject jsonObject = new JSONObject();
	}

}
