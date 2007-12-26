package uk.co.unclealex.music.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CoreMain extends Main {

	@Override
	protected List<String> getContextLocations() {
		String[] locationsArray = new String[] {
				"applicationContext-music-core-jdbc-direct.xml"
		};
		List<String> locationsList = new ArrayList<String>(locationsArray.length);
		locationsList.addAll(Arrays.asList(locationsArray));
		return locationsList;
	}
}
