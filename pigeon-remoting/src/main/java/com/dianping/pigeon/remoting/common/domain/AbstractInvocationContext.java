/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.common.domain;

public abstract class AbstractInvocationContext implements InvocationContext {

	protected InvocationRequest request;
	protected InvocationResponse response;

	public AbstractInvocationContext(InvocationRequest request) {
		this.request = request;
	}

	@Override
	public InvocationRequest getRequest() {
		return request;
	}

	public void setRequest(InvocationRequest request) {
		this.request = request;
	}

	@Override
	public InvocationResponse getResponse() {
		return response;
	}

	public void setResponse(InvocationResponse response) {
		this.response = response;
	}

}
