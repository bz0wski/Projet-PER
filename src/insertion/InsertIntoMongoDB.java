/**
 * 
 */
package insertion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

/**
 * @author limi
 *
 */
public class InsertIntoMongoDB {

	/**
	 * @param args
	 */
	private static String outputDossier = null;


	public static String loadUserDocuments(){
		/* String osName = System.getProperty("os.name").toLowerCase();

		String oString =  osName.contains("win") ? "win" :
		        osName.contains("mac") ? "cocoa" : null;*/
		return System.getProperty("user.home")+File.separator+"Documents";
	}


	public static void main(String[] args) {
		String documents = loadUserDocuments();
		/*
		 * Read all generated json files and insert into mongodb
		 */

		// Saisie du nom du dossier où seront stockés les fichiers JSON convertis.

		System.out.println("Donner le nom du dossier où stocker les fichiers de sortie.");

		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			String nomDossierSortie = in.readLine();

			while(nomDossierSortie.isEmpty()){
				System.out.println("Donner le nom du dossier où stocker les fichiers de sortie.");
				nomDossierSortie = in.readLine();
			}

			outputDossier = documents+File.separator+nomDossierSortie;
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			/*
			 * Create mongodb interfacing instance.
			 */
			MongoClient mongoClient = new MongoClient("127.0.0.1");
			DB bdPER = mongoClient.getDB("dbPER");
			DBCollection collection =  bdPER.getCollection("papers");
			
			Files.walk(Paths.get(outputDossier)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					System.out.println(filePath);
					try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()))) {
						/*
						 * Converting file to string
						 */
						String wholeDocument = IOUtils.toString(bufferedReader);						

						

						
						//BasicDBObject dbObject = new BasicDBObject();

						DBObject dbObj = (DBObject)JSON.parse(wholeDocument);

						//insert into the collection
						collection.insert(dbObj);

						// Close the mongoClient instance and clean up resources

						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//close mongoClient when done inserting
			mongoClient.close();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
			e.getLocalizedMessage();
		}


	}

}
