//package proj3inout;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class writeOutput {
	private String outputName;
	private boolean append = false;
	
	public writeOutput(String name) {
		outputName = name;
	}
	
	public writeOutput(String name, boolean appendBool) {
		outputName = name;
		append = appendBool;
	}
	
	public void writeToOutput (String line) throws IOException {
		FileWriter write = new FileWriter(outputName, append);
		PrintWriter print = new PrintWriter(write);
		
		print.printf("%s", line);
		print.close();
	}
}
