package com.sinxin;


import java.io.UnsupportedEncodingException;

import com.sx.sxhardware.MainActivity;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class TraceCardDLL
{

    public final int fLZCardTrans_Sign = 1;
    public final int fLZCardTrans_Cos = 2;			//消费
    public final int fLZCardTrans_BatchUpload = 3;	//批上传
    public final int fLZCardTrans_BatchClear = 4;

    public final int fLZCardTrans_GetUserInfo = 11;



    private Context mContext;

    public TraceCardDLL(Context context) {
        mContext = context;
    }

    //callback from JNI
    //type == -1000,t600读卡器返回
    //其它:t800读卡器
    private void ZSTraceCardCallback(int type, int Current, int Total, String CardSn){

        MainActivity main= (MainActivity)mContext;
        main.getSoTx800(CardSn);
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }








    public native  void func(int type, int Current, int Total, String cardSn);



    //CardModuleType:0--t600;1---t800
    //portName:
    //BandRate:
    //NOTING：需要判断返回值，如果返回值!=0;表示有错误发生
    public native int Init(int CardModuleType, String portName, int BandRate);

    //启动m1卡读卡器读取序列号
    public native int Exit();

    //设置访问密码
    public native int LoadKey(String strKey);

    //强制退出拔卡处理流程
    public native int SetTransDetail(String strDetail);

    //获取用户详细信息
    public native int GetTransDetail();

    //强制中止寻卡
    public native int AbortFindCard();

    //清除交易明细
    public native int ClearAllDetail();

    //输入函数返回值，，得到相当提标信息
    public native String GetErrMsg(int index);

    //use lib
    static {
        System.loadLibrary("ZSTraceCard");
    }

}
