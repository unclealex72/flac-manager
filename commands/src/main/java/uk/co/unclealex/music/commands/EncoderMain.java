package uk.co.unclealex.music.commands;

import java.util.ArrayList;
import java.util.List;

import uk.co.unclealex.music.core.mains.CoreMain;

public abstract class EncoderMain extends CoreMain {

	@Override
	protected List<String> getContextLocations() {
		List<String> contextLocations = new ArrayList<String>();
		contextLocations.add("applicationContext-music-encoder-jdbc-direct.xml");
		contextLocations.addAll(super.getContextLocations());
		return contextLocations;
	}
}
