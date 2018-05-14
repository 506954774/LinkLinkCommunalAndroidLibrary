package com.qdong.communal.library.module.VersionManager;

import java.io.Serializable;

/**
 * 升级实体
 *
 * @author LHD
 *
 */
public class UpgradeEntity implements Serializable {
	private static final long serialVersionUID = 0x001;

	private long deployTime; // 更新时间
	private String description; // 版本信息
	private int platform;// 版本
	private String uploadUrl;// 请求URL
	private int versionId;// 版本ID
	private String versionLevel;// 版本等级
	private String versionName;// 版本名称
	private int versionSize;// 版本大小

	/**
	 * 是否强制升级 0-非强制 1-强制
	 */
	private int isForceUpgrade;
	/**
	 * 强制升级的最后时间
	 */
	private long forceUpgradeTime;
	/**
	 * 是否到了升级的最后时间 0-没到 1-到了
	 */
	private int isEnd;

	public long getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(long deployTime) {
		this.deployTime = deployTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	public String getVersionLevel() {
		return versionLevel;
	}

	public void setVersionLevel(String versionLevel) {
		this.versionLevel = versionLevel;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionSize() {
		return versionSize;
	}

	public void setVersionSize(int versionSize) {
		this.versionSize = versionSize;
	}



	public int getIsForceUpgrade() {
		return isForceUpgrade;
	}

	public void setIsForceUpgrade(int isForceUpgrade) {
		this.isForceUpgrade = isForceUpgrade;
	}

	public long getForceUpgradeTime() {
		return forceUpgradeTime;
	}

	public void setForceUpgradeTime(long forceUpgradeTime) {
		this.forceUpgradeTime = forceUpgradeTime;
	}

	public int getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(int isEnd) {
		this.isEnd = isEnd;
	}

	@Override
	public String toString() {
		return "UpgradeEntity [deployTime=" + deployTime + ", description=" + description + ", platform=" + platform
				+ ", uploadUrl=" + uploadUrl + ", versionId=" + versionId + ", versionLevel=" + versionLevel
				+ ", versionName=" + versionName + ", versionSize=" + versionSize + ", isForceUpgrade=" + isForceUpgrade
				+ ", forceUpgradeTime=" + forceUpgradeTime + ", isEnd=" + isEnd + "]";
	}



}
