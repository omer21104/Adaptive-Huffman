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
		BinaryIn in = null;
		BinaryOut out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;

		try 
		{
			inStream = new FileInputStream(input_names[0]);
			outStream = new FileOutputStream(output_names[0]);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("File not found. Terminating");
			e.printStackTrace();
			System.exit(1);
		}

		in = new BinaryIn(inStream);
		out = new BinaryOut(outStream);

		// init huffman tree
		HuffmanTree huffmanTree = new HuffmanTree(this.numberOfSymbols);

		Symbol currentSymbol = null;
		Node currentNode = null;
		boolean reachedEOF = false;
		Stack<Boolean> currentPathToNode = null;

		// write 4 bits for symbol size
		writeSymbolSizeHeader(out);
		
		while (!in.isEmpty()) 
		{
			// read bytes according to symbol length
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
					byte tmpByteArr[] = Arrays.copyOf(currentBytes, i + 1);
					currentBytes = tmpByteArr;
					
					reachedEOF = true;
					break;
				}
			}
			
			// set bytes to symbol
			currentSymbol = new Symbol(currentBytes);

			// check if the symbol exists in the tree
			currentNode = huffmanTree.containsSymbol(currentSymbol);

			// not a new symbol
			if (currentNode != null) 
			{
				currentPathToNode = currentNode.getPathToThisNode();
				
				huffmanTree.updateTree(currentNode);
			}

			// encounter a new symbol
			else 
			{
				currentPathToNode = huffmanTree.getCurrentNYT().getPathToThisNode();
				
				huffmanTree.addNewSymbolNode(currentSymbol); 
			}
			
			// unwind path to node stack
			while (!currentPathToNode.isEmpty()) 
			{
				if (currentPathToNode.pop() == Node.LEFT_CHILD) 
				{
					out.write(ZERO_BIT);
				}
				else 
				{
					out.write(ONE_BIT);
				}
			}
			
			// write bytes if it was a new symbol
			if (currentNode == null) 
			{
				byte bytesToWrite[] = currentSymbol.getBytes();
				for (int i = 0; i < bytesToWrite.length; i++) 
				{
					out.write(bytesToWrite[i]);
				}
			}
			
			// write a '0' control bit after each code for a symbol
			if (!reachedEOF && !in.isEmpty())
			{				
				out.write(ZERO_BIT);
			}
		}
		
		// write finishing 1 bit
		out.write(ONE_BIT);

		// close resources
		out.flush();
		out.close();

		try 
		{
			inStream.close();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			outStream.close();
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
		BinaryIn in = null;
		BinaryOut out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;

		try 
		{
			inStream = new FileInputStream(input_names[0]);
			outStream = new FileOutputStream(output_names[0]);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("File not found. Terminating");
			e.printStackTrace();
			System.exit(1);
		}

		in = new BinaryIn(inStream);
		out = new BinaryOut(outStream);

		// read first 4 bits to get symbol size
		symbolSize = readSymbolSizeFromHeader(in);

		System.out.println("[*] symbol size: " + symbolSize);
		numberOfSymbols = (int)Math.pow(2,(symbolSize * 8));
		
		// init huffman tree
		HuffmanTree huffmanTree = new HuffmanTree(this.numberOfSymbols);

		// read one symbol at a time, traverse the huffman tree
		Node root = huffmanTree.getRoot();
		Node traverseNode = root;
		Symbol currentSymbol = null;
		String currentCode = "";
		boolean currentBit;
		boolean reachedEOF = false;
		boolean controlBit = false;
		
		while (!in.isEmpty()) 
		{
			if (traverseNode == null) 
			{
				break;
			}
			
			// read bits until you reach a leaf
			if (traverseNode.isLeaf()) 
			{
				// nyt - new symbol encountered
				if (traverseNode.isNYT()) 
				{	
					// read bytes according to symbol length
					byte currentBytes[] = new byte[symbolSize];
					for (int i = 0; i < symbolSize; i++) 
					{
						try 
						{
							currentBytes[i] = in.readByte();
						} 
						catch(NoSuchElementException e) 
						{
							// EOF - handle case where the last bytes left are smaller than the symbol size
							byte tmpByteArr[] = Arrays.copyOf(currentBytes, i + 1);
							currentBytes = tmpByteArr;

							break;
						}
					}
					
					// create a symbol with read bytes
					currentSymbol = new Symbol(currentBytes);
					
					// save binary representation of the symbol
					currentCode = Converter.bytesToString(currentSymbol.getBytes());
				
					huffmanTree.addNewSymbolNode(currentSymbol);
				}

				// not nyt - byte already exists
				else 
				{
					// take the value of the node
					currentSymbol = traverseNode.getSymbol();
					
					// update tree
					huffmanTree.updateTree(traverseNode);	
					
					currentCode = Converter.bytesToString(currentSymbol.getBytes());	
				}
				
				// write the code
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
				
				// read one more control bit
				try 
				{
					controlBit = in.readBoolean();
				}
				catch(NoSuchElementException e)
				{
					
				}
				
				if (controlBit == ONE_BIT)
				{
					reachedEOF = true;
				}
				
				// reset traverseNode
				traverseNode = root;
				
				// reset currentCode
				currentCode = "";
			}
			
			if (reachedEOF)
			{
				break;
			}

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
		}

		// close resources
		out.flush();
		out.close();

		try 
		{
			inStream.close();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			outStream.close();
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
		
		System.out.println("[*] Finished decompressing");
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
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
}
