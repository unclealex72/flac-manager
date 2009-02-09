package uk.co.unclealex.music.base.writer;

public enum Progress implements Comparable<Progress> {
	PREPARING("PREPARING"), WRITING("WRITING"), FINALISING("FINALISING"), FINISHED("FINISHED");
	
	private String i_status;

	private Progress(String status) {
		i_status = status;
	}
	
	@Override
	public String toString() {
		return i_status;
	}

	public String getStatus() {
		return i_status;
	}
}
