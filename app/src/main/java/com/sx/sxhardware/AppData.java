package com.sx.sxhardware;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AppData extends Application {

	public String WEIGHTCOM="3";//秤板串口号com0
	public String weightPort ="115200";//秤板波特率
	public String maxValue ="30";//最大量程
	public String ifDouWeight="0";//双精度 1为双精度

	/*public String PRINTCOM="3";
	public String PRINTTXTCOM="4";
	public String PRINTCARD="1";
	public int printPort =9600;//打印机波特率9600 115200
	 */

	public String PRINTCOM="1";
	public String PRINTTXTCOM="COM1";//原来是4,所以打印不出来
	public String PRINTCARD="2";
	public int printPort =115200;//打印机波特率9600 115200

	private List<Activity> mActivityList;

	@Override
	public void onCreate() {
		super.onCreate();
		mActivityList = new ArrayList<Activity>();
	}

	/**
	 * 添加单个Activity
	 */
	public void addActivity(Activity activity) {
		// 为了避免重复添加，需要判断当前集合是否满足不存在该Activity
		if (!mActivityList.contains(activity)) {
			mActivityList.add(activity); // 把当前Activity添加到集合中
		}
	}

	/**
	 * 销毁单个Activity
	 */
	public void removeActivity(Activity activity) {
		// 判断当前集合是否存在该Activity
		if (mActivityList.contains(activity)) {
			mActivityList.remove(activity); // 从集合中移除
			if (activity != null) {
				activity.finish(); // 销毁当前Activity
			}
		}
	}

	/**
	 * 销毁所有的Activity
	 */
	public void removeAllActivity() {
		// 通过循环，把集合中的所有Activity销毁
		for (Activity activity : mActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
		// 杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
