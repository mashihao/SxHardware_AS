package com.sx.sxhardware;

import com.sinxin.sxhardware.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastAcitivity {
	public static void toast(Context context,String msg) {
		 Toast t=new Toast(context);
		 LayoutInflater inflater = LayoutInflater.from(context);

		 View toastView = inflater.inflate((Integer) R.layout.toast, null);
	  	 TextView txt=(TextView)toastView.findViewById(R.id.txtToast);
	  	 txt.setText(msg);	  	 
	  	 t.setGravity(Gravity.BOTTOM, 0, 0);
	   	 t.setView(toastView);
	   	 t.setDuration(Toast.LENGTH_LONG);
	   	 t.show();
	}
	
	public static void toast(Context context,String msg,int color) {
		 Toast t=new Toast(context);
		 LayoutInflater inflater = LayoutInflater.from(context);

		 View toastView = inflater.inflate((Integer) R.layout.toast, null);
	  	 TextView txt=(TextView)toastView.findViewById(R.id.txtToast);
	  	 txt.setText(msg);
	  	 txt.setTextColor(color);
	   	 t.setView(toastView);
	   	 t.setDuration(Toast.LENGTH_LONG);
	   	 t.setGravity(Gravity.BOTTOM, 0, 0);
	   	 t.show();
	}
	
}
