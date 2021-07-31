
package utilities;

import java.util.Arrays;

/**
 * class to handle symbols of variable sizes
 * 
 */

public class Symbol {
	private int len;
	private byte bytes[];
	
	public Symbol(int len) {
		this.len = len;
		bytes = new byte[len];
	}
	
	public Symbol(byte arr[]) {
		bytes = new byte[arr.length];
		len = arr.length;
		setBytes(arr);
	}
	
	public Symbol(byte b) {
		// single byte symbol
		len = 1;
		bytes = new byte[1];
		bytes[0] = b;
	}
	
	public void setBytes(byte arr[]) {	
		for (int i = 0; i < len; i++) {
			bytes[i] = arr[i];
		}
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public String getString() {
		String s = "";
		for (int i = 0; i < len; i++) {
			s += (char)bytes[i];
		}
		return s;
	}
	
	public String toString() {
		return getString();
	}

	
	// Eclipse generated hashCode() and equals() for comparing symbols
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbol other = (Symbol) obj;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		return true;
	}
	
}