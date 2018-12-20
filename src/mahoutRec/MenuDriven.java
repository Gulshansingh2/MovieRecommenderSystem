package mahoutRec;

import java.io.IOException;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;


public class MenuDriven 
{  

	public void mahoutMain() throws IOException, TasteException 
    {          
     	Scanner scanner = new Scanner(System.in);
     	System.out.print("Enter 1 for ItemBased and 2 for UserBased recommendation: ");
     	int ch = scanner.nextInt();
     	try{
     		
		switch(ch) {
	        case 1:
		        ItemBasedRecommender IBR = new  ItemBasedRecommender() ;
		        IBR.item();
		        break;
	        case 2:
	        	UserBasedRecommender UBR = new UserBasedRecommender() ;
	        	UBR.user();
	        	break;
	        default:
	            System.out.println("invalid");
		}
		
	     }
	     catch (Exception e){
	     }
	     }
     
    

}

	        	
	        	
	            	
	     
     



    	
    	