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
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;

public class History extends Activity {
	private static Map map = new TreeMap<String, Object>();

	private class PomodorosData {
		String date;
		int mydata;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 打开或创建tompomodoros.db数据库
		SQLiteDatabase db = openOrCreateDatabase("tompomodoros.db",
				Context.MODE_PRIVATE, null);
		 db.execSQL("DROP TABLE IF EXISTS mytable");
		// 创建mytable表
		db.execSQL("CREATE TABLE mytable (_id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, mydata SMALLINT)");
		PomodorosData pomodorosdata = new PomodorosData();
		pomodorosdata.date = "11-29";
		pomodorosdata.mydata = 16;
		// 插入数据
		db.execSQL("INSERT INTO mytable VALUES (NULL, ?, ?)", new Object[] {
				pomodorosdata.date, pomodorosdata.mydata });
		db.close();

		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
		setChartSettings(renderer);
		// (1) during it map was filled, by Tom Xue
		XYMultipleSeriesDataset getBarDataset2 = getBarDataset(this);

		int count = 1;
		// 这里比较重要，这里手动给X轴填刻度。有多少条内容，你就要添多少个刻度，这样X轴就显示的是时间，也能显示出长方形图
		// (2) then map is further rendered by Tom Xue
		for (Object key2 : map.keySet()) {
			renderer.addXTextLabel(count, key2.toString());
			count++;
		}

		View chart = ChartFactory.getBarChartView(this, getBarDataset2,
				renderer, Type.DEFAULT);
		setContentView(chart);
	}

	private static XYMultipleSeriesDataset getBarDataset(Context cxt) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries("最近31天");
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		String key = year + "-" + (month < 10 ? ("0" + month) : month) + "-"
				+ (day < 10 ? ("0" + day) : day);

		map.put(key, 0.0);
		// for (int i = 0; i <= 29; i++) { // 31 days
		// c.add(Calendar.DAY_OF_YEAR, -1); // ?
		// day = c.get(Calendar.DAY_OF_MONTH);
		// month = c.get(Calendar.MONTH) + 1;
		// map.put(key, 0.0);
		// }

		// 这里的list是我取出一个对象列表，自己可以找别的数据代替
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[] { 18, 9, 21, 15, 10, 6 }); // data to be shown
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (map.containsKey(key)) {
					map.put(key, list.get(i)); // but the key is unique
				}
			}
		}

		for (Object key1 : map.keySet()) {
			series.add((String) key1, 3);
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
		renderer.setChartTitle("最近31天");
		renderer.setXTitle("日期");
		renderer.setYTitle("番茄数");
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
		// 设置页边空白的颜色
		renderer.setMarginsColor(Color.GRAY);
		// 设置是否显示,坐标轴的轴,默认为 true
		renderer.setShowAxes(true);
		// 设置x,y轴显示的排列
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// 设置坐标轴,轴的颜色
		renderer.setAxesColor(Color.RED);
		// 显示网格
		renderer.setShowGrid(true);
		// 设置x,y轴上的刻度的颜色
		renderer.setLabelsColor(Color.BLACK);
		// 设置是否显示,坐标轴的轴,默认为 true
		renderer.setShowAxes(true);
		// 设置条形图之间的距离
		renderer.setBarSpacing(2.5);
		int length = renderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);
			// 不知道作者的居中是怎么计算的,默认是Align.CENTER,但是对于两个以上的条形显示
			// 就画在了最右边
			ssr.setChartValuesTextAlign(Align.RIGHT);
			ssr.setChartValuesTextSize(16);
			ssr.setDisplayChartValues(true);
		}
	}

}