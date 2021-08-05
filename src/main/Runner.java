package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import encoder_decoder.AdaptiveHuffmanEncoderDecoder;

public class Runner
{
	public static void main(String[] args) throws IOException {
//		ProgramHandler handler = new ProgramHandler();
//		handler.initAndShowGui();
		
		String[] in_comp = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\ExampleInputs\\OnTheOrigin.txt"};
		String[] out_comp = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\test outputs\\test4"};
		String[] out_decomp = {"C:\\Users\\ASUS\\Dropbox\\Java\\Adaptive Huffman\\test outputs\\test4out.txt"};
		
		
		AdaptiveHuffmanEncoderDecoder foo = new AdaptiveHuffmanEncoderDecoder(4);
		foo.Compress(in_comp, out_comp);
		foo.Decompress(out_comp, out_decomp);
		
		// test
		byte[] arr1 = Files.readAllBytes(Path.of(in_comp[0]));
		byte[] arr2 = Files.readAllBytes(Path.of(out_decomp[0]));
		
		boolean same = Arrays.equals(arr1, arr2);
		System.out.println(String.format("%s", same ? "Success" : "Fail"));
		}
}
