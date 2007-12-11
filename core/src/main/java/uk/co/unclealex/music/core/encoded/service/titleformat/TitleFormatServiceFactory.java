package uk.co.unclealex.music.core.encoded.service.titleformat;

import uk.co.unclealex.music.core.encoded.model.DeviceBean;

public interface TitleFormatServiceFactory {

	public TitleFormatService createTitleFormatService(String titleFormat);

	public TitleFormatService createTitleFormatService(DeviceBean deviceBean);

}