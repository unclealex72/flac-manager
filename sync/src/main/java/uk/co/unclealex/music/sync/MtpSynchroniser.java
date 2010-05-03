package uk.co.unclealex.music.sync;

import uk.co.unclealex.music.MtpDevice;

public class MtpSynchroniser extends PythonSynchroniser<MtpDevice> {

	@Override
	protected String[] getCommandArguments() {
		return new String[] { "mtp" };
	}

}
