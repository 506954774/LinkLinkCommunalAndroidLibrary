package com.qdong.communal.library.module.VersionManager;



import java.io.Serializable;
import java.util.Arrays;

import okhttp3.Call;

/**
 * 任务对象
 *
 * @version 1.4
 * @Time 2016-02-04
 * @author LHD
 *
 */
public class TaskEntity implements Serializable {
	private static final long serialVersionUID = 0x001;

	private String url; // 地址
	private String tag; // 标记
	private String request; // 请求
	private String result; // 返回
	private String type; // 类型
	private String[] rawPaths;
	private String errorContent; // 错误内容
	private int httpType;// 请求方式
	private Call call;

	public TaskEntity() {
		super();
	}

	public TaskEntity(String url) {
		this.url = url;
	}

	public TaskEntity(String url, String request) {
		this.url = url;
		this.request = request;
	}

	public TaskEntity(String url, String tag, String request, String type) {
		super();
		this.url = url;
		this.tag = tag;
		this.request = request;
		this.type = type;
	}

	public TaskEntity(String tag, String type, String url, String[] rawPaths) {
		this.tag = tag;
		this.type = type;
		this.url = url;
		this.rawPaths = rawPaths;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getRawPaths() {
		return rawPaths;
	}

	public void setRawPaths(String[] rawPaths) {
		this.rawPaths = rawPaths;
	}

	public int getHttpType() {
		return httpType;
	}

	/**
	 * 0是post 1是get 2是图文
	 *
	 * @param httpType
	 */
	public void setHttpType(int httpType) {
		this.httpType = httpType;
	}

	public String getErrorContent() {
		return errorContent;
	}

	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}

	public Call getCall() {
		return call;
	}

	public void setCall(Call call) {
		this.call = call;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this == obj || !(obj instanceof TaskEntity))
			return false;
		TaskEntity entity = (TaskEntity) obj;
		if (type == null && tag == null)
			return false;
		if (tag == null && type != null)
			return type.equals(entity.getType());
		if (type != null && tag != null)
			return tag.equals(entity.getTag()) && type.equals(entity.getType());
		return false;
	}

	@Override
	public String toString() {
		return "TaskEntity [url=" + url + ", tag=" + tag + ", request=" + request + ", result=" + result + ", type="
				+ type + ", rawPaths=" + Arrays.toString(rawPaths) + "]";
	}

}
