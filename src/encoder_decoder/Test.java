package encoder_decoder;

import java.util.Vector;

import main.ProgramHandler;
import tree.utils.Node;
import utilities.Symbol;

public class Test {

	public static void main(String[] args) {

//		ProgramHandler handler = new ProgramHandler();
//		handler.initGui();
	
		
//		System.out.println(Converter.stringToInt("0001"));
		
//		Node n1 = new Node(504 ,3 ,new Symbol((byte)4) ,null);
//		Node n2 = new Node(504 ,3 ,new Symbol((byte)5) ,null);
//		
//		System.out.println(n1.equals(n2));
		
		String[] input_name = {"OnTheOrigin.txt"};
		String[] inputDe_name = {"outTest"};
		String[] out_name = {"testing.txt"};
		
		AdaptiveHuffmanEncoderDecoder ende = new AdaptiveHuffmanEncoderDecoder(1);
		ende.Compress(input_name, inputDe_name);
		ende.Decompress(inputDe_name, out_name);
	}
	


}
