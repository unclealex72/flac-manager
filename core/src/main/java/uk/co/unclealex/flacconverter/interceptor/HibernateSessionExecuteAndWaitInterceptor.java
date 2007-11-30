package uk.co.unclealex.flacconverter.interceptor;

import org.apache.struts2.interceptor.BackgroundProcess;
import org.apache.struts2.interceptor.ExecuteAndWaitInterceptor;

import com.opensymphony.xwork2.ActionInvocation;

public class HibernateSessionExecuteAndWaitInterceptor extends
		ExecuteAndWaitInterceptor {

	@Override
	protected BackgroundProcess getNewBackgroundProcess(
			String name, ActionInvocation actionInvocation, int threadPriority) {
		return new HibernateSessionBackgroundProcess(name, actionInvocation, threadPriority);
	}
}
