package uk.co.unclealex.music;

public interface ProcessCallback {

	public void lineWritten(String line);
	
	public void errorLineWritten(String line);
}
