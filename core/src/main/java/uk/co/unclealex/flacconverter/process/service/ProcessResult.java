package uk.co.unclealex.flacconverter.process.service;

public class ProcessResult {

	private int i_returnValue;
	private String i_output;
	private String i_error;
	
	public ProcessResult(int returnValue, String output, String error) {
		super();
		i_returnValue = returnValue;
		i_output = output;
		i_error = error;
	}

	public int getReturnValue() {
		return i_returnValue;
	}

	public String getOutput() {
		return i_output;
	}

	public String getError() {
		return i_error;
	}
	
	
}
