package uk.co.unclealex.music.base.service.titleformat;

import uk.co.unclealex.music.base.model.DeviceBean;

public interface TitleFormatServiceFactory {

	public TitleFormatService createTitleFormatService(String titleFormat);

	public TitleFormatService createTitleFormatService(DeviceBean deviceBean);

}