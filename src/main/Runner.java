package main;

import java.io.IOException;

public class Runner
{
	public static void main(String[] args) throws IOException 
	{
//		ProgramHandler handler = new ProgramHandler();
//		handler.initAndShowGui();

		//System.out.println(System.getProperty("user.dir"));

		// uncomment to test outputs
		try
		{
			Tester test = new Tester();
			test.test(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
