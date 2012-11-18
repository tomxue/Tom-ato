package com.example.tompomodoros;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import java.util.Timer;
import android.widget.Button;
import android.widget.ProgressBar;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private ProgressBar progressbar;
	protected static final int STOP = 0x10000;
	protected static final int NEXT = 0x10001;
	private int iCount = 0;
	private int TotalLength = 60 * 25;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button_start = (Button) findViewById(R.id.button1);
		ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar1);

		button_start.setOnClickListener(start);
		progressbar.setIndeterminate(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// 开始按钮
	private OnClickListener start = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 开启Service
			// startService(new Intent("com.yarin.Android.MUSIC"));
			// Timer timer = new Timer();
			// timer.schedule(new MyTask(), 0, 1000);

			// 创建一个线程,每秒步长为1增加,到100%时停止
			progressbar.setVisibility(View.VISIBLE);
			progressbar.setMax(TotalLength);
			progressbar.setProgress(0);

			Thread mThread = new Thread(new Runnable() {
				public void run() {
					for (iCount = 0; iCount < TotalLength; iCount++) {
						try {
							Thread.sleep(1000);
							if (iCount == TotalLength) {
								Message msg = new Message();
								msg.what = STOP;
								mHandler.sendMessage(msg);
								break;
							} else {
								Message msg = new Message();
								msg.what = NEXT;
								mHandler.sendMessage(msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});
			mThread.start();
		}
	};

	// static class MyTask extends java.util.TimerTask {
	// @Override
	// public void run() {
	// System.out.println("________");
	//
	// }
	// }

	// 定义一个Handler
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOP:
				progressbar.setVisibility(View.GONE);
				Thread.currentThread().interrupt();
				break;
			case NEXT:
				if (!Thread.currentThread().isInterrupted()) {
					progressbar.setProgress(iCount);
				}
				break;
			default:
				break;
			}
		}
	};
}
