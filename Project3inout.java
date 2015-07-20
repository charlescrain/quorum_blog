//package proj3inout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project3inout {
	
	public static void main(String[] args) throws InterruptedException {
		//List<Site> sites = new ArrayList<Site>();
		Site site;
		int numLines, siteNum;
		String config_file, privateIP;
		String[] config, splitStr, publicIPs, ports;
		readInput file;

		//List<String> inputs = new ArrayList<String>();
		//List<String> outputs = new ArrayList<String>();
		
		// inputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/site1.txt");
		// inputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/site2.txt");
		// inputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/site3.txt");
		// inputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/site4.txt");
		// inputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/site5.txt");
		 
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/output1.txt");
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/output2.txt");
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/output3.txt");
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/output4.txt");
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/output5.txt");
		// outputs.add("/Users/sarahpilkington/Documents/UCSB/ECE_151/Project_3/Assignment3/src/proj3inout/log.txt");

		// inputs.add("site1.txt");
		// inputs.add("site2.txt");
		// inputs.add("site3.txt");
		// inputs.add("site4.txt");
		// inputs.add("site5.txt");
		 
		// outputs.add("output1.txt");
		// outputs.add("output2.txt");
		// outputs.add("output3.txt");
		// outputs.add("output4.txt");
		// outputs.add("output5.txt");
		// outputs.add("log.txt");
		config_file = "config.txt";
		/******* open input file *******/
		try {
	   		file = new readInput(config_file);
	   		String[] line = file.openInput();
	   		numLines = line.length;
	   		config = new String[numLines];
	   	
	   		int i;
	   		for (i=0; i<line.length; i++) {
		    	config[i] = line[i];
		    }
	   	} catch (IOException e) {
	   		System.out.println( e.getMessage() );
	   		config = new String[1];
		}
		publicIPs = new String[6];
		ports = new String[6];	
		siteNum = Integer.parseInt(config[0]);
		for(int i=1; i<7; i++){
			splitStr = config[i].split("\\s+");
			publicIPs[i-1]= splitStr[0];
			ports[i-1] = splitStr[1];
		}
		splitStr = config[7].split("\\s+");
		privateIP = splitStr[0];
		if(siteNum == 6){
			Log log = new Log(siteNum,publicIPs,ports[siteNum-1],privateIP);
			log.start();
		}else{
			site = new Site(siteNum,publicIPs,ports,privateIP);
			site.start();
			site.commThread.start();
		}




		
		// for (int i=0; i<6; i++) { 
		// 	try {
		// 		writeOutput output = new writeOutput(outputs.get(i), false);
		// 		output.writeToOutput("");
		// 	}
		// 	catch (IOException e){
		// 		System.out.println( e.getMessage() );
		// 	}	
		// }
	
		// for (int i = 1; i < numSites+1; i++) {
	 // 	 	Site site = new Site(i, inputs.get(i-1), outputs.get(i-1));
	 // 	 	sites.add(site);
		// }
		// Log log = new Log(6, outputs.get(5));
		
		// for(int i=0; i< numSites; i++){
		// 	sites.get(i).start();
		// 	sites.get(i).commThread.start();
		// }
		// log.start();
		
		// for(int i=0; i< numSites; i++) {
		// 	sites.get(i).join();
		// }
		

	}	
}