package uk.co.unclealex.flacconverter.encoded.service.titleformat;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public interface TitleFormatServiceFactory {

	public TitleFormatService createTitleFormatService(String titleFormat);

	public TitleFormatService createTitleFormatService(DeviceBean deviceBean);

}