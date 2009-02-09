package uk.co.unclealex.music.core.service.titleformat;

import uk.co.unclealex.music.core.model.DeviceBean;

public interface TitleFormatServiceFactory {

	public TitleFormatService createTitleFormatService(String titleFormat);

	public TitleFormatService createTitleFormatService(DeviceBean deviceBean);

}