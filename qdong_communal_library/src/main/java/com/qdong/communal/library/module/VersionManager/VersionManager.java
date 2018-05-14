package com.qdong.communal.library.module.VersionManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;

import com.qdong.communal.library.R;
import com.qdong.communal.library.module.network.QDongNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.util.JsonUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.util.ToastUtil;

import java.io.File;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 版本管理
 *
 * @Time 2015-12-17
 * @author LHD
 *
 */
public class VersionManager {

	//private static final String HOST_URL ="http://tryingdo.cn/" ;
	private static final String HOST_URL = Constants.SERVER_URL;

	private UpgradeEntity upgradeEntity;
	private Activity activity;
	private FileLoader loader;
	private boolean isLoading;
	private NotificationCompat.Builder builder;
	private long lastTime = 0;
	private String className;

	private boolean mIsShowingDialog;//是否正展示着升级的dialog
	private Subscription mSubscription;
	private VersionCheckerCallBack mVersionCheckerCallBack;
	private boolean isAuto;

	public VersionManager(Activity activity,VersionCheckerCallBack mVersionCheckerCallBack) {
		this.activity = activity;
		this.mVersionCheckerCallBack = mVersionCheckerCallBack;
	}





	/** Bugfix-0412-20160413-LHD-START */
	// 修改界面标记设置方式
	public void setTag(String name) {
		this.className = name;
	}
	/** Bugfix-0412-20160413-LHD-END */

	/**
	 * 请求服务器数据
	 */
	public void checkVersion(boolean isAuto) {

		this.isAuto=isAuto;
		if(mVersionCheckerCallBack !=null){
			if(!isAuto){
				mVersionCheckerCallBack.showLoadingView();
			}
		}

		Observer<QDongNetInfo> ob =new Observer<QDongNetInfo>() {
			@Override
			public void onCompleted() {
				try {
					mSubscription.unsubscribe();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable e) {
				if(mVersionCheckerCallBack !=null){
						mVersionCheckerCallBack.dismissLoadingView();
					mVersionCheckerCallBack.onError();
				}
			}

			@Override
			public void onNext(QDongNetInfo qDongNetInfo) {
				mVersionCheckerCallBack.dismissLoadingView();
				if(qDongNetInfo.isSuccess()&&qDongNetInfo.getResult()!=null){
					upgradeEntity = JsonUtil.fromJson(qDongNetInfo.getResult(),UpgradeEntity.class);
					upgradeVersionPrompt(upgradeEntity);
				}
				else {
					if(mVersionCheckerCallBack !=null){
						mVersionCheckerCallBack.onError();
					}
				}
			}
		};

		mSubscription=RetrofitAPIManager.provideClientApi(activity,HOST_URL).findLatestVersion(getVersionName(activity))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(ob);




		//PreferenceUtil.put(activity, "check_Version", System.currentTimeMillis());
		SharedPreferencesUtil.getInstance(activity).putLong("check_Version", System.currentTimeMillis());
	}

	/**
	 * 自动检测版本
	 */
	public void autoCheck() {
		//long time = PreferenceUtil.getLong(activity, "check_Version");
		long time = SharedPreferencesUtil.getInstance(activity).getLong("check_Version", 0);

		long now = System.currentTimeMillis();
		//long limit=86400000;
		long limit=0;
		if (now - time > limit) { //每次进来都检测
			checkVersion(true);
		}
	}

	/**
	 * 获取应用版本码
	 *
	 * @return 版本号
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 版本提示
	 */
	private void upgradeVersionPrompt(final UpgradeEntity entity) {
		if (entity != null) {

			/**升级弹出框正在展示**/
			setmIsShowingDialog(true);

			final CustomPromptDialog dialog = new CustomPromptDialog(activity);
			/** Bugfix-0412-20160413-LHD-START */
			// 去除Dialog时，版本显示Vv中的V


			/***是否强制升级**/
			String IsForceUpgrade="";
			if(entity.getIsForceUpgrade()==1){

				if(entity.getIsEnd()==1){//时间到
					IsForceUpgrade="\n\n"+activity.getString(R.string.check_version_hint1);
				}
				else{
					IsForceUpgrade="\n\n"+activity.getString(R.string.check_version_time_hint1)
							+ DateFormatUtil.getDate(DateFormatUtil.YMD, entity.getForceUpgradeTime())
							+activity.getString(R.string.check_version_time_hint2);
				}

				dialog.setPromptText(activity.getString(R.string.check_version_force_update), entity.getDescription()+IsForceUpgrade, activity.getString(R.string.commit));
			}
			else{
				dialog.setPromptText(activity.getString(R.string.check_version_update) + entity.getVersionName(), entity.getDescription()+IsForceUpgrade, activity.getString(R.string.commit));
			}



			/** Bugfix-0412-20160413-LHD-END */
			dialog.getConfirm().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					/**升级弹出框消除**/
					ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_updating));
					setmIsShowingDialog(false);
					dialog.dialogDismiss();
					if (isLoading) {
						//Tools.showShort(activity, "已在下载中");
						ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_updating));
						return;
					}
					download(entity);
				}
			});

			/***是否强制升级,且时间到了**/
			if(entity.getIsForceUpgrade()==1&&entity.getIsEnd()==1){
				dialog.getCancel().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						/**升级弹出框消除**/
						setmIsShowingDialog(false);
						dialog.dialogDismiss();

						//Tools.showShort(activity, "抱歉,当前版本过低,无法使用!");

						/**把事件给调用者**/
						if(mVersionCheckerCallBack !=null){
							mVersionCheckerCallBack.forceCloseApp();
						}
					}
				});
			}



		} else {
			//Tools.showShort(activity, "已是最新版本");
			/*if(!isAuto){
				ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_is_the_very_latest));
			}*/

			if(mVersionCheckerCallBack!=null){
				mVersionCheckerCallBack.noNewVersion();
			}

		}
	}

	/**
	 * 下载文件
	 *
	 * @param entity
	 */
	private void download(UpgradeEntity entity) {
		isLoading = true;
		// 调取异步任务下载文件
		loader = new FileLoader(activity) {
			protected void onPostExecute(String result) {
				/** Bugfix-0412-20160412-yyh-START */
				// 增加文件写入错误版本处理
				if (result.equals("error") || "fail".equals(result)) {
					//loadError("下载失败");
					loadError(activity.getString(R.string.check_version_download_failed));
				} else {
					loadEnd(result);
				}
				/** Bugfix-0412-20160412-yyh-END */
				isLoading = false;
				exit();
				loader = null;
			}

			protected void onProgressUpdate(String... values) {
				updateProgress(Integer.valueOf(values[0]), values[1]);
			}
		};
		loader.execute(entity);// 执行下载
	}

	/**
	 * 初始化通知对象
	 */
	private void initNoti() {
		String title = activity.getString(R.string.app_name)+"_" + upgradeEntity.getVersionName();
		builder = new NotificationCompat.Builder(activity).setSmallIcon(R.mipmap.icon_logo)
				// 设置状态栏的小标题
				.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_download))
				// 设置下拉列表里的图标
				.setWhen(System.currentTimeMillis()).setTicker(activity.getString(R.string.app_name)+"下载")// 设置状态栏的显示的信息
				.setContentTitle(title); // 设置下拉列表里的标题
	}

	/**
	 * 更新进度
	 *
	 * @param position
	 */
	private void updateProgress(int position, String text) {
		long nowTime = System.currentTimeMillis();
		if (nowTime - lastTime < 500)
			return;
		lastTime = nowTime;
		initNoti();
		// 1 得到通知管理器
		NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		builder.setProgress(100, position, false).setAutoCancel(false).setContentText(text);// 设置可以清除;
		Notification notification = builder.build();// API 16添加创建notification的方法
		notification.flags = Notification.FLAG_NO_CLEAR;
		manager.notify(1111, notification);
	}

	/**
	 * 取消通知对象,并安装程序
	 */
	private void loadEnd(String fileName) {
		NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(1111);
		Intent app = new Intent(Intent.ACTION_VIEW);
		app.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		app.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		activity.startActivity(app);
	}

	/**
	 * 下载错误
	 */
	private void loadError(String text) {
		initNoti();
		Intent intent = new Intent(activity, activity.getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(activity, 100, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);// FLAG_UPDATE_CURRENT显示最新的
		NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		builder.setContentText(text).setContentIntent(contentIntent).setAutoCancel(true);
		Notification notification = builder.build();// API 16添加创建notification的方法
		manager.notify(1111, notification);
	}

	public void exit() {
		if (loader != null) {
			loader.cancel(true);
			loader = null;
		}
		activity = null;
	}

	public boolean ismIsShowingDialog() {
		return mIsShowingDialog;
	}

	public void setmIsShowingDialog(boolean mIsShowingDialog) {
		this.mIsShowingDialog = mIsShowingDialog;
	}




}
