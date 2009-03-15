package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.DeviceBean;

public interface SpeechWriterFactory {

	public SpeechWriter createSpeechWriter(DeviceBean deviceBean);
}
