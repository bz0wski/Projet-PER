package insertion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;


public class testDate {

	public static void main(String[] args) {
		String startDateString = "06/27/2007";
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
	    Date startDate;
	    
	    String string = "January 2, 2010";
	    String frenchDate = "24/06/2005";
	    DateFormat format = new SimpleDateFormat("d/M/yyyy", Locale.FRENCH);
	 
	    
	    try {
	    	Calendar mydate = new GregorianCalendar();
	    	
	    	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy",Locale.FRANCE);
	    	LocalDate date = LocalDate.parse(frenchDate,dateTimeFormatter);
	    	
	    	//Date date = format.parse(frenchDate);
	    	//mydate.setTime(date);
	    	
	    	StringBuilder builder = new StringBuilder();
	    	
	    	builder.append(date.getMonth()+" ");
	    	builder.append(date.getDayOfMonth()+", ");
	    	builder.append(date.getYear());
	    	
	   	    System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
	   	    System.out.println(builder.toString());
	   	    
	        startDate = df.parse(startDateString);
	        String newDateString = df.format(startDate);
	       // System.out.println(newDateString);
	        
	        
	        
	        
	        String file = "/Users/limi/Desktop/test/paper_1.xml";
	        InputStream inputStream = new FileInputStream(file);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        
	      //  IOUtils.copy(inputStream, baos);
		//	byte[] bytes = baos.toByteArray();
		//	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			
			String wholeDoc = IOUtils.toString(inputStream);
			Pattern pattern = Pattern.compile("(PubliDate"+"\\s*" +"=\\s*)"+ "\"([\\d|/]+)\"");
			Matcher matcher = pattern.matcher(wholeDoc);
			StringBuffer docBuilder = new StringBuffer();
			if (matcher.find()) {
				System.out.println("matched, second group "+matcher.group(2));
				
				LocalDate mongoDate = LocalDate.parse(matcher.group(2),dateTimeFormatter);
				StringBuilder strbuilder = new StringBuilder();
		    	strbuilder.append("PubliDate=\"");
				strbuilder.append(mongoDate.getMonth()+" ");
				strbuilder.append(mongoDate.getDayOfMonth()+", ");
				strbuilder.append(mongoDate.getYear()+"\"");
				
				System.out.println("New date "+strbuilder.toString());
				
				//matcher.replaceFirst(strbuilder.toString());
				matcher.appendReplacement(docBuilder, strbuilder.toString());
				matcher.appendTail(docBuilder);
			}else {
				System.out.println("not matched");
			}
			
			System.out.println("whole document "+wholeDoc);
			
			System.out.println("String buffer "+docBuilder.toString());
			//if(bais.markSupported()){
				/*
				 * Reset reading mark to beginning of file.
				 */
			//	bais.reset();
		//	}
	        inputStream.close();
	       // bais.close();
	    } catch (ParseException|IOException e) {
	        e.printStackTrace();
	    }finally{
	    	
	    }
	    

	}

}
