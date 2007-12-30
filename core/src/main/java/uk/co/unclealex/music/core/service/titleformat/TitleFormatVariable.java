package uk.co.unclealex.music.core.service.titleformat;

public enum TitleFormatVariable {
	ARTIST("artist"), ALBUM("album"), TRACK("track"), TITLE("title"), EXTENSION("ext"), OWNER("owner"), DEVICE("device");
	
	private String i_substitutionVariable;
	
	private TitleFormatVariable(String substitutionVariable) {
		i_substitutionVariable = substitutionVariable;
	}

	@Override
	public String toString() {
		return getSubstitutionVariable();
	}

	public String toString(int length) {
		return getSubstitutionVariable(length);
	}

	private String getSubstitutionVariable() {
		return i_substitutionVariable;
	}
	
	private String getSubstitutionVariable(int length) {
		return length + ":" + getSubstitutionVariable();
	}

}
