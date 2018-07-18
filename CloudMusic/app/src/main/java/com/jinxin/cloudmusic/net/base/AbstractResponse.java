package com.jinxin.cloudmusic.net.base;

/**
 * @author Huang
 * @date 2015-12-16 上午11:04:17
 * @version V1.0
 */
public abstract class AbstractResponse<T> implements IResponse {

	private ICallback<T> callback = null;
	
	public AbstractResponse(ICallback<T> callback) {
		this.callback = callback;
	}
	
	protected ICallback<T> getCallback() {
		return callback;
	}
}
