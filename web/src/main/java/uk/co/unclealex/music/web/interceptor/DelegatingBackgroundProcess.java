package uk.co.unclealex.music.web.interceptor;

import org.apache.struts2.interceptor.BackgroundProcess;

import com.opensymphony.xwork2.ActionInvocation;

public class DelegatingBackgroundProcess extends BackgroundProcess {

	private BackgroundProcessDelegate i_backgroundProcessDelegate;

	public DelegatingBackgroundProcess(String threadName,
			ActionInvocation invocation, int threadPriority,
			BackgroundProcessDelegate backgroundProcessDelegate) {
		super(threadName, invocation, threadPriority);
		i_backgroundProcessDelegate = backgroundProcessDelegate;
	}

	@Override
	protected void beforeInvocation() throws Exception {
		while (i_backgroundProcessDelegate == null) {
			// Wait until the delegate has been populated.
		}
		i_backgroundProcessDelegate.beforeInvocation(getInvocation(), getAction());
	}
	
	@Override
	protected void afterInvocation() throws Exception {
		i_backgroundProcessDelegate.afterInvocation(getInvocation(), getAction());
	}
	
}
