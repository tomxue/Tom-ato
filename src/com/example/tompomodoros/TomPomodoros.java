package com.example.tompomodoros;

import java.util.Calendar;
import java.util.Timer;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class TomPomodoros extends Activity {
	private ProgressBar progressbar;
	private Button button_start, button_cancel, button_history, button_clear;

	protected static final int STOP = 0x10000;
	protected static final int NEXT = 0x10001;

	private int iCount = 0;
	private int TotalLength = 4; // 60*25=1500，25分钟/番茄
	private static String mydate_key;
	private final String DBNAME = "tompomo12.db";
	private static SQLiteDatabase db;
	Timer timer_tmp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 初始化mydate_key
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		mydate_key = year + "-" + (month < 10 ? ("0" + month) : month)
				+ "-" + (day < 10 ? ("0" + day) : day);

		button_start = (Button) findViewById(R.id.button1);
		button_cancel = (Button) findViewById(R.id.button2);
		button_history = (Button) findViewById(R.id.button3);
		button_clear = (Button) findViewById(R.id.button4);
		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
		progressbar.setIndeterminate(false);

		button_start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 创建一个线程,每秒步长为1增加,到100%时停止
				progressbar.setVisibility(View.VISIBLE);
				progressbar.setMax(TotalLength);
				progressbar.setProgress(0);

				Timer timer = new Timer();
				timer.schedule(new ProgressbarTask(), 0, 1000);
				timer_tmp = timer;		
				
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				mydate_key = year + "-" + (month < 10 ? ("0" + month) : month)
						+ "-" + (day < 10 ? ("0" + day) : day);
			}
		});

		button_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				iCount = 0;
				progressbar.setProgress(0);
				timer_tmp.cancel();
			}
		});

		button_history.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Switch to report page
				Intent intent = new Intent();
				intent.setClass(TomPomodoros.this, History.class);
				startActivity(intent);
			}
		});

		button_clear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				deleteDatabase("tompomo12.db");
			}
		});
	}

	private class ProgressbarTask extends java.util.TimerTask {
		@Override
		public void run() {
			iCount += 1;
			progressbar.setProgress(iCount);
			if (iCount == TotalLength) {
				timer_tmp.cancel();
				progressbar.setProgress(0);
				iCount = 0;

				// 每结束一个番茄，再操作db
				dbHandler();
			}
		}
	}

	private void dbHandler() {
		// 打开或创建tompomodoros.db数据库
		db = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
		// 创建mytable表
		db.execSQL("CREATE TABLE if not exists mytable (_id INTEGER PRIMARY KEY AUTOINCREMENT, mydate VARCHAR, mydata SMALLINT)");
		// ContentValues以键值对的形式存放数据, make the table not empty, by Tom Xue
		ContentValues cv;

		int mydata_dbitem = 0;
		boolean dbitem_exist = false;
		Cursor c = db.rawQuery("SELECT _id, mydate, mydata FROM mytable",
				new String[] {});
		while (c.moveToNext()) {	// 有则替换
			String mydate_item = c.getString(c.getColumnIndex("mydate"));
			if (mydate_item.equals(mydate_key)) {
				mydata_dbitem = c.getInt(c.getColumnIndex("mydata"));
				cv = new ContentValues();
				
				cv.put("mydata", 1 + mydata_dbitem);
				
				// 更新数据
				db.update("mytable", cv, "mydate = ?",
						new String[] { mydate_key });
				dbitem_exist = true;
			}
		}
		c.close();

		// 无则插入
		if (dbitem_exist == false) {
			// ContentValues以键值对的形式存放数据
			cv = new ContentValues();
			cv.put("mydate", mydate_key);
			cv.put("mydata", 1);
			// 插入ContentValues中的数据
			db.insert("mytable", null, cv);
		}

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
