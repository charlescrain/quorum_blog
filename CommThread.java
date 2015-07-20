//package proj3inout;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;

public class CommThread extends Thread {
	
	private ServerSocket commSocket;
	private Socket socket;
	private String lockReq;
	private Vector<String> activeLocks;
	private LinkedList<String> reqQueue;
	private int id;
	private String privateIP, port;
	
	public CommThread(int id, String privateIP, String port) {
		try {
			this.privateIP = privateIP;
			this.port = port;
			this.id = id;
			//this.output = output;
			//commSocket = new ServerSocket(Integer.parseInt(port));
			commSocket = new ServerSocket();
			commSocket.bind(new InetSocketAddress(privateIP,Integer.parseInt(port)));
			lockReq = "";
			activeLocks = new Vector<String>(0,1);
			reqQueue = new LinkedList<String>();
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
	public void run() {
		String[] splitReq;
		String reqId, tmp;
		Iterator iterator;
		boolean inQueue, inLocks;

		while(true){
			try{	
				inLocks = false;
				inQueue = false;
				socket = commSocket.accept();
				if(!socket.isConnected()){
					socket.close();
					continue;
				}
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

				if( (lockReq = inFromClient.readLine()) == null ){
					socket.close();
					continue;
				}

				splitReq = lockReq.split(",");

				if(splitReq[0].equals("R")){
					//Check to see if request is holding until popped off queue
					iterator = reqQueue.iterator(); //Check if it is already in the queue
					while (iterator.hasNext()) {
						if ((tmp =(String) iterator.next()).substring(2,3).equals(splitReq[1])) {
							inQueue = true;
							break;
						}
					}

					// If its in the Queue check if it can be granted
					if(inQueue){
						//if lockReq is at top of queue check if write lock exists
						if(reqQueue.peek().equals(lockReq)){
							iterator = activeLocks.iterator();
							while(iterator.hasNext()){
								if((tmp =  (String) iterator.next()).substring(0,1).equals("W")){
									inLocks = true;
									break;
								}
							}
							//if W lock exists deny, else pop and grant
							if(inLocks)
								outToClient.writeBytes("No\n");
							else{
								activeLocks.add(reqQueue.pop());
								outToClient.writeBytes("OK\n");
							}
						}else{ // Not at topof queue so wait
							outToClient.writeBytes("No\n");
						}

					}else{
						//if anything in queue add to queue
						if(!reqQueue.isEmpty()){
							reqQueue.add(lockReq);
							outToClient.writeBytes("No\n");
						}else{
							iterator = activeLocks.iterator();
							while(iterator.hasNext()){
								if((tmp =  (String) iterator.next()).substring(0,1).equals("W")){
									inLocks = true;
									break;
								}
							}
							//if Write lock exists add to queue
							if(inLocks){
								reqQueue.add(lockReq);
								outToClient.writeBytes("No\n");
							}else{ // Grant Lock
								activeLocks.add(lockReq);
								outToClient.writeBytes("OK\n");
							}
						}
					}
				} 
				else if (splitReq[0].equals("W")) {
					//Check to see if it is holding because in queue
					iterator = reqQueue.iterator();
					while (iterator.hasNext()) {
						if ((tmp =(String) iterator.next()).substring(2,3).equals(splitReq[1])) {
							inQueue = true;
							break;
						}
					}
					// if its in the queue check if its at the top of queue
					if(inQueue){
						if(reqQueue.peek().equals(lockReq)){
							if(!activeLocks.isEmpty()){
								outToClient.writeBytes("No\n");
							}else{ //Grant Lock
								activeLocks.add(reqQueue.pop());
								outToClient.writeBytes("OK\n");
							}
						}else{ // In Queue but not at top
							outToClient.writeBytes("No\n");
						}
					}
					else{ // Not in queue
						// if any current locks tell it no and add to queue
						if(!activeLocks.isEmpty()){
							reqQueue.add(lockReq);
							outToClient.writeBytes("No\n");
						}
						else{ //Grant Lock
							activeLocks.add(lockReq);
							//System.out.println("Sending OK for write lock back to " + lockReq.substring(2,3));
							outToClient.writeBytes("OK\n");
						}
					}

					//printState();
					
				}else if (splitReq[0].equals("release")){
					//look for lock in activeLocks and remove
					iterator = activeLocks.iterator();
					while(iterator.hasNext()){
						if((tmp =  (String) iterator.next()).substring(2,3).equals(splitReq[1])){
							iterator.remove();
							break;
						}
					   	// try {
					   	// 	writeOutput outputText = new writeOutput(output, true);
					   	// 	outputText.writeToOutput("Communication thread " + id + " released lock from Site " + splitReq[1] + ".\n");
					   	// }
					   	// catch (IOException e){
					   	// 	System.out.println( e.getMessage() );
					   	// }
						//System.out.println("Communication thread " + id + " released lock from Site " + splitReq[1] + ".");
					}
				}else if (splitReq[0].equals("T")){
					//System.out.println("This is not good");
					//Remove from queue the process that sent this
					iterator = reqQueue.iterator();
					while (iterator.hasNext()) {
						if ((tmp =(String) iterator.next()).substring(2,3).equals(splitReq[1])) {
							iterator.remove();
							break;
						}
					}
					iterator = activeLocks.iterator();
					while(iterator.hasNext()){
						if((tmp =  (String) iterator.next()).substring(2,3).equals(splitReq[1])){
							iterator.remove();
							break;
						}
					}
					//System.out.println("Timeout received in comm from " + splitReq[1] );
					//printState();
					outToClient.writeBytes("Timeout\n");

				}
			//printState();
			socket.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}		
	}

	public void printState(){
		Iterator iterator;
		System.out.println("Active Locks: ");
		iterator = activeLocks.iterator();
		while(iterator.hasNext()){
			System.out.println(iterator.next());
		}

		System.out.println("Queue: ");
		iterator = reqQueue.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

	}
}