//package proj3inout;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Arrays;

public class Log extends Thread{
	
	private int id;
	private Vector<String> log;
	private Socket socket;
	private ServerSocket logSocket;
	private String ports, privateIP;
	private String[] publicIPs;
	//public volatile boolean done;
	
	public Log(int id, String[] publicIPs, String port, String privateIP){
		try {
			this.id = id;
			this.publicIPs = publicIPs;
			this.ports = port;
			this.privateIP = privateIP;
			this.log = new Vector<String>(0,1);
			logSocket = new ServerSocket();
			logSocket.bind(new InetSocketAddress(privateIP,Integer.parseInt(port)));
			//done = false;
		}
		catch (Exception e) {
			//e.printStackTrace();
		}

	}
	
	public void run() {
		String siteRequest, message, releaseMessage;
		message = "";
		String[] msgTok;
		String[] msgTok2;
		int siteId;
		
		while(true) {
			try {
				socket = logSocket.accept();
				if(!socket.isConnected()){ 
					continue;
				}
				BufferedReader inFromSite = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outToSite = new DataOutputStream(socket.getOutputStream());
				
				siteRequest = inFromSite.readLine();
				//System.out.println("Log read from Site: " + siteRequest);
				if (siteRequest == null) {
					socket.close();
					continue;
				}
				siteId = Integer.parseInt(siteRequest.split(",")[1]);
			
				if (siteRequest.startsWith("R")) {
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Sending log to Site " + siteId + ".\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					//System.out.println("Sending log to Site " + siteId + ".");
					String output = printLog(log);
					//System.out.println("output = " + output);
					outToSite.writeBytes(output + "\n");
				}
				else if (siteRequest.startsWith("W")) {
					msgTok = siteRequest.split("\"");
					message = msgTok[1];
					msgTok2 = msgTok[0].split(",");
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Site " + siteId + " writing \"" + message + "\" to log.\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					//System.out.println("Writing \"" + message + "\" to log.");
					log.addElement(message);
					outToSite.writeBytes("Append successful\n");
				}
				
				releaseMessage = inFromSite.readLine();
				if(releaseMessage.equals("release")) {
				   	// try {
				   	// 	writeOutput outputText = new writeOutput(output, true);
				   	// 	outputText.writeToOutput("Log received release message from Site " + siteId + "\n");
				   	// }
				   	// catch (IOException e){
				   	// 	System.out.println( e.getMessage() );
				   	// }
					System.out.println("Log received release message from Site " + siteId);
					socket.close();
				}
				
			socket.close();	
			}
			catch (Exception e) {
				//System.out.println("Failed to open Socket in Log.");
			}
		}
	}
	
	public String printLog(Vector<String> log) {
		boolean empty = log.isEmpty();
		String Log = "";
		if (empty == true) {
			Log += "Log empty.";
			//System.out.println("Log empty.");
			return Log;
		}
		else {
			//System.out.print("Log: ");
			int x = 0;
			while(x != log.size()-1) {
				Log += log.get(x) + ", ";
		   		System.out.println(log.get(x) + ", ");
		   		x++;
			}
			Log += log.get(x);
			//System.out.println(log.get(x));
			return Log;
		}
	}
	
}