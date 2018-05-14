package com.qdong.communal.library.module.VersionManager;

import android.content.Context;
import android.os.Environment;

import com.qdong.communal.library.BuildConfig;
import com.qdong.communal.library.util.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class FileUtils {
	private static final  String ROOT_FILE_PATH="/"+ BuildConfig.FILE_ROOT_NAME+"/";
	private String SDPATH;
	private int FILESIZE = 1024;

	/**
	 * 获取跟路径
	 *
	 * @return
	 */
	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
	}

	public FileUtils(Context context) {
		/** Bugfix-0412-20160413-LHD-START */
		// 修改跟目录名称和路径
		SDPATH = Environment.getExternalStorageDirectory() + ROOT_FILE_PATH;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			SDPATH = Environment.getExternalStorageDirectory() + ROOT_FILE_PATH;
		} else {
			SDPATH = context.getFilesDir().toString() + ROOT_FILE_PATH;
		}
		/** Bugfix-0412-20160413-LHD-END */
		LogUtil.e("FileUtils", "创建根目录：" + SDPATH);
	}

	/**
	 * 在SD卡上创建文件
	 */
	public File creatSDFile(String fileName) throws IOException {
		// 先判断文件夹是否存在，如果不存在，创建它
		File dir = new File(SDPATH);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(SDPATH + fileName);
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			// System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 *
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 获取文件最后的修改时间
	 *
	 * @param fileName
	 *            文件名称
	 * @return
	 */
	public long file_time(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.lastModified();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public void write2SDFromInput(String path, byte[] buffer) {
		// LogUtil.showString("client", "FileUtils-input", "写入文件：" + path);
		OutputStream output = null;
		File file = null;
		try {
			file = new File(path);
			output = new FileOutputStream(path);
			if (!file.getParentFile().exists()) {
				file.exists();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			int len = buffer.length;
			int start = 0;
			int write = 1024;
			while (len == start) {
				output.write(buffer, start, write);
				// LogUtil.showString("client", "FileUtils-input", "写入文件：" +
				// start);
				output.flush();
				start += write;
				if (len - start < 1024) {
					write = len - start;
				}
			}
		} catch (Exception e) {
			// LogUtil.showString("client", "FileUtils-input",
			// "文件写入异常：" + e.getStackTrace());
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除所有文件
	 *
	 * @param fileStr
	 */
	public static void deleteFile(String fileStr) {
		File file = new File(fileStr);
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			}
			file.delete();
		}
	}

	/**
	 * 删除单个文件
	 *
	 * @param path
	 */
	public void delFile(String path) {
		String str = SDPATH + path;
		File file = new File(str);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 复制文件到文件
	 *
	 * @param fromFile
	 *            需要复制的文件
	 * @param toFile
	 *            目标文件
	 * @return
	 */
	public static boolean CopyFileToFile(String fromFile, String toFile) {
		InputStream fosfrom = null;
		OutputStream fosto = null;
		try {
			fosfrom = new FileInputStream(fromFile);
			fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			bt = null;
			fosfrom.close();
			fosto.close();
			return true;

		} catch (Exception ex) {
			return false;
		} finally {
			fosfrom = null;
			fosto = null;
		}
	}

	/**
	 * 复制文件到sd卡中
	 *
	 * @param romfile
	 * @return
	 */
	public String CopyFileRomToSD(String romfile) {
		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return null;
		}

		String sd = Environment.getExternalStorageDirectory() + "/ibabyzone/" + romfile;

		if (CopyFileToFile(romfile, sd)) {
			return sd;
		}

		return null;
	}

	/**
	 * @method name:writerLine
	 * @des:逐行写入文件
	 * @param :[path,
	 *            append, value, charsetName]
	 * @return type:void
	 * @date 创建时间:2016/6/4
	 * @author Chuck
	 **/
	public static void writerLine(String path, boolean append, String value, String charsetName) throws IOException {

		if (new File(path).exists()) {

			OutputStreamWriter osw = null;
			BufferedWriter bw = null;
			try {
				osw = new OutputStreamWriter(new FileOutputStream(path, append), charsetName);
				bw = new BufferedWriter(osw);
				bw.write(new Date().toLocaleString() + value);
				bw.newLine();
				bw.flush();
			} catch (UnsupportedEncodingException e) {
				System.out.println("没有指定的字符集");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("没有指定的文件");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("IOException");
			} finally {
				bw.close();
				osw.close();
			}
		}

	}

	/**
	 * @method name:getAssetsCacheFile
	 * @des:把assets里面的文件缓存起来
	 * @param :[context,
	 *            fileName:assset 里面的文件名]
	 * @return type:java.lang.String cache里的文件的路径
	 * @date 创建时间:2016/6/4
	 * @author Chuck
	 **/
	public static String getAssetsCacheFile(Context context, String fileName) {

		File cacheFile = new File(context.getCacheDir(), fileName);
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			try {
				FileOutputStream outputStream = new FileOutputStream(cacheFile);
				try {
					byte[] buf = new byte[1024];
					int len;
					while ((len = inputStream.read(buf)) > 0) {
						outputStream.write(buf, 0, len);
					}
				} finally {
					outputStream.close();
				}
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cacheFile.getAbsolutePath();
	}

	/**
	 * @method name:getSDPath
	 * @des:获取sd卡路径
	 * @param :[]
	 * @return type:java.lang.String
	 * @date 创建时间:2016/6/4
	 * @author Chuck
	 **/
	public static String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		if (sdCardExist)
			return Environment.getExternalStorageDirectory().toString();// 获取跟目录
		return null;
	}
}
