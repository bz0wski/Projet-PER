/**
 * 
 */
package insertion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

/**
 *Cette class permettra de dupliquer les fichiers afin de creer une base plus grande.
 ** @author Salim AHMED
 */
public class GenerateurFichiers {

	/**
	 * @param args
	 */
	private static String inputDossier = null;
	private static String outputDossier = null;

	public static String loadUserDocuments(){

		return System.getProperty("user.home")+File.separator+"Documents";
	}

	public static void main(String[] args) {
		String documents = loadUserDocuments();
		System.out.println("Donner le nom du dossier où sont stockés les fichiers.");	
		try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			String nomDossier = in.readLine();
			while(nomDossier.isEmpty()){
				System.out.println("Donner le nom du dossier où sont stockés les fichiers.");
				nomDossier = in.readLine();
			}
			inputDossier = documents+File.separator+nomDossier;

			// Saisie du nom du dossier où seront stockés les fichiers JSON convertis.
			System.out.println("Donner le nom du dossier où stocker les fichiers de sorti.");
			String nomDossierSortie = in.readLine();

			while(nomDossierSortie.isEmpty()){
				System.out.println("Donner le nom du dossier où stocker les fichiers de sorti.");
				nomDossierSortie = in.readLine();
			}

			outputDossier = documents+File.separator+nomDossierSortie;

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (outputDossier == null || inputDossier == null)
			return;

		try {
			Files.walk(Paths.get(inputDossier)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					System.out.println(filePath);

					try (InputStream inputStream = new FileInputStream(filePath.toString());
							ByteArrayOutputStream baos = new ByteArrayOutputStream()){

						IOUtils.copy(inputStream, baos);
						byte[] bytes = baos.toByteArray();
						ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

						for (int i = 0; i < 1000; i++) {

							String nomFichier = filePath.toFile().getName().replace(".xml", "");
							nomFichier = nomFichier+i+".xml";
							System.out.println("Nom du fichier NEW: "+nomFichier);
							Writer outputORGJSON = new FileWriter(outputDossier+File.separator+nomFichier);
							outputORGJSON.write(IOUtils.toString((InputStream)bais));
							outputORGJSON.close();
							bais.reset();
						}

					} catch (Exception e) {	
						e.printStackTrace();
						System.exit(500);
					}finally{

					}


				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}



	}
}
