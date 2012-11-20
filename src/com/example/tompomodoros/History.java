package com.example.tompomodoros;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;

public class History extends Activity {
	private static Map map = new TreeMap<String, Object>();
	private static int mydata_user;
	private static String mydate_key;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		mydata_user = bundle.getInt("mydata");
		mydate_key = bundle.getString("mydate");
		
		// �򿪻򴴽�tompomodoros.db���ݿ�
		SQLiteDatabase db = openOrCreateDatabase("tompomodoros.db",
				Context.MODE_PRIVATE, null);
		db.execSQL("DROP TABLE IF EXISTS mytable");	// ������? to be fixed...
		// ����mytable��
		db.execSQL("CREATE TABLE mytable (_id INTEGER PRIMARY KEY AUTOINCREMENT, mydate VARCHAR, mydata SMALLINT)");
				
		//ContentValues�Լ�ֵ�Ե���ʽ�������  
        ContentValues cv = new ContentValues();  
        cv.put("mydate", mydate_key);  
        cv.put("mydata", mydata_user);  
        //����ContentValues�е�����  
        db.insert("mytable", null, cv);  
		db.close();

		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
		setChartSettings(renderer);
		// (1) during it map was filled, by Tom Xue
		XYMultipleSeriesDataset getBarDataset2 = getBarDataset(this);

		int count = 1;
		// ����Ƚ���Ҫ�������ֶ���X����̶ȡ��ж��������ݣ����Ҫ����ٸ��̶ȣ�����X�����ʾ����ʱ�䣬Ҳ����ʾ��������ͼ
		// (2) then map is further rendered, by Tom Xue
		for (Object key_tmp : map.keySet()) {
			renderer.addXTextLabel(count, key_tmp.toString());
			count++;
		}

		View chart = ChartFactory.getBarChartView(this, getBarDataset2,
				renderer, Type.DEFAULT);
		setContentView(chart);
	}

	private static XYMultipleSeriesDataset getBarDataset(Context cxt) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries("���31��");

		map.put(mydate_key, 0.0);	// initialize to zero

		// �����list����ȡ��һ�������б��Լ������ұ�����ݴ���
		// List<int[]> list = new ArrayList<int[]>();
		// list.add(new int[] { 18, 9, 21, 15, 10, 6 }); // data to be shown
		// if (list != null && list.size() > 0) {
		// for (int i = 0; i < list.size(); i++) {
		// if (map.containsKey(mydate_key)) {
		// map.put(mydate_key, mydata_user); // but the key is unique
		// }
		// }
		// }

		for (Object key1 : map.keySet()) {
			// series.add((String) key1, mydata_user);
			series.add(mydate_key, mydata_user);
		}
		dataset.addSeries(series.toXYSeries());
		return dataset;
	}

	private static XYMultipleSeriesRenderer getBarDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(16);
		renderer.setLabelsTextSize(16);
		renderer.setLegendTextSize(16);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	private static void setChartSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setChartTitle("���31��");
		renderer.setXTitle("����");
		renderer.setYTitle("������");
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(31);
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(7.5);
		renderer.setShowLegend(false);
		renderer.setShowLabels(true);
		renderer.setShowGrid(true);
		renderer.setXLabels(1);
		// renderer.setDisplayChartValues(true);
		renderer.setBackgroundColor(Color.WHITE);
		// ����ҳ�߿հ׵���ɫ
		renderer.setMarginsColor(Color.GRAY);
		// �����Ƿ���ʾ,���������,Ĭ��Ϊ true
		renderer.setShowAxes(true);
		// ����x,y����ʾ������
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// ����������,�����ɫ
		renderer.setAxesColor(Color.RED);
		// ��ʾ����
		renderer.setShowGrid(true);
		// ����x,y���ϵĿ̶ȵ���ɫ
		renderer.setLabelsColor(Color.BLACK);
		// �����Ƿ���ʾ,���������,Ĭ��Ϊ true
		renderer.setShowAxes(true);
		// ��������ͼ֮��ľ���
		renderer.setBarSpacing(2.5);
		int length = renderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);
			// ��֪�����ߵľ�������ô�����,Ĭ����Align.CENTER,���Ƕ����������ϵ�������ʾ
			// �ͻ��������ұ�
			ssr.setChartValuesTextAlign(Align.RIGHT);
			ssr.setChartValuesTextSize(16);
			ssr.setDisplayChartValues(true);
		}
	}

}