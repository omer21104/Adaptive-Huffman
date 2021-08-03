package encoder_decoder;

import javax.swing.SwingWorker;

import gui.eWorkType;
import main.ProgramHandler;

/**
 * This class is a Task to be executed on a separate thread
 */
public class AdaptiveHuffmanEnDeTask extends SwingWorker<Object, Object> 
{
	private eWorkType workType;
	private ProgramHandler handler;
	
	public AdaptiveHuffmanEnDeTask(eWorkType workType, ProgramHandler handler) 
	{
		this.workType = workType;
		this.handler = handler;
	}
	
	@Override
	protected Object doInBackground() throws Exception 
	{
		
		if (workType == eWorkType.compress)
		{
			handler.startCompression();
		}
		else
		{
			handler.startDecompression();
		}
		
		return null;
	}
	
	protected void done()
    {
        try
        {
            handler.showDoneMsg();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
