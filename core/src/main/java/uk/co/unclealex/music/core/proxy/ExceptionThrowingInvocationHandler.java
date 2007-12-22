package uk.co.unclealex.music.core.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This proxy can be used to proxy an object where, if a method allows a certain
 * caught exception to be thrown, it will be used to wrap any runtime exceptions
 * @author alex
 *
 */
public class ExceptionThrowingInvocationHandler<E extends Exception> implements InvocationHandler {

	private Object i_object;
	private Class<E> i_exceptionClass;

	public ExceptionThrowingInvocationHandler(Object object, Class<E> exceptionClass) {
		super();
		i_object = object;
		i_exceptionClass = exceptionClass;
		// see if the exception can be created
		createException(new RuntimeException());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(getObject(), args);
		}
		catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof RuntimeException) {
				throw createException(t);
			}
			else {
				throw t;
			}
		}
	}

	protected E createException(Throwable t) throws IllegalArgumentException {
		try {
			Constructor<E> constructor = getExceptionClass().getConstructor(
					Throwable.class);
			return constructor.newInstance(t);
		}
		catch (Throwable th) {
			throw new IllegalArgumentException(th);
		}
	}
	
	public Object getObject() {
		return i_object;
	}

	protected void setObject(Object object) {
		i_object = object;
	}

	public Class<E> getExceptionClass() {
		return i_exceptionClass;
	}

	protected void setExceptionClass(Class<E> exceptionClass) {
		i_exceptionClass = exceptionClass;
	}

}
