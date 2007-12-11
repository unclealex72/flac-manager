package uk.co.unclealex.music.web.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

public class PrepareAfterInterceptor extends MethodFilterInterceptor implements PreResultListener {

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		invocation.addPreResultListener(this);
		return invocation.invoke();
	}

	@Override
	public void beforeResult(ActionInvocation invocation, String resultCode) {
		Object action = invocation.getAction();
		if (action instanceof AfterPreparable) {
			((AfterPreparable) action).prepareAfter();
		}
	}
}
