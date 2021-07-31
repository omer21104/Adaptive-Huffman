/**
 * Assignment 1
 * Submitted by: 
 * Student 1. 	ID# 315740118
 * Student 2. 	ID# 311263842
 */

package base;

public interface Compressor
{
	abstract public void Compress(String[] input_names, String[] output_names);
	abstract public void Decompress(String[] input_names, String[] output_names);

	abstract public byte[] CompressWithArray(String[] input_names, String[] output_names);
	abstract public byte[] DecompressWithArray(String[] input_names, String[] output_names);
}
