package uk.co.unclealex.flacconverter.encoded.service.titleformat;

public class TitleFormatFactoryImpl implements TitleFormatFactory {

	private String i_defaultTitleFormat;

	public String getDefaultTitleFormat() {
		return i_defaultTitleFormat;
	}

	public void setDefaultTitleFormat(String defaultTitleFormat) {
		i_defaultTitleFormat = defaultTitleFormat;
	}
}
