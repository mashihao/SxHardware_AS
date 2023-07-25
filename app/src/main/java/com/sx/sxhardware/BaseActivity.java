package com.sx.sxhardware;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	// 存储设置的参数
	protected SharedPreferences sp;
	// 获取Editor对象
	protected Editor editor;
	private ActionBar actionBar;
	protected AppData app;


	protected ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);//隐藏Label标签：
		actionBar.setDisplayShowHomeEnabled(false);// 隐藏logo和icon：
*/

		app = (AppData) getApplicationContext();
		app.addActivity(this);
		// xml的配置
		sp = this.getSharedPreferences("SystemPreferences",
				Activity.MODE_PRIVATE);
		editor = sp.edit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	private void dimssDialog(Dialog dialog) {
		dialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		app.removeActivity(this);
		super.onDestroy();
	}
}
