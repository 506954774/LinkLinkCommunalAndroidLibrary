package com.qdong.communal.library.module.VersionManager;

import android.content.Context;
import android.os.AsyncTask;


import com.qdong.communal.library.util.DecimalUtil;
import com.qdong.communal.library.util.OkHttpUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * 升级文件下载异步对象
 *
 * @Time 2015-12-18
 *
 * @author LHD
 *
 */
public class FileLoader extends AsyncTask<UpgradeEntity, String, String> {

	private Context context;
	private int progress;

	public FileLoader(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(UpgradeEntity... params) {
		try {
			UpgradeEntity upgradeEntity = params[0];
			/** Bugfix-0412-20160412-yyh-START */
			// 下载URL变更，修改文件名称获取方式
			String url = upgradeEntity.getUploadUrl();
			String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			return writeFile(OkHttpUtil.downFile(url), fileName, upgradeEntity.getVersionSize());
			/** Bugfix-0412-20160412-yyh-END */
		} catch (Exception e) {

		}
		return "error"; // 失败返回
	}


	private String writeFile(ResponseBody body, String filaName, int fileSize) throws Exception {
		if (body == null)
			return "fail";
		// path表示你所创建文件的路径
		FileUtils fileUtils = new FileUtils(context);
		File file = fileUtils.creatSDFile(filaName);
		if (save(body.byteStream(), file, body.contentLength())) {
			return file.getAbsoluteFile().getAbsolutePath();
		}
		return "fail";
	}

	/**
	 * 从指定资源输入流中往文件输出流中交换数据
	 *
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public boolean readData(InputStream is, OutputStream os, long fileSize) throws IOException {
		boolean isLoad = false;
		// fileSize = 28835840;
		if (is != null && os != null) {
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int len = 0;
			long downloadFileSize = 0;
			byte[] data = new byte[1024];
			while ((len = bis.read(data)) != -1) {
				try {
					downloadFileSize += len;
					bos.write(data, 0, len);
					if (len % 1024 == 0) {
						// 计算下载进度比例
						double loadSize = downloadFileSize * 1.0;
						double count = loadSize / fileSize;
						int index = (int) (count * 100);
						if (index - progress >= 1) {
							progress = index;
							onProgressUpdate(new String[] { String.valueOf(progress), //
									getPrompt(downloadFileSize, fileSize) });
						}
					}
					if (fileSize <= downloadFileSize) {
						isLoad = true;
					}
					bos.flush();
				} catch (Exception e) {

				}
			}

			// 关闭流操作
			is.close();
			os.close();
			bis.close();
			bos.close();
		}
		return isLoad;
	}

	/**
	 * 根据资源输入流创建本地文件并存储到文件中
	 *
	 * @param is
	 *            输入流
	 * @param file
	 *            文件对象
	 * @param
	 * @throws IOException
	 */
	public boolean save(InputStream is, File file, long fileSize) throws IOException {
		if (is != null && file != null) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream os = new FileOutputStream(file);
			return readData(is, os, fileSize);
		}
		return false;
	}

	/**
	 * 获取下载提示字符串
	 *
	 * @param downSize
	 *            已下载数据
	 * @param fileSize
	 *            文件大小
	 * @return
	 */
	public String getPrompt(long downSize, long fileSize) {
		String down = DecimalUtil.getFileSize(downSize, "#0.00");
		String file = DecimalUtil.getFileSize(fileSize, "#0.00");
		return "已下载：" + down + "M/" + file + "M";
	}

	public void exit() {
		cancel(true);
		context = null;
	}
}
