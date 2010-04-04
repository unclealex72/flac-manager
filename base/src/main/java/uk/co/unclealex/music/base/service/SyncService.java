package uk.co.unclealex.music.base.service;

import java.io.IOException;

public interface SyncService {

	public void synchronise(SynchroniserCallback synchroniserCallback) throws IOException;
}
