package com.dianping.pigeon.demo;

import com.dianping.pigeon.remoting.invoker.callback.ServiceCallback;


public class EchoServiceCallback implements ServiceCallback {


	@Override
	public void onSuccess(Object result) {
		System.out.println("callback:" + result);
	}

	@Override
	public void onFailure(Throwable exception) {
		exception.printStackTrace();
	}

}
