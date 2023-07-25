package com.sx.sxhardware;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sinxin.sxhardware.R;
import com.sx.manager.SerialPortManager;
import com.wzx.WeightAPI.WeightDLL;

/**
 * @author : 马世豪
 * time : 2023/7/17 16
 * email : ma_shihao@yeah.net
 * des :
 */
public class MainActivity2 extends BaseActivity {
    TextView SetWeight;
    private WeightDLL weightdll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetWeight = (TextView) findViewById(R.id.SetWeight);

        try {
            // 实例化重量版
            weightdll = new WeightDLL();
            resetWeightPort();
            weightdll.setCallBack(new WeightDLL.WeightCallBack() {
                @Override
                public void onWeightChange(final String widget) {
                    Toast.makeText(app, widget, Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SetWeight.setText(widget);
                        }
                    });
                }
            });

        } catch (Exception e) {

        }

    }

    boolean ifReadWeight = false;
    String tty = "/dev/ttyS";

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
        weightdll.WeightOpen(ttyStr + w, Integer.parseInt(weightPort), 0, Integer.parseInt(sp.getString("maxValue", app.maxValue)), Integer.parseInt(sp.getString("ifDouWeight", app.ifDouWeight)), 1);
        // weightdll.WeightOpen(tty + WEIGHTCOM, Integer.parseInt(weightPort),
        // 0,Integer.parseInt(sp.getString("maxValue",
        // app.maxValue)),Integer.parseInt(sp.getString("ifDouWeight",
        // app.ifDouWeight)));
        ifReadWeight = true;
    }
}
