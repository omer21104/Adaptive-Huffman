package main;

import java.io.IOException;

public class Runner
{
	public static void main(String[] args) throws IOException 
	{
		ProgramHandler handler = new ProgramHandler();
		handler.initAndShowGui();
		
		// uncomment to test outputs
//		Tester.test();
		}
}
