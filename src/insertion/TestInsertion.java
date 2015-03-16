package insertion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

/**
 * Cette classe est responsable de faire la traduction des fichiers XML en JSON et l'insertion 
 * eventuelle dans la base de données Mongodb.
 * @author Salim AHMED
 *
 */
public class TestInsertion {

	/**
	 * @param args
	 */
	private static String inputDossier = null;
	private static String outputDossier = null;
	/*
	 * Cette méthode permet de recupérer le dossier documents.
	 * Ligne de commentaire
	 */

	public static String loadUserDocuments(){
		/* String osName = System.getProperty("os.name").toLowerCase();

		String oString =  osName.contains("win") ? "win" :
		        osName.contains("mac") ? "cocoa" : null;*/
		return System.getProperty("user.home")+File.separator+"Documents";
	}



	static final int PRETTY_INDENT_FACTOR = 6;

	public static void main(String[] args) {
		String documents = loadUserDocuments();

		// Saisie du nom du dossier où sont stockés les fichiers XML à convertir.
		System.out.println("Donner le nom du dossier où sont stockés les fichiers.");	
		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			String nomDossier = in.readLine();

			while(nomDossier.isEmpty()){
				System.out.println("Donner le nom du dossier où sont stockés les fichiers.");
				nomDossier = in.readLine();
			}
			inputDossier = documents+File.separator+nomDossier;

			// Saisie du nom du dossier où seront stockés les fichiers JSON convertis.
			System.out.println("Donner le nom du dossier où stocker les fichiers de sortie.");
			String nomDossierSortie = in.readLine();

			while(nomDossierSortie.isEmpty()){
				System.out.println("Donner le nom du dossier où stocker les fichiers de sortie.");
				nomDossierSortie = in.readLine();
			}

			outputDossier = documents+File.separator+nomDossierSortie;

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (outputDossier == null || inputDossier == null)
			return;

		//In java 8 you can read all the files in a dossier with the new API

		try {
			Files.walk(Paths.get(inputDossier)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					System.out.println(filePath);
					try(InputStream inputStream = new FileInputStream(filePath.toString()); 
							OutputStream output = new FileOutputStream(outputDossier+File.separator+filePath.toFile().getName()+".json");
							Writer outputORGJSON = new FileWriter(outputDossier+File.separator+filePath.toFile().getName()+".json");
							ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

						/*
						 * Copy input stream so I read the file again.
						 */
						IOUtils.copy(inputStream, baos);
						byte[] bytes = baos.toByteArray();
						ByteArrayInputStream bais = new ByteArrayInputStream(bytes);


						/*
						 * Change the date formatting so it can be converted to mongodb date objects.
						 */
						DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy",Locale.FRANCE);

						String wholeDoc = IOUtils.toString((InputStream)bais);
						Pattern pattern = Pattern.compile("(PubliDate"+"\\s*" +"=\\s*)"+ "\"([\\d|/]+)\"");
						Matcher matcher = pattern.matcher(wholeDoc);
						StringBuffer docBuilder = new StringBuffer();
						if (matcher.find()) {
							//System.out.println("matched, second group "+matcher.group(2));

							LocalDate mongoDate = LocalDate.parse(matcher.group(2),dateTimeFormatter);
							StringBuilder strbuilder = new StringBuilder();
							strbuilder.append("PubliDate=\"");
							strbuilder.append(mongoDate.getMonth()+" ");
							strbuilder.append(mongoDate.getDayOfMonth()+", ");
							strbuilder.append(mongoDate.getYear()+"\"");

							System.out.println("New date "+strbuilder.toString());

							/*
							 * replace date in the document
							 */


							matcher.appendReplacement(docBuilder, strbuilder.toString());
							matcher.appendTail(docBuilder);
						}else {
							System.out.println("not matched");
						}

						//reintroduce the modifications into the current inputStream
						bais = new ByteArrayInputStream(docBuilder.toString().getBytes("UTF-8"));
						if(bais.markSupported()){
							/*
							 * Reset reading mark to beginning of file.
							 */
							bais.reset();
						}

						/*
						 * Using the first method, org.json 
						 */
						long startTimeORGJSON = System.currentTimeMillis();


						JSONObject jsonObject = XML.toJSONObject(IOUtils.toString((InputStream)bais));
						jsonObject.write(outputORGJSON);
						outputORGJSON.append(jsonObject.toString(PRETTY_INDENT_FACTOR));						 

						System.out.printf("Time elapsed, 1st method: %d\n",System.currentTimeMillis() - startTimeORGJSON);

						/*
						 * EOF first method
						 */



						/*
						 * Using the second method, saxon
						 * This method will be shelved for the time being
						 */
						//	long startTimeSAXON = System.currentTimeMillis();
						/*
						 * Specify the configuration for the XML file.
						 */
						//	JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).autoPrimitive(true).prettyPrint(true).build();
						/*
						 * Create source (XML).
						 */

						//	XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader((InputStream)bais);
						//	Source source = new StAXSource(reader);

						/*
						 * Create result (JSON).
						 */
						//	XMLStreamWriter writer = new JsonXMLOutputFactory(config).createXMLStreamWriter(output);
						//	Result result = new StAXResult(writer);


						/*
						 * Copy source to result via "identity transform".
						 */
						//	TransformerFactory.newInstance().newTransformer().transform(source, result);

						//	System.out.printf("Time elapsed SAXON: %d\n",System.currentTimeMillis() - startTimeSAXON);

						/*
						 * EOF second method
						 */

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e1) {

			e1.printStackTrace();
		}finally{

		}
		/*
		 * Read all generated json files and insert into mongodb
		 */

		
		try {
			MongoClient mongoClient = new MongoClient("localhost");
			Files.walk(Paths.get(outputDossier)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					System.out.println(filePath);
					try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()))) {
						/*
						 * Converting file to string
						 */
						String wholeDocument = IOUtils.toString(bufferedReader);
						/*
						 * Create mongodb interfacing instance.
						 */
						

						DB bdPER = mongoClient.getDB("dbPER");

						DBCollection collection =  bdPER.getCollection("papers");
						//BasicDBObject dbObject = new BasicDBObject();

						DBObject dbObj = (DBObject)JSON.parse(wholeDocument);

						//insert into the collection
						collection.insert(dbObj);

						// Close the mongoClient instance and clean up resources

						mongoClient.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}



	}
}
