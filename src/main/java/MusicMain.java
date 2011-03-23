import java.io.IOException;

import org.guicerecipes.Injectors;

import uk.co.unclealex.music.inject.MusicModule;
import uk.co.unclealex.music.sync.SynchroniserService;
import uk.co.unclealex.process.inject.ProcessServiceModule;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class MusicMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Injector injector = Guice.createInjector(new MusicModule(), new ProcessServiceModule());
		injector.getInstance(SynchroniserService.class).synchroniseAll();
		Injectors.close(injector);
	}

}