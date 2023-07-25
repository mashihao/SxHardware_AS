package com.sx.sxhardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;

import com.sinxin.TraceCardDLL;
import com.sinxin.sxhardware.R;
import com.sx.manager.SerialPortManager;
import com.sx.util.ESCCmd;
import com.wzx.WeightAPI.WeightDLL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.SerialPort;

public class MainActivity extends BaseActivity {

	public static String wei; // 重量值
	public static String ad; // 重量板原始数据

	public SerialPort mSerialPortPrint;
	public static OutputStream mOutputStreamPrint;
	public InputStream mInputStreamPrint;

	public SerialPort txtSerialPortPrint;
	public static OutputStream txtOutputStreamPrint;
	public InputStream txtInputStreamPrint;

	TraceCardDLL sdll;
	private WeightDLL weightdll;
	private ReadThreadWeight weightThread;
	String unit = "kg";
	TextView SetWeight;
	TextView originalWeight; // 重量原始数据
	TextView txtUnit;

	Button btnUnit;
	//Button btnSetWeight;
	Button btnPrintLabel;
	Button btnPrintTxt;
	Button btnReturnZero;
	int unitflag = 0;// 0公斤，1斤
	String tty = "/dev/ttyS";

	Button cd1, cd2, cd3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 刷卡
		sdll = new TraceCardDLL(MainActivity.this);

		SetWeight = (TextView) findViewById(R.id.SetWeight);
		originalWeight = (TextView) findViewById(R.id.originalWeight);
		txtUnit = (TextView) findViewById(R.id.txtUnit);
		btnUnit = (Button) findViewById(R.id.btnUnit);
		btnPrintLabel = (Button) findViewById(R.id.btnPrintLabel);
		btnPrintTxt = (Button) findViewById(R.id.btnPrintTxt);
		btnReturnZero = (Button) findViewById(R.id.btnReturnZero);

		cd1 = (Button) findViewById(R.id.cd1);
		cd2 = (Button) findViewById(R.id.cd2);
		cd3 = (Button) findViewById(R.id.cd3);

		btnUnit.setOnClickListener(new ButtonListener());
		btnPrintLabel.setOnClickListener(new ButtonListener());
		btnPrintTxt.setOnClickListener(new ButtonListener());
		btnReturnZero.setOnClickListener(new ButtonListener());
		cd1.setOnClickListener(new ButtonListener());
		cd2.setOnClickListener(new ButtonListener());
		cd3.setOnClickListener(new ButtonListener());

		try {
			// 实例化重量版
			weightdll = new WeightDLL();
			resetWeightPort();
			ReadThreadWeight threadWeight = new ReadThreadWeight();
			threadWeight.start();

			// 初始化刷卡器
			sdll.Init(2, tty + app.PRINTCARD, 9600);
		} catch (Exception e) {

		}

		// setScreen();
	}

	DifferentDislay diffPresentation = null;

	@SuppressLint("NewApi")
	public void setScreen() {
		DisplayManager mDisplayManager;// 屏幕管理类
		Display[] displays;// 屏幕数组
		mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
		displays = mDisplayManager.getDisplays();

		diffPresentation = new DifferentDislay(this, displays[1]);// displays[1]是副屏
		diffPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		diffPresentation.show();
	}

	public void initLabelPrint() {
		try {
			// 打开打印标签串口
			String p = "1";
			String PRINTCOM = sp.getString("PRINTCOM", app.PRINTCOM);
			if (PRINTCOM != null && !"".equals(PRINTCOM)) {
				p = PRINTCOM.substring(PRINTCOM.length() - 1);
			}
			p = SerialPortManager.rtnSerialStr(p);
			String ttyStr = SerialPortManager.rtnTTy(tty, p);
			Log.e("swang", "ttyStr = " + ttyStr);
			Log.e("swang", "app.PRINTCOM = " + app.PRINTCOM);
			mSerialPortPrint = getSerialPortPrint(ttyStr + app.PRINTCOM, app.printPort);
			mOutputStreamPrint = mSerialPortPrint.getOutputStream();
			mInputStreamPrint = mSerialPortPrint.getInputStream();
		} catch (Exception e) {

		}
	}

	public void initTxtPrint() {
		try {
			// 打开小票串口
			String pTxt = "4";
			String PRINTTXTCOM = sp.getString("PRINTTXTCOM", app.PRINTTXTCOM);
			if (PRINTTXTCOM != null && !"".equals(PRINTTXTCOM)) {
				pTxt = PRINTTXTCOM.substring(PRINTTXTCOM.length() - 1);
			}
			pTxt = SerialPortManager.rtnSerialStr(pTxt);
			Log.e("swang", "pTxt = " + pTxt);
			String txtTtyStr = SerialPortManager.rtnTTy(tty, pTxt);
//			Log.e("swang", "txtTtyStr = " + txtTtyStr + pTxt);
			Log.e("swang", "txtTtyStr = " + txtTtyStr );
			Log.e("swang", "app.printPort = " + app.printPort);
			txtSerialPortPrint = getSerialTXTPortPrint(txtTtyStr + pTxt, app.printPort);
			txtOutputStreamPrint = txtSerialPortPrint.getOutputStream();
			txtInputStreamPrint = txtSerialPortPrint.getInputStream();
		} catch (Exception e) {
		}
	}

	// 打印接口9600
	public SerialPort getSerialPortPrint(String file, int weightPort) throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPortPrint == null) {
			mSerialPortPrint = new SerialPort(new File(file), weightPort, 0);
		}
		return mSerialPortPrint;
	}

	// 打印接口9600
	public SerialPort getSerialTXTPortPrint(String file, int weightPort) throws SecurityException, IOException, InvalidParameterException {
		if (txtSerialPortPrint == null) {
			txtSerialPortPrint = new SerialPort(new File(file), weightPort, 0);
		}
		return txtSerialPortPrint;
	}

	class ButtonListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btnUnit:
					// 切换单位
					ChangeUnit();
					break;
				case R.id.btnPrintLabel:

					initLabelPrint();

					try {
						String print = print22Code("测试机构", "菜心", "2018-05-23", "SS0032018040300005", "1000", "1", "1.25");
						mOutputStreamPrint.write(print.getBytes("GB2312"));
					} catch (Exception e) {
					}
					break;
				case R.id.btnPrintTxt:
					initTxtPrint();

					try {
						// 初始化打印机
						byte[] arry = new byte[2];
						arry[0] = 0x1b;
						arry[1] = 0x40;
						txtOutputStreamPrint.write(arry);
						byte bold[] = { 27, 69, 1 };
						txtOutputStreamPrint.write(bold, 0, 3);
						txtOutputStreamPrint.write("hello world\r\n".getBytes("GB2312"));
						//打印一维码



						//打印二维码
						String no="hello_sinxin";
						POS_S_SetQRcode(no, 4, 3, 3);
						txtOutputStreamPrint.write("\r\n\n".getBytes("GB2312"));

					} catch (Exception e) {
						Log.e("测试",""+e.getMessage());
					}
					break;
				case R.id.btnReturnZero:
					ReturnZero();
					break;
				case R.id.cd1:
					setCdName(cd1.getText().toString());
					break;
				case R.id.cd2:
					setCdName(cd2.getText().toString());
					break;
				case R.id.cd3:
					setCdName(cd3.getText().toString());
					break;
			}
		}
	}

	public void setCdName(String str) {
		if (diffPresentation != null) {
			diffPresentation.setCdName(str);
		}
	}

	private class ReadThreadWeight extends Thread {
		@Override
		public void run() {
			while (ifReadWeight) {
				handler.sendEmptyMessage(0);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 打印二维码，使用的命令非（ESC/POS）。一般适用于便携式打印机。
	 *
	 * @param strCodedata           二维码字符串
	 * @param nWidthX               二维码每个模块的单元宽度，[1,16]
	 * @param nVersion              二维码版本大小，该值和二维码大小相关。 [0,16]
	 *                              （设置为0自动计算二维码大小，设置为10基本占满2寸纸，如果二维码超出纸张宽度则不会打印）
	 * @param nErrorCorrectionLevel 纠错等级。[1,4]
	 */
	public void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion,
								int nErrorCorrectionLevel) {

		try {
			if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1
					|| nErrorCorrectionLevel > 4 || nVersion < 0
					|| nVersion > 16)
				throw new Exception("invalid args");

			byte[] bCodeData = strCodedata.getBytes();
			ESCCmd Cmd = new ESCCmd();

			Cmd.GS_w_n[2] = (byte) nWidthX;
			Cmd.GS_k_m_v_r_nL_nH[3] = (byte) nVersion;
			Cmd.GS_k_m_v_r_nL_nH[4] = (byte) nErrorCorrectionLevel;
			Cmd.GS_k_m_v_r_nL_nH[5] = (byte) (bCodeData.length & 0xff);
			Cmd.GS_k_m_v_r_nL_nH[6] = (byte) ((bCodeData.length & 0xff00) >> 8);

			byte[] data = byteArraysToBytes(new byte[][]{Cmd.GS_w_n,
					Cmd.GS_k_m_v_r_nL_nH, bCodeData});
			mOutputStreamPrint.write(data, 0, data.length);

		} catch (Exception ex) {
		}
	}



	private byte[] byteArraysToBytes(byte[][] data) {
		int length = 0;
		for (int i = 0; i < data.length; i++)
			length += data[i].length;
		byte[] send = new byte[length];
		int k = 0;
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				send[k++] = data[i][j];
		return send;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void ChangeUnit() {
		String unit = txtUnit.getText().toString();
		if (unit.equals("Kg") || unit.equals("公斤")) {
			txtUnit.setText("斤");
			unitflag = 1;
		} else {
			unitflag = 0;
			txtUnit.setText("Kg");
		}
	}

	void resetWeightPort() {
		String w = "3";
		String weightPort = sp.getString("weightPort", app.weightPort);
		String WEIGHTCOM = sp.getString("weightCom", app.WEIGHTCOM);
		if (WEIGHTCOM != null && !"".equals(WEIGHTCOM)) {
			w = WEIGHTCOM.substring(WEIGHTCOM.length() - 1);
		}
		w = SerialPortManager.rtnSerialStr(w);

		String ttyStr = SerialPortManager.rtnTTy(tty, w);
		Log.e("swang", "ttyStr + w=====" + ttyStr + w);
		Log.e("swang", "weightPort=====" + weightPort);
		weightdll.WeightOpen(ttyStr + w, Integer.parseInt(weightPort), 0, Integer.parseInt(sp.getString("maxValue", app.maxValue)), Integer.parseInt(sp.getString("ifDouWeight", app.ifDouWeight)),1);
		// weightdll.WeightOpen(tty + WEIGHTCOM, Integer.parseInt(weightPort),
		// 0,Integer.parseInt(sp.getString("maxValue",
		// app.maxValue)),Integer.parseInt(sp.getString("ifDouWeight",
		// app.ifDouWeight)));
		ifReadWeight = true;
	}

	boolean ifReadWeight = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				// 整型
				case 0:
					String weightValue = "";
					if (!TextUtils.isEmpty(wei)) {
						weightValue = wei;
					} else {
						weightValue = "0";
					}

					if (unitflag != 0) {
						weightValue = convertWeight(weightValue, "2", "3");
					}
					SetWeight.setText(weightValue);
					originalWeight.setText(MainActivity.ad);
					break;
			}
		}
	};

	// 置零
	public void ReturnZero() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					int[] P = { 12, 34 };
					int res = weightdll.WeightControl(4, P);
				} catch (Exception e) {

				}
			}
		}).start();

	}

	public String convertWeight(String weightValue, String unit, String weightDemical) {
		String convertWeightValue = "";
		int unitType = Integer.parseInt(unit);
		String unitWeightValue = "";
		BigDecimal bd = new BigDecimal(weightValue);
		switch (unitType) {
			case 1:
				convertWeightValue = convertDemical(weightValue, weightDemical) + "";
				break;
			case 2:

				unitWeightValue = bd.multiply(new BigDecimal(2)) + "";
				convertWeightValue = convertDemical(unitWeightValue, weightDemical) + "";
				break;
			case 3:

				unitWeightValue = bd.divide(new BigDecimal(1000)) + "";
				convertWeightValue = convertDemical(unitWeightValue, weightDemical) + "";
				break;
			case 4:
				unitWeightValue = bd.multiply(new BigDecimal(1000)) + "";
				convertWeightValue = convertDemical(unitWeightValue, weightDemical) + "";
				break;
			case 5:
				unitWeightValue = bd.multiply(new BigDecimal(2.2046226218488)) + "";
				convertWeightValue = convertDemical(unitWeightValue, weightDemical) + "";
				break;

			default:
				break;
		}
		return convertWeightValue;
	}

	public BigDecimal convertDemical(String weightValue, String weightDemical) {
		BigDecimal bd = new BigDecimal(weightValue);
		if (weightDemical.equals("0")) {
			bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
		} else if (weightDemical.equals("1")) {
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		} else if (weightDemical.equals("2")) {
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else if (weightDemical.equals("3")) {
			bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
		} else if (weightDemical.equals("4")) {
			bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP);
		} else {
			bd = bd.setScale(5, BigDecimal.ROUND_HALF_UP);
		}
		return bd;
	}

	public PrinterCMD pcmd = new PrinterCMD();
	public String command = "";
	public byte[] outbytes;

	// / <summary>
	// / 网络打印机 打印的文本
	// / </summary>
	// / <param name="pszString"></param>
	// / <param name="nFontAlign">0:居左 1:居中 2:居右</param>
	// / <param name="nfontsize">字体大小0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽
	// 6:三倍大小 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小</param>
	// / <param name="ifzhenda">0:非针打 1:针打</param>
	// / <param name="bold">0:解除粗体打印模式 1:设定粗体打印模式</param>
	public void PrintText(String pszString, int nFontAlign, int nFontSize, int ifzhenda, int bold) {
		try {
			command = pcmd.CMD_TextAlign(nFontAlign);
			outbytes = command.getBytes(Charset.forName("ASCII"));
			mOutputStreamPrint.write(outbytes);

			if (ifzhenda == 1) {
				command = pcmd.CMD_FontSize_BTP_M280(nFontSize);
				outbytes = command.getBytes(Charset.forName("ASCII"));
				mOutputStreamPrint.write(outbytes);

				command = pcmd.CMD_FontSize_BTP_M2801(nFontSize);
				outbytes = command.getBytes(Charset.forName("ASCII"));
				mOutputStreamPrint.write(outbytes);
			} else {
				command = pcmd.CMD_FontSize(nFontSize);
				outbytes = command.getBytes(Charset.forName("ASCII"));
				mOutputStreamPrint.write(outbytes);
			}
			command = pcmd.CMD_Bold(bold);
			outbytes = command.getBytes(Charset.forName("ASCII"));
			mOutputStreamPrint.write(outbytes);
			command = pszString;// +CMD_Enter();
			outbytes = command.getBytes(Charset.forName("GB2312")); // Charset.defaultCharset());
			// //forName("UTF-8")
			mOutputStreamPrint.write(outbytes);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		if (mSerialPortPrint != null) {
			mSerialPortPrint.close();
			mSerialPortPrint = null;
		}
		// 重量板so
		if (weightdll != null) {
			weightdll.WeightClose();
		}

		mSerialPortPrint = null;

		app.removeActivity(this);
		super.onDestroy();
	}

	public String print22Code(String agencyName, String cdName, String date, String billNo, String terOId, String printCount, String weight) {
		try {
			String tmp = getFromAssets("print.txt");
			// String str=String.format(tmp,printBar2Code("abc"));
			String str = String.format(tmp, PrintBarTempalte(agencyName, cdName, date, billNo, terOId, weight), printCount);
			return str;
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}

	public String PrintBarTempalte(String agencyName, String cdName, String date, String billNo, String terOId, String weight) {
		String tagency = PrintBarText("公司名称：" + agencyName, 65, 25) + "\n";
		String tdate = PrintBarText("进场日期：" + date, 65, 60) + "\n";
		String tbillNo = PrintBarText("进场批次：" + billNo, 65, 95) + "\n";
		String tcdName = PrintBarText("商品名称：" + cdName, 65, 130) + "\n";
		String tTerOId = PrintBarText("终端编号：" + terOId, 65, 165) + "\n";
		String tWeight = PrintBarText("商品净重：" + weight + "kg", 65, 200) + "\n";
		String xcode = PrintBarCode(billNo, 65, 235) + "\n";
		String twocode = Print2BarCode(billNo, 300, 120);
		// String aa = PrintBarText(billNo, 65, 270);
		return tagency + tdate + tbillNo + tcdName + tTerOId + tWeight + tWeight + xcode + twocode;
	}

	public String PrintBarText(String str, int x, int y) {
		String code = String.format("TEXT " + x + "," + y + ",\"TSS24.BF2\",0,1,1,\"%s\"", str);
		return code;
	}

	public String PrintBarCode(String str, int x, int y) {
		String code = String.format("BARCODE " + x + "," + y + ",\"128\",40,1,0,2,1,\"%s\"", str);
		return code;
	}

	public String Print2BarCode(String str, int x, int y) {
		// str="http://"+lip+":8090/trade/TradeSubInfoCamera?cameraId="+str;
		// str = QrUrl + "?cameraId=" + str;
		str = "=" + str;
		String code = String.format("QRCODE " + x + "," + y + ",L,3,A,0,\"%s\"", str);
		return code;
	}

	public String getFromAssets(String fileName) {
		String Result = "";
		try {
			InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				Result += line + "\n";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

	public void getSoTx800(final String cardNum) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "卡号：" + cardNum, Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 關閉刷卡器串口
	public void CloseSerial() {
		if (sdll != null)
			sdll.Exit();
	}

	//监听按键值;
	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {
		int action = event.getAction();

		final int KeyCode = event.getKeyCode();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					Log.d("Y", "--按键--" + KeyCode);
					Toast.makeText(MainActivity.this, "KeyCode：" + KeyCode, Toast.LENGTH_SHORT).show();
				}
			}
		});
		return super.dispatchKeyEvent(event);
	}
}