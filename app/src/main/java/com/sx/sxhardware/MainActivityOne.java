package com.sx.sxhardware;

import com.sinxin.sxhardware.R;
import com.sx.manager.SerialPortManager;
import com.wzx.WeightAPI.WeightDLL;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityOne extends Activity {


	private TextView tvWeight;
	private Button btnWeight,btnCloseWeight;

	public static String wei; // 重量值
	public static String ad; // 重量板原始数据

	private WeightDLL weightdll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_one);

		initView();

//		try {
//			// 实例化重量版
//			weightdll = new WeightDLL();
//			openWeightPort();
//		} catch (Exception e) {
//
//		}


		new ReadThreadWeight().start();
	}



	private void initView() {
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		btnWeight = (Button) findViewById(R.id.btnWeight);
		btnCloseWeight = (Button) findViewById(R.id.btnCloseWeight);

		btnWeight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 实例化重量版
				weightdll = new WeightDLL();
				openWeightPort();

			}
		});

		btnCloseWeight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 重量板so
				if (weightdll != null) {
					weightdll.WeightClose();
				}

			}
		});
	}

	private class ReadThreadWeight extends Thread {
		@Override
		public void run() {
			while (true) {
				handler.sendEmptyMessage(0);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				// 整型
				case 0:
					String weightValue = "";
//				Log.e("swang", "========weightValue==========" + wei);
					if (!TextUtils.isEmpty(wei)) {
						weightValue = wei;
					} else {
						weightValue = "0";
					}

					tvWeight.setText(weightValue);
//				originalWeight.setText(MainActivity.ad);
					break;
			}
		}
	};


	private void openWeightPort() {
		String w = "3";
		String tty = "/dev/ttyS";
		String weightPort = "115200";
		String WEIGHTCOM = "3";
		if (WEIGHTCOM != null && !"".equals(WEIGHTCOM)) {
			w = WEIGHTCOM.substring(WEIGHTCOM.length() - 1);
		}
		w = SerialPortManager.rtnSerialStr(w);

		String ttyStr = SerialPortManager.rtnTTy(tty, w);
		Log.e("swang", "ttyStr + w=====" + ttyStr + w);
		Log.e("swang", "weightPort=====" + weightPort);
		weightdll.WeightOpen(ttyStr + w, Integer.parseInt(weightPort), 0, 30, 0,0);
	}

}
