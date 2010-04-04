package uk.co.unclealex.music.base.process.service;

public interface ProcessCallback {

	public void lineWritten(String line);
	
	public void errorLineWritten(String line);
}
