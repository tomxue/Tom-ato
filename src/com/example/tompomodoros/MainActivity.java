package com.example.tompomodoros;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	private ProgressBar progressbar;
	private Button button_start;

	protected static final int STOP = 0x10000;
	protected static final int NEXT = 0x10001;

	private int iCount = 0;
	private int TotalLength = 1500;  // 60*25=1500

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button_start = (Button) findViewById(R.id.button1);
		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
		progressbar.setIndeterminate(false);
		
		button_start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
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
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

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
