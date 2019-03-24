package forklift;

import java.text.*; 
import java.util.*; 

public class DateFormatTest 
{ 
  public DateFormatTest() 
  { 
    String dateString = "2001-03-09"; 
     
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
    Date convertedDate = null;
	try {
		convertedDate = dateFormat.parse(dateString);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

    System.out.println("Converted string to date : " + convertedDate); 
  } 

  public static void main(String[] argv) 
  { 
    new DateFormatTest(); 
  } 
} 



