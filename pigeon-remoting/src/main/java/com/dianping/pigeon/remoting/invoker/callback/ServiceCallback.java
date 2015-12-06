/**
 * 
 */
package com.dianping.pigeon.remoting.invoker.callback;


/**    
 * <p>    
 * Title: DPSFCallback.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-22 上午12:12:51   
 */
public interface ServiceCallback {
	
	/**
	 * 正常结果返回
	 * @param result
	 */
	public void onSuccess(Object result);
	
	public void onFailure(Throwable exception);

}