/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.provider.process.filter;

import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationFilter;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.provider.domain.ProviderContext;
import com.dianping.pigeon.util.ContextUtils;

public class ContextTransferProcessFilter implements ServiceInvocationFilter<ProviderContext> {

	private static final Logger logger = LoggerLoader.getLogger(ContextTransferProcessFilter.class);

	@Override
	public InvocationResponse invoke(ServiceInvocationHandler handler, ProviderContext invocationContext)
			throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug("invoke the ContextTransferProcessFilter, invocationContext:" + invocationContext);
		}
		InvocationRequest request = invocationContext.getRequest();
		transferContextValueToProcessor(invocationContext, request);
		InvocationResponse response = null;
		try {
			response = handler.handle(invocationContext);
			return response;
		} finally {
			if (response != null) {
				try {
					transferContextValueToResponse(invocationContext, response);
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	private void transferContextValueToProcessor(final ProviderContext processContext, final InvocationRequest request) {
		ContextUtils.setGlobalContext(request.getGlobalValues());
		ContextUtils.putLocalContext("CLIENT_IP", processContext.getChannel().getRemoteAddress());
		ContextUtils.putLocalContext("CLIENT_APP", request.getApp());
		Map<String, Serializable> requestValues = request.getRequestValues();
		if (requestValues != null) {
			for (String key : requestValues.keySet()) {
				ContextUtils.putLocalContext(key, requestValues.get(key));
			}
		}
	}

	private void transferContextValueToResponse(final ProviderContext processContext, final InvocationResponse response) {
		response.setResponseValues(ContextUtils.getResponseContext());
	}

}
