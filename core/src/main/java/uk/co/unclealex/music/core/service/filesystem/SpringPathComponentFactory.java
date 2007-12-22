package uk.co.unclealex.music.core.service.filesystem;

import java.lang.reflect.Proxy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.core.proxy.ExceptionThrowingInvocationHandler;

@Service
public class SpringPathComponentFactory implements PathComponentFactory, ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	
	@Override
	public PathComponent createRootPathComponent(Context context) {
		return createPathComponent("rootPathComponent", context);
	}

	@Override
	public PathComponent createAlbumPathComponent(Context context) {
		return createPathComponent("albumPathComponent", context);
	}

	@Override
	public PathComponent createArtistPathComponent(Context context) {
		return createPathComponent("artistPathComponent", context);
	}

	@Override
	public PathComponent createEncoderPathComponent(Context context) {
		return createPathComponent("encoderPathComponent", context);	
	}

	@Override
	public PathComponent createFirstLetterOfArtistPathComponent(Context context) {
		return createPathComponent("firstLetterOfArtistPathComponent", context);
	}

	@Override
	public PathComponent createTrackPathComponent(Context context) {
		return createPathComponent("trackPathComponent", context);
	}

	protected PathComponent createPathComponent(String pathComponentName, Context context) {
		PathComponent pathComponent = (PathComponent) getApplicationContext().getBean(pathComponentName);
		Class<? extends PathComponent> clazz = pathComponent.getClass();
		pathComponent.setContext(context);
		return (PathComponent) Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				clazz.getInterfaces(), 
				new ExceptionThrowingInvocationHandler<PathNotFoundException>(
						pathComponent, PathNotFoundException.class));
	}

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

}
