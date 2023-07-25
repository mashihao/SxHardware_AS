package com.sx.sxhardware;

import com.sinxin.sxhardware.R;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

@SuppressLint("NewApi") 
public class DifferentDislay extends Presentation{

    public DifferentDislay(Context outerContext, Display display) {
        super(outerContext,display);
        //TODOAuto-generated constructor stub  
    }

    TextView cdname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.diff_traceinfo);
        /*View viewNumber = (View) findViewById(R.id.viewNumber);
        setContentView(viewNumber);*/
        cdname=(TextView)findViewById(R.id.diff_cdname);
    }
    
    
    public void setCdName(String str){
    	cdname.setText(str);
    }
    
    

}
