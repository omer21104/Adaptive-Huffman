package encoder_decoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Stack;

import BinaryIO.BinaryIn;
import BinaryIO.BinaryOut;
import base.Compressor;
import tree_utils.*;
import utilities.Converter;
import utilities.Symbol;

/**
 * This class implements the Adaptive Huffman algorithm for compression / decompression. <br>
 * Vitter algorithm for handling tree updation is used.
 */
public class AdaptiveHuffmanEncoderDecoder implements Compressor 
{
	private static final boolean ONE_BIT = true;
	private static final boolean ZERO_BIT = false;
	private static final int numOfBitsForSymbolSize = 4;
	
	private int symbolSize;
	private int numberOfSymbols;
	
	public AdaptiveHuffmanEncoderDecoder() 
	{
		
	}
	
	public AdaptiveHuffmanEncoderDecoder(int symbolSize) 
	{
		this.setSymbolSize(symbolSize);
		this.numberOfSymbols = (int)Math.pow(2,(symbolSize * 8));
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) 
	{
		// init output and input streams
		BinaryIn in = initBinaryIn(input_names);
		BinaryOut out = initBinaryOut(output_names);

		HuffmanTree huffmanTree = new HuffmanTree(this.numberOfSymbols);

		Symbol currentSymbol = null;
		Node currentNode = null;
		boolean reachedEOF = false;
		Stack<Boolean> currentPathToNode = null;

		writeSymbolSizeHeader(out);
		
		while (!in.isEmpty()) 
		{
			currentSymbol = readNextSymbol(in);
			currentNode = huffmanTree.containsSymbol(currentSymbol);

			if (currentNode != null) 
			{
				// not a new symbol
				currentPathToNode = currentNode.getPathToThisNode();
				huffmanTree.updateTree(currentNode);
			}
			else 
			{
				// encounter a new symbol
				currentPathToNode = huffmanTree.getCurrentNYT().getPathToThisNode();
				huffmanTree.addNewSymbolNode(currentSymbol); 
			}
			
			writePathToNode(out, currentPathToNode);
			
			if (currentNode == null) 
			{
				// write bytes if it was a new symbol
				byte bytesToWrite[] = currentSymbol.getBytes();
				out.write(bytesToWrite);
			}
			
			if (!reachedEOF && !in.isEmpty())
			{				
				// write a '0' control bit after each code for a symbol
				out.write(ZERO_BIT);
			}
		}
		
		// write finishing 1 bit
		out.write(ONE_BIT);

		try 
		{
			closeResources(in, out);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		System.out.println("[*] Finished compressing");
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) 
	{
		// init input and output streams and objects
		BinaryIn in = initBinaryIn(input_names);
		BinaryOut out = initBinaryOut(output_names);
	
		symbolSize = readSymbolSizeFromHeader(in);

		System.out.println("[*] symbol size: " + symbolSize);
		numberOfSymbols = calculateNumberOfSymbols();
		
		HuffmanTree huffmanTree = new HuffmanTree(this.numberOfSymbols);

		Node root = huffmanTree.getRoot();
		Node traverseNode = root;
		Symbol currentSymbol = null;
		String currentCode = "";
		boolean reachedEOF = false;
		
		while (!in.isEmpty()) 
		{
			// read one symbol at a time, traverse the huffman tree
			if (traverseNode == null) 
			{
				break;
			}
			
			// read bits until you reach a leaf
			if (traverseNode.isLeaf()) 
			{
				// new symbol
				if (traverseNode.isNYT()) 
				{	
					currentSymbol = readNextSymbol(in);
					currentCode = Converter.bytesToString(currentSymbol.getBytes());
					huffmanTree.addNewSymbolNode(currentSymbol);
				}

				// not nyt - symbol already exists
				else 
				{
					currentSymbol = traverseNode.getSymbol();
					huffmanTree.updateTree(traverseNode);
					currentCode = Converter.bytesToString(currentSymbol.getBytes());	
				}
				
				writeCode(out, currentCode);
				
				reachedEOF = readControlBit(in);
				
				// reset traverseNode and currentCode
				traverseNode = root;
				currentCode = "";
			}
			
			if (reachedEOF)
			{
				break;
			}

			traverseNode = traverseHuffmanTree(in, traverseNode);
		}

		// close resources
		try 
		{
			closeResources(in, out);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("[*] Finished decompressing");
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) 
	{
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) 
	{
		return null;
	}
	
	private void setSymbolSize(int size) 
	{
		if (size < 1) 
		{
			size = 1;
		}
		
		this.symbolSize = size;
	}
	
	/**
	 * write out 4 bit header for symbol size
	 * @param out BinaryOut object for writing
	 */
	private void writeSymbolSizeHeader(BinaryOut out)
	{
		String symbolSizeBits = Converter.getNBitsString(numOfBitsForSymbolSize, symbolSize);
	
		for (int i = 0; i < numOfBitsForSymbolSize; i++) 
		{
			if (symbolSizeBits.charAt(i) == '1')
			{
				out.write(ONE_BIT);
			}
			else
			{
				out.write(ZERO_BIT);
			}
		}
	}
	
	/**
	 * read in 4 bit header to determine symbol size. <br>
	 * @see #writeSymbolSizeHeader
	 * @param in BinaryIn object to read from file
	 * @return int representation of the symbol size
	 */
	private int readSymbolSizeFromHeader(BinaryIn in) 
	{
		String first4bits = "";
		for (int i = 0; i < numOfBitsForSymbolSize; i++) 
		{
			boolean bit = in.readBoolean();
			if (bit == ONE_BIT)
			{
				first4bits += "1";
			}
			else
			{
				first4bits += "0";
			}
		}
		
		int result = Converter.stringToInt(first4bits);
		return result;
	}
	
	private BinaryIn initBinaryIn(String[] input_names) {
		FileInputStream inStream = null;
		try 
		{
			inStream = new FileInputStream(input_names[0]);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("File not found. Terminating");
			e.printStackTrace();
			System.exit(1);
		}

		return new BinaryIn(inStream);
	}
	
	private BinaryOut initBinaryOut(String[] output_names) 
	{
		FileOutputStream outStream = null;
		try 
		{
			outStream = new FileOutputStream(output_names[0]);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("File not found. Terminating");
			e.printStackTrace();
			System.exit(1);
		}

		return new BinaryOut(outStream);
	}

	private void closeResources(BinaryIn in, BinaryOut out) throws IOException 
	{
		in.close();
		out.close();
	}

	/**
	 * read next symbol from file, this method adjusts for symbols with size smaller than the symbol size,<br> 
	 * so the {@code Symbol} contains only bytes read.
	 * @param in BinaryIn object to read from file
	 * @return {@code Symbol} object with the bytes actually read
	 */
	private Symbol readNextSymbol(BinaryIn in)
	{
		byte currentBytes[] = new byte[symbolSize];
		for (int i = 0; i < currentBytes.length; i++) 
		{
			try 
			{
				currentBytes[i] = in.readByte();
			} 
			catch(NoSuchElementException e) 
			{
				// EOF - handle case where the last bytes left are smaller than the symbol size
				currentBytes = Arrays.copyOf(currentBytes, i);
				
				break;
			}
		}
		
		return new Symbol(currentBytes);
	}
	
	private void writePathToNode(BinaryOut out, Stack<Boolean> PathToNode)
	{
		while (!PathToNode.isEmpty()) 
		{
			if (PathToNode.pop() == Node.LEFT_CHILD) 
			{
				out.write(ZERO_BIT);
			}
			else 
			{
				out.write(ONE_BIT);
			}
		}
	}

	private int calculateNumberOfSymbols() 
	{
		return (int)Math.pow(2 , (symbolSize * 8));
	}

	private void writeCode(BinaryOut out, String currentCode) 
	{
		for (int i = 0; i < currentCode.length(); i++) 
		{
			if (currentCode.charAt(i) == '1') 
			{
				out.write(ONE_BIT);
			} 
			else 
			{
				out.write(ZERO_BIT);
			}
		}
	}
	
	/**
	 * this method determines whether or not EOF has been reached by reading the control bit after each symbol.
	 * @param in BinaryIn object for reading
	 * @return {@code true} if and only if the read bit is 1
	 */
	private boolean readControlBit(BinaryIn in) 
	{
		boolean controlBit = false;
		try 
		{
			controlBit = in.readBoolean();
		}
		catch(NoSuchElementException e)
		{
			
		}
		
		return controlBit;
	}
	
	/**
	 * traverse the huffman tree by reading a bit. 1 means go right, 0 means left
	 * @param in BinaryIn object for reading
	 * @param traverseNode the node to traverse the tree with
	 * @return {@code traverseNode} after having moved it along the appropriate path in the tree
	 */
	private Node traverseHuffmanTree(BinaryIn in, Node traverseNode) 
	{
		boolean currentBit;
		// traverse the tree
		try 
		{
			currentBit = in.readBoolean();
			if (currentBit == ONE_BIT) 
			{
				traverseNode = traverseNode.getRight();
			} 
			else 
			{
				traverseNode = traverseNode.getLeft();
			}
		} 
		catch (NoSuchElementException ex) 
		{
			// EOF
		}
		
		return traverseNode;
	}
}
