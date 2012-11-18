package com.example.tompomodoros;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import java.util.Timer;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button_start = (Button) findViewById(R.id.button1);
		button_start.setOnClickListener(start);
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
			Timer timer = new Timer();
			timer.schedule(new MyTask(), 0, 1000);
		}
	};

	static class MyTask extends java.util.TimerTask {
		@Override
		public void run() {
			System.out.println("________");
		}
	}
}
