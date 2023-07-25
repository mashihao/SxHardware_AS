package com.sx.service;


import com.sx.sxhardware.DifferentDislay;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Diff_Service  extends Service {


	private Context mContext = null;
	private MyReceiver myReceiver = null;
	public static final String ACTION_TOAST = "com.sinxin.diff";//
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = Diff_Service.this;
		showView();
		//自定义广播接收器
		myReceiver = new MyReceiver();
		IntentFilter my_filter = new IntentFilter();
		my_filter.addAction(ACTION_TOAST);//
		registerReceiver(myReceiver, my_filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(myReceiver);//注销广播

		//重启
		Intent it = new Intent();// 后台服务--
		it.setClass(getApplicationContext(), Diff_Service.class);
		startService(it);


		super.onDestroy();
	}
	/**
	 * 自定义广播接收器
	 * @author lizhou
	 *
	 */
	class MyReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();


			if(action.equals(ACTION_TOAST)){
				Toast.makeText(mContext, intent.getStringExtra("MSG"),Toast.LENGTH_SHORT).show();
			}


		}
	}

	@SuppressLint("NewApi")
	private void showView(){
		DisplayManager mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
		Display[] displays = mDisplayManager.getDisplays();
		if(displays.length>=2){

			//[副屏]获取屏幕尺寸
			Point point = new  Point() ;
			displays[1].getRealSize(point);
			int screenWidth_diff = point.x;//[副屏] 宽度
			int screenHeight_diff = point.y;//[副屏] 高度


			DifferentDislay mPresentation = new DifferentDislay(mContext,displays[1]);
			mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//
			mPresentation.show();//显示副屏
		}
	}

}
