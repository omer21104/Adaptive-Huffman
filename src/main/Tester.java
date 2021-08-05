package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import encoder_decoder.AdaptiveHuffmanEncoderDecoder;

public class Tester 
{
	static final String[] in_comp = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\ExampleInputs\\OnTheOrigin.txt"};
	static final String[] out_comp_path = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\test outputs\\"};
	static final String[] out_decomp_path = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\test outputs\\"};
	static final String comp_file_name = "Test";
	static final String decomp_file_name = "OnTheOrigin";
	static final String format = ".txt";
	
	public static void test()
	{
		final int maxSupportedSymbolSize = 8;
		AdaptiveHuffmanEncoderDecoder ende;
		
		for (int i = 1; i <= maxSupportedSymbolSize; i++) 
		{
			String[] out_comp_name = {out_comp_path[0] + comp_file_name + i};
			String[] out_decomp_name = {out_decomp_path[0] + decomp_file_name + "_" + i + format};
			
			ende = new AdaptiveHuffmanEncoderDecoder(i);
			ende.Compress(in_comp, out_comp_name);
			ende.Decompress(out_comp_name, out_decomp_name);
			
			// test result of compression
			byte[] arr1 = null;
			try 
			{
				arr1 = Files.readAllBytes(Path.of(in_comp[0]));
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
	}
}
