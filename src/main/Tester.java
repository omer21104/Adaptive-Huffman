package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import encoder_decoder.AdaptiveHuffmanEncoderDecoder;

/**
 * This class is used for testing AdaptiveHuffmanEncoderDecoder
 */
public class Tester 
{
	static final String sf_CurrentDirPath = System.getProperty("user.dir");
	static final String sf_OutputDirName = "Test Outputs";

	static final String sf_InCompFileName = "OnTheOrigin";
	static final String comp_file_name = "Test";
	static final String decomp_file_name = "OnTheOrigin";
	static final String format = ".txt";

	private final String[] k_InCompPath;
	private final String[] k_OutCompPath;
	private final String[] k_OutDecompPath;

	public Tester() throws Exception
	{
		// init dirs
		if (!new File(sf_CurrentDirPath + "\\" + sf_OutputDirName).mkdirs())
		{
			throw new Exception("Folder already exists, delete it or rename it first");
		}

		String inCompFilePath = sf_CurrentDirPath + "\\ExampleInputs\\" + sf_InCompFileName + format;
		k_InCompPath = new String[]{inCompFilePath};

		k_OutCompPath = new String[]{sf_CurrentDirPath + "\\" + sf_OutputDirName};
		k_OutDecompPath = k_OutCompPath;
	}

	/**
	 * test a single file by compressing it and then decompressing it, comparing input and output.
	 * <br> this method tries all possible symbol sizes allowed by the program implementation.
	 * @param i_CleanupAfter a flag to indicate whether to delete residual files afterwards.
	 */
	public void test(boolean i_CleanupAfter)
	{
		System.out.println(k_OutCompPath[0]);
		final int maxSupportedSymbolSize = 8;
		AdaptiveHuffmanEncoderDecoder ende;
		
		for (int i = 1; i <= maxSupportedSymbolSize; i++) 
		{
			String[] out_comp_name = {k_OutCompPath[0] + "\\" + sf_InCompFileName + i};
			String[] out_decomp_name = {k_OutDecompPath[0] + "\\" + sf_InCompFileName + "_" + i + format};
			
			ende = new AdaptiveHuffmanEncoderDecoder(i);
			ende.Compress(k_InCompPath, out_comp_name);
			ende.Decompress(out_comp_name, out_decomp_name);
			
			// test result of compression
			byte[] arr1 = null;
			try 
			{
				arr1 = Files.readAllBytes(Path.of(k_InCompPath[0]));
			} 
			catch (IOException e) 
			{
				
			}
			byte[] arr2 = null;
			try 
			{
				arr2 = Files.readAllBytes(Path.of(out_decomp_name[0]));
			} 
			catch (IOException e) 
			{
				
			}
			
			boolean areFilesEqual = Arrays.equals(arr1, arr2);
			System.out.println(String.format("%s", areFilesEqual ? "Success" : "Fail"));
		}

		if (i_CleanupAfter)
		{
			finalizeTest();
		}
	}

	private void finalizeTest()
	{
		System.out.println("[*] Deleting residual files");
		deleteDir(new File(k_OutCompPath[0]));
	}

	void deleteDir(File file)
	{
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (! Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}
}
