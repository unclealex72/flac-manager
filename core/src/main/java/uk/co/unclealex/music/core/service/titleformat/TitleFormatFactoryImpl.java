package uk.co.unclealex.music.core.service.titleformat;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

@Service
public class TitleFormatFactoryImpl implements TitleFormatFactory {

	private String i_defaultTitleFormat;

	public String getDefaultTitleFormat() {
		return i_defaultTitleFormat;
	}

	@Required
	public void setDefaultTitleFormat(String defaultTitleFormat) {
		i_defaultTitleFormat = defaultTitleFormat;
	}
}
