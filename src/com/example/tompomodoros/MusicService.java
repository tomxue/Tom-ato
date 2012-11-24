package com.example.tompomodoros;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
	// MediaPlayer����
	private MediaPlayer player;

    // startService() used, bindService() not used
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		// ����������Ϊװ�������ļ�
		player = MediaPlayer.create(this, R.raw.anydo_pop);
		// ��ʼ����
		player.start();
	}

	public void onDestroy() {
		super.onDestroy();
		// ֹͣ����-ֹͣService
		player.stop();
	}

}

