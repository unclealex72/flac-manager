package uk.co.unclealex.music.web.interceptor;

import com.opensymphony.xwork2.ActionInvocation;

public interface BackgroundProcessDelegate {

	public void beforeInvocation(ActionInvocation invocation, Object action) throws Exception;
	public void afterInvocation(ActionInvocation invocation, Object action) throws Exception;
}
