/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.common.domain;

import java.io.Serializable;
import java.util.Map;

public interface InvocationResponse extends InvocationSerializable {

	void setMessageType(int messageType);

	int getMessageType();
	
	Object getResponse();

	void setResponse(Object response);

	Map<String, Serializable> getResponseValues();

	void setResponseValues(Map<String, Serializable> responseValues);

	long getCreateMillisTime();

	void setCreateMillisTime(long createMillisTime);
}
