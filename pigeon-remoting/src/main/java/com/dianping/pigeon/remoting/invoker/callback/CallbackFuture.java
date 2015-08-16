/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.invoker.callback;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.monitor.Monitor;
import com.dianping.pigeon.monitor.MonitorLoader;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.RpcException;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.exception.RequestTimeoutException;
import com.dianping.pigeon.remoting.invoker.route.statistics.ServiceStatisticsHolder;
import com.dianping.pigeon.remoting.invoker.util.InvokerUtils;
import com.dianping.pigeon.util.ContextUtils;

public class CallbackFuture implements Callback, CallFuture {

	private static final Logger logger = LoggerLoader.getLogger(CallbackFuture.class);

	private static final Monitor monitor = MonitorLoader.getMonitor();

	protected InvocationResponse response;
	private CallFuture future;
	private boolean done = false;
	private boolean concelled = false;
	private boolean success = false;
	protected InvocationRequest request;
	protected Client client;

	public void run() {
		synchronized (this) {
			this.done = true;
			if (this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
				this.success = true;
			}
			this.notifyAll();
		}
	}

	public void callback(InvocationResponse response) {
		this.response = response;
	}

	public InvocationResponse getResponse() throws InterruptedException {
		return getResponse(Long.MAX_VALUE);
	}

	public InvocationResponse getResponse(long timeoutMillis) throws InterruptedException {
		synchronized (this) {
			long start = request.getCreateMillisTime();
			while (!this.done) {
				long timeoutMillis_ = timeoutMillis - (System.currentTimeMillis() - start);
				if (timeoutMillis_ <= 0) {
					StringBuilder sb = new StringBuilder();
					sb.append("request timeout, current time:").append(System.currentTimeMillis())
							.append("\r\nrequest:").append(request).append("\r\nhost:").append(client.getHost())
							.append(":").append(client.getPort());
					ServiceStatisticsHolder.flowOut(request, client.getAddress());
					RequestTimeoutException e = new RequestTimeoutException(sb.toString());
					throw e;
				} else {
					this.wait(timeoutMillis_);
				}
			}
			processContext();

			if (response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
				RpcException cause = InvokerUtils.toRpcException(response);
				StringBuilder sb = new StringBuilder();
				sb.append("remote call exception\r\nrequest:").append(request).append("\r\nhost:")
						.append(client.getHost()).append(":").append(client.getPort()).append("\r\nresponse:")
						.append(response);
				logger.error(sb.toString(), cause);
				monitor.logError(sb.toString(), cause);
			} else if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
				if (Constants.LOG_INVOKER_APP_EXCEPTION) {
					Throwable cause = InvokerUtils.toApplicationException(response);
					StringBuilder sb = new StringBuilder();
					sb.append("remote service exception\r\nrequest:").append(request).append("\r\nhost:")
							.append(client.getHost()).append(":").append(client.getPort()).append("\r\nresponse:")
							.append(response);
					logger.error(sb.toString(), cause);
					monitor.logError(sb.toString(), cause);
				}
			}
			return this.response;
		}
	}

	protected void processContext() {
		setResponseContext(response);
	}

	protected void setResponseContext(InvocationResponse response) {
		if (response != null) {
			Map<String, Serializable> responseValues = response.getResponseValues();
			if (responseValues != null) {
				ContextUtils.setResponseContext(responseValues);
			}
		}
	}

	public InvocationResponse getResponse(long timeout, TimeUnit unit) throws InterruptedException {
		return getResponse(unit.toMillis(timeout));
	}

	public boolean cancel() {
		if (this.future != null) {
			synchronized (this) {
				this.concelled = this.future.cancel();
				this.notifyAll();
			}
		}
		return this.concelled;
	}

	public boolean isCancelled() {
		return this.concelled;
	}

	public boolean isDone() {
		return this.done;
	}

	public void setRequest(InvocationRequest request) {
		this.request = request;
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Client getClient() {
		return this.client;
	}

	@Override
	public void dispose() {

	}

}
