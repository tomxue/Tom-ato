package com.example.tompomodoros;

import java.util.Calendar;
import java.util.Timer;

import android.os.Bundle;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.Gravity;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import com.example.tompomo.R;

public class Tomato extends Activity {
	private ProgressBar progressbar;
	private Button button_start, button_cancel, button_history, button_clear,
			button_about;
	private Chronometer chronometer;

	protected static final int STOP = 0x10000;
	protected static final int NEXT = 0x10001;

	private int iCount = 0;
	private int TotalLength = 3; // 60*25=1500，25分钟/番茄
	private static String mydate_key;
	private final String DBNAME = "tompomo12.db";
	private static SQLiteDatabase db;
	private static Timer timer1;
	private static PowerManager.WakeLock mWakeLock;
	private static Vibrator vt;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// screen kept on related
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TomTag");

		vt = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);

		// 初始化mydate_key
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		mydate_key = year + "-" + (month < 10 ? ("0" + month) : month) + "-"
				+ (day < 10 ? ("0" + day) : day);

		button_start = (Button) findViewById(R.id.button1);
		button_cancel = (Button) findViewById(R.id.button2);
		button_history = (Button) findViewById(R.id.button3);
		button_clear = (Button) findViewById(R.id.button4);
		button_about = (Button) findViewById(R.id.button5);
		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
		progressbar.setIndeterminate(false);
		chronometer = (Chronometer) findViewById(R.id.chronometer1);
		chronometer.setFormat("%s");

		button_start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// getWindow().addFlags(
				// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				mWakeLock.acquire();

				// 创建一个线程,每秒步长为1增加,到100%时停止
				progressbar.setVisibility(View.VISIBLE);
				progressbar.setMax(TotalLength);
				progressbar.setProgress(0);

				timer1 = new Timer();
				timer1.schedule(new ProgressbarTask(), 0, 1000);

				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				mydate_key = year + "-" + (month < 10 ? ("0" + month) : month)
						+ "-" + (day < 10 ? ("0" + day) : day);
				// set chronometer to zero
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
			}
		});

		button_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				iCount = 0;
				progressbar.setProgress(0);
				try {
					timer1.cancel();
				} catch (Exception e) {
					System.out.println("error = " + e.getMessage());
				}

				if (mWakeLock.isHeld())
					mWakeLock.release();
			}
		});

		button_history.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Switch to report page
				Intent intent = new Intent();
				intent.setClass(Tomato.this, History.class);
				startActivity(intent);
			}
		});

		button_clear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				deleteDatabase("tompomo12.db");

				Toast toast;
				toast = Toast.makeText(getApplicationContext(),
						"the db is deleted.", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
		});

		button_about.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Toast toast;
				toast = Toast.makeText(getApplicationContext(),
						"Author: Tom Xue" + "\n"
								+ "Email: tomxue0126@gmail.com" + "\n"
								+ "https://github.com/tomxue/Tom-ato.git",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}
		});
	}

	private class ProgressbarTask extends java.util.TimerTask {
		@Override
		public void run() {
			iCount += 1;
			progressbar.setProgress(iCount);
			if (iCount == TotalLength) {
				iCount = 0;
				progressbar.setProgress(0);
				// in case at the same time button_cancel is pressed
				// so try..catch.. applied
				try {
					timer1.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				chronometer.stop();

				// 每结束一个番茄，再操作db
				dbHandler();
				
				// play the sound
				startService(new Intent("com.example.tompomodoros.MUSIC"));
				// vibrate
				vt.vibrate(1000);

				// delay 5s
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (mWakeLock.isHeld())
					mWakeLock.release();
				// below part will cause fatal error
				// getWindow()
				// .addFlags(
				// WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
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
		while (c.moveToNext()) { // 有则替换
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
