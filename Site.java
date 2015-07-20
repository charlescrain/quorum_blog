//package proj3inout;

import java.io.*;
import java.net.*;
//import java.util.Vector;
//import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class Site extends Thread {
	
	private int id;
	private String[] publicIPs, ports;
	private String privateIP;
	//private volatile Vector<String> log;
	public CommThread commThread;

	String output;
	String input;
	//public volatile boolean done;
	
	public Site(int id, String[] publicIPs, String[] ports, String privateIP){
		this.id = id;
		// this.input = input;
		// this.output = output;
		this.publicIPs = publicIPs;
		this.ports = ports;
		this.privateIP = privateIP;
		commThread = new CommThread(id, privateIP, ports[id-1]);
		//done = false;

	}
	
	public void run() {
		Scanner scanner = new Scanner(System.in);
		int timeout;
		boolean timedout;

		
		//while (true) {
			//if(id == 1) {
			String instruction = "";
			int[] quorum = {0, 0};
			boolean readReady;
			boolean writeReady;
			String message = "";
			String[] msgTok;
			String[] instructions;
			int numInstruc;
			
			// System.out.println("ID is " + id);
			// System.out.println("Ports and Public IPs ");
			// for(int i=0;i<6;i++){
			// 	System.out.println(ports[i]+ "     " + publicIPs[i]);
			// }
			// System.out.println(" Private IP is " + privateIP);
			// return;
		
			
			/******* handle instructions *******/
			//for(int x=0; x<instructions.length; x++) {
			while(true){
				//String message = "";
				//String[] msgTok;
				instruction = readFromCommLine(scanner);
				if(instruction.startsWith("R") | instruction.startsWith("r")) {
					quorum = makeRandomQuorum();
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Implementing Read.\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					// send read request to members of the quorum
					//System.out.println("Site " + id + " requesting read lock from Site " + quorum[0] + ".");	
					timedout = true;				
					while(timedout){
						readReady = false;
						timeout = 0;
						while(!readReady) {
							if(timeout < 20){
								//System.out.println("About to sendQuorumRequest in R in "+ id);
								readReady = sendQuorumRequest("R", quorum[0]);
								try{
									if(!readReady)
										sleep(5);
								}catch(Exception e) {}
								timeout++;
								timedout = false;
							}else{
								readReady = sendQuorumRequest("T", quorum[0]);
								readReady = sendQuorumRequest("T", quorum[1]);
								timeout = 0;
								//System.out.println("About to makeRandomQuorum in R in " + id);
								quorum = makeRandomQuorum();
								timedout = true;
							}
						}
						if(timedout)
							continue;


						//System.out.println("Site " + id + " requesting read lock from Site " + quorum[1] + ".");
						readReady = false;
						timeout = 0;
						while(!readReady) {
							if(timeout < 20){
								//System.out.println("About to sendQuorumRequest in R in "+ id);
								readReady = sendQuorumRequest("R", quorum[1]);
								try{
									if(!readReady)
										sleep(5);
								}catch(Exception e) {}
								timeout++;
								timedout = false;
							}else{
								readReady = sendQuorumRequest("T", quorum[0]);
								readReady = sendQuorumRequest("T", quorum[1]);
								timeout = 0;
								//System.out.println("About to makeRandomQuorum in R in " + id);
								quorum = makeRandomQuorum();
								timedout = true;
							}
						}
					}
					
					// interact with the log
					//while(!logInteraction("R", 6, null)) {}
					logInteraction("R", 6, null);
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Site " +id+ " sending release to site " + quorum[0] + "\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					//while(!sendReleaseRequest("release", quorum[0])) {}
					sendReleaseRequest("release", quorum[0]);
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Site " +id+ " sending release to site " + quorum[1] + "\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					sendReleaseRequest("release", quorum[1]);
				}
				else if(instruction.startsWith("W") | instruction.startsWith("w")) {
					quorum = makeRandomQuorum();
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Implementing Write: " + instructions[x] + ".\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					// send write request to members of the quorum
					System.out.println("Site " + id + " requesting write lock from Site " + quorum[0] + ".");
					timedout = true;				
					while(timedout){
						writeReady = false;
						timeout = 0;
						while(!writeReady) {
							if(timeout < 20){
								//System.out.println("About to sendQuorumRequest in W in "+ id);
								writeReady = sendQuorumRequest("W", quorum[0]);
								try{
									if(!writeReady)
										sleep(5);
								}catch(Exception e) {}
								timeout++;
								timedout = false;
							}else{
								writeReady = sendQuorumRequest("T", quorum[0]);
								writeReady = sendQuorumRequest("T", quorum[1]);
								timeout = 0;
								//System.out.println("About to makeRandomQuorum in W in " + id);
								quorum = makeRandomQuorum();
								timedout = true;
							}
						}
						if(timedout)
							continue;


						//System.out.println("Site " + id + " requesting write lock from Site " + quorum[1] + ".");
						writeReady = false;
						timeout = 0;
						while(!writeReady) {
							if(timeout < 20){
								System.out.println("About to sendQuorumRequest in W in "+ id);
								writeReady = sendQuorumRequest("W", quorum[1]);
								try{
									if(!writeReady)
										sleep(5);
								}catch(Exception e) {}
								timeout++;
								timedout = false;
							}else{
								writeReady = sendQuorumRequest("T", quorum[0]);
								writeReady = sendQuorumRequest("T", quorum[1]);
								timeout = 0;
								System.out.println("About to makeRandomQuorum in W in " + id);
								quorum = makeRandomQuorum();
								timedout = true;
							}
						}
					}
					
					// interact with the log
					msgTok = instruction.split(" ");
					int i;
					if(msgTok.length != 1) {
						for(i=1; i<msgTok.length-1; i++) {
							message += msgTok[i] + " ";
						}
						message += msgTok[i];
					}
					else {
						message = msgTok[0];
					}
					if(message.length() > 140) {
						message = message.substring(0,140);
					}
					message = "\"" + message + "\"";
					System.out.println("Message: " + message);
					while(!logInteraction("W", 6, message)) {
						try{
							sleep(5);
						}catch(Exception e) {}
					}
					//logInteraction("W", 6, message);
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Site " +id+ " sending release to site " + quorum[0] + "\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					while(!sendReleaseRequest("release", quorum[0])) {
						try{
							sleep(5);
						}catch(Exception e) {}
					}
					//sendReleaseRequest("release", quorum[0]);
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Site " +id+ " sending release to site " + quorum[1] + "\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					while(!sendReleaseRequest("release", quorum[1])) {
						try{
							sleep(5);
						}catch(Exception e) {}
					}
					//sendReleaseRequest("release", quorum[1]);
				}	
			}					
		//}
	}
	
	public String readFromCommLine(Scanner scanner) {
		System.out.print("Enter your instruction: ");		
		String instruction = scanner.nextLine();		
		return instruction;
	}
	
	public boolean sendQuorumRequest(String instruction, int receiverId) {
		String commReply;
		String publicIp = "";

		
		try {
			// error here
			//System.out.println("Made it here");
			//Socket requestSocket = new Socket(publicIPs[receiverId - 1], Integer.parseInt(ports[receiverId-1])); // handled later with receiverId
			Socket requestSocket = new Socket(); // handled later with receiverId
			requestSocket.connect(new InetSocketAddress(publicIPs[receiverId - 1], Integer.parseInt(ports[receiverId-1])), 1000);
			if(!requestSocket.isConnected()) {
				System.out.println("Failed to connect to log in Site: " + id + "\n");
			}
			DataOutputStream outToComm = new DataOutputStream(requestSocket.getOutputStream());
			BufferedReader inFromComm = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			
			if(instruction.equals("R")) {
				// send request
				//System.out.println("Site " + id + " requesting read lock from Site " + receiverId + ".");
				outToComm.writeBytes("R," + id + "\n");
				//System.out.println("Waiting for read reply in "+ id);
				commReply = inFromComm.readLine();
				//System.out.println("Finished for read reply in "+ id);
				if (commReply== null) {
					//System.out.println("Error in read lock request from Site " + id + " to Site " + receiverId + ".");
					requestSocket.close();
					return false;
				}
				else if (commReply.equals("No")) {
					//System.out.println("Read lock request from Site " + id + " to Site " + receiverId + " denied.");
					requestSocket.close();
					return false;
				}
				else if (commReply.equals("OK")) {
					//System.out.println("Read lock request from Site " + id + " to Site " + receiverId + " granted.");
					requestSocket.close();
					return true;
				}else{
					requestSocket.close();
				}
			}
			else if (instruction.equals("W")) {
				// send request
				outToComm.writeBytes("W," + id + "\n");
				//System.out.println("Waiting for write reply in "+ id);
				commReply = inFromComm.readLine();
				//System.out.println("Finished for write reply in "+ id);
				if (commReply  == null) {
					//System.out.println("Error in write lock request from Site " + id + " to Site " + receiverId + ".");
					requestSocket.close();
					return false;
				}
				else if (commReply.equals("No")) {
					//System.out.println("Write lock request from Site " + id + " to Site " + receiverId + " denied.");
					requestSocket.close();
					return false;
				}
				else if (commReply.equals("OK")) {
					//System.out.println("Write lock request from Site " + id + " to Site " + receiverId + " granted.");
					requestSocket.close();
					return true;
				}else{
					requestSocket.close();
				}
			}else if(instruction.equals("T")){
				outToComm.writeBytes("T," + id + "\n");
				commReply = inFromComm.readLine();
				if (commReply  == null) {
					//System.out.println("Error in timeout lock request from Site " + id + " to Site " + receiverId + ".");
					requestSocket.close();
					return false;
				}else if (commReply.equals("Timeout")){
					return false;
				}
			}
			requestSocket.close();
		}
		catch( Exception e ) {
			//System.out.print(e);
			//System.out.println(" at sendQuorumRequest in site " + id);
			return false;
		}
		return false;
	}
	
	public boolean logInteraction(String instruction, int receiverId, String message) {
		String logReply;
		try {
			//Socket logSocket = new Socket(publicIPs[5], Integer.parseInt(ports[5]),50); // handled later with receiverId
			Socket logSocket = new Socket();
			logSocket.connect(new InetSocketAddress(publicIPs[5], Integer.parseInt(ports[5])),2000);
			DataOutputStream outToLog = new DataOutputStream(logSocket.getOutputStream());
			BufferedReader inFromLog = new BufferedReader(new InputStreamReader(logSocket.getInputStream()));
			
			if(instruction.equals("R")) {
				// send request
				//System.out.println("Site " + id + " requesting to read from Log.");
				outToLog.writeBytes("R," + id + "\n");
				logReply = inFromLog.readLine();
				System.out.println("Log: " + logReply);
				System.out.println("Read from Site " + id + " to Log successful.");
				if (logReply == null) {
					//System.out.println("Error in read request from Site " + id + " to Log.");
					logSocket.close();
					return false;
				}
				else {
//				   	try {
//				   		writeOutput outputText = new writeOutput(output, true);
//				   		outputText.writeToOutput("Log read by Site " + id + ": " + logReply + "\n");
//				   	}
//				   	catch (IOException e){
//				   		System.out.println( e.getMessage() );
//				   	}
					//System.out.println("Log read by Site " + id + ": " + logReply);
				}
			}
			else if (instruction.equals("W")) {
				// send request
				//System.out.println("Site " + id + " requesting to write to Log.");
				//System.out.println("Message: " + message);
				outToLog.writeBytes("W," + id + "," + message + "\n");
				logReply = inFromLog.readLine();
				if(logReply == null) {
					//System.out.println("Error in write request from Site " + id + " to Log.");
					logSocket.close();
					return false;
				}
				else if (logReply.equals("Append unsuccessful")) {
					//System.out.println("Write from Site " + id + " to Log unsuccessful.");
					logSocket.close();
					return false;
				}
				else if(logReply.equals("Append successful")) {
//				   	try {
//				   		writeOutput outputText = new writeOutput(output, true);
//				   		outputText.writeToOutput("Write from Site " + id + " to Log successful.\n");
//				   	}
//				   	catch (IOException e){
//				   		System.out.println( e.getMessage() );
//				   	}
					System.out.println("Write from Site " + id + " to Log successful.");
					//logSocket.close();
				}
			}
			// release log
			//System.out.println("Before sending release to log");
			outToLog.writeBytes("release\n");
			//System.out.println("After sending release to log");
			logSocket.close();
			return true;
		}
		catch( Exception e ) {
			//System.out.print(e);
			//System.out.println(" in logInteraction in site " + id);
			return false;
		}
	}
	
	public boolean sendReleaseRequest(String instruction, int receiverId) {
		try {
			Socket requestSocket = new Socket(); // handled later with receiverId
			requestSocket.connect(new InetSocketAddress(publicIPs[receiverId - 1], Integer.parseInt(ports[receiverId-1])), 1000);
			//Socket requestSocket = new Socket(publicIPs[receiverId - 1], Integer.parseInt(ports[receiverId-1]), 50); // handled later with receiverId
			if(!requestSocket.isConnected()) {
				System.out.println("Failed to connect in Site: " + id + "\n");
			}
			DataOutputStream outToComm = new DataOutputStream(requestSocket.getOutputStream());
			
			outToComm.writeBytes(instruction + "," + id + "\n");
			requestSocket.close();
			return true;
		}
		catch (Exception e) {
			//System.out.println(e);
			return false;
		}			
	}
	
	public int[] makeRandomQuorum() {
		Random quorumBuilder = new Random();
		int[] quorum = {0, 0};
		for(int j=0; j<2; j++) {
			int randomInt = quorumBuilder.nextInt(5) + 1;
			while (randomInt == id) {
				randomInt = quorumBuilder.nextInt(5) + 1;
			}
			if (j == 1) {
				while(randomInt == quorum[0] | randomInt == id) {
					randomInt = quorumBuilder.nextInt(5) + 1;
				}
			}
			quorum[j] = randomInt;
		}
//	   	try {
//	   		writeOutput outputText = new writeOutput(output, true);
//	   		outputText.writeToOutput("Quorum from site " + id + ": " + id + ", " + quorum[0] + ", " + quorum[1] + ".\n");
//	   	}
//	   	catch (IOException e){
//	   		System.out.println( e.getMessage() );
//	   	}
		//System.out.println("Quorum from site " + id + ": " + id + ", " + quorum[0] + ", " + quorum[1] + ".");
		return quorum;
	}
	
}