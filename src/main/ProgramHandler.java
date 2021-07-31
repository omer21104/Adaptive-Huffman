package main;

import encoder_decoder.AdaptiveHuffmanEncoderDecoder;
import gui.ProgramGui;

/**
 * this class manages all events triggered by the gui and fires them in the encoder decoder
 * @author ASUS
 *
 */
public class ProgramHandler {
	private AdaptiveHuffmanEncoderDecoder encoderDecoder;
	private ProgramGui gui;
	
	public ProgramHandler() {
		
	}
	
	public ProgramHandler(ProgramGui gui) {
		this.gui = gui;	
	}

	public void startCompression() {
		int symbolSize = gui.getSymbolSize();
		String[] inputFilePath = gui.getCompInputFilePath();
		String[] outputFileFolderPath = gui.getCompOutputFolderPath();
		String[] outputFileName = gui.getCompOutputFileName();
		
		// combine the path for output with name
		String[] fullOutputPath = {outputFileFolderPath[0] + "\\" + outputFileName[0]};
		
		// create encoderDecoder object
		encoderDecoder = new AdaptiveHuffmanEncoderDecoder(symbolSize);
		
		System.out.println(inputFilePath[0]);
		System.out.println(fullOutputPath[0]);
		
		// compress
		encoderDecoder.Compress(inputFilePath, fullOutputPath);
		
		// signal gui that compression has finished
		gui.finalizeCompression();
	}
	
	public void startDecompression() {
		String[] inputFilePath = gui.getDecompInputFilePath();
		String[] outputFolderPath = gui.getDecompOutputFolderPath();
		String[] outputFileName = gui.getDecompOutputFileName();
		
		// combine a full path for output
		String[] fullOutputPath = {outputFolderPath[0] + "\\" + outputFileName[0]};
		
		// create encoderDecoder object
		encoderDecoder = new AdaptiveHuffmanEncoderDecoder();
				
		
		encoderDecoder.Decompress(inputFilePath, fullOutputPath);
		
		// signal gui that decompression has finished
		gui.finalizeDecompression();
	}
	
	public void initGui() {
		gui = new ProgramGui(this);
		gui.showGui(this);
	}
	
	public void setGuiRef(ProgramGui gui) {
		this.gui = gui;
	}
	
	public void showDoneMsg() 
	{	
		gui.showDoneMsg();
	}
	

}
