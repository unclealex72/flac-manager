package uk.co.unclealex.music.sync;

import uk.co.unclealex.music.IpodDevice;

public class IpodSynchroniser extends PythonSynchroniser<IpodDevice> {

	@Override
	protected String[] getCommandArguments() {
		return new String[] { "ipod", getDevice().getMountPoint().getPath() };
	}

}
