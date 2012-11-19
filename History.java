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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;

public class History extends Activity {
	private static Map map = new TreeMap<String, Object>();

	@Override
public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
		setChartSettings(renderer);
		XYMultipleSeriesDataset getBarDataset2 = getBarDataset(this); // then map was filled, by Tom Xue
		
		int count = 1;
		// ����Ƚ���Ҫ�������ֶ���X����̶ȡ��ж��������ݣ����Ҫ����ٸ��̶ȣ�����X�����ʾ����ʱ�䣬Ҳ����ʾ��������ͼ
		for (Object key2 : map.keySet()) {
			renderer.addXTextLabel(count, key2.toString());
			count++;
		}
		
		View chart = ChartFactory.getBarChartView(this, getBarDataset2, renderer, Type.DEFAULT);
		setContentView(chart);
		}

	private static XYMultipleSeriesDataset getBarDataset(Context cxt) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries("���31��");
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		String key = (month < 10 ? ("0" + month) : month) + "-"
				+ (day < 10 ? ("0" + day) : day);
		
		map.put(key, 0.0);
		for (int i = 0; i <= 29; i++) {	// 31 days
			c.add(Calendar.DAY_OF_YEAR, -1);	// ?
			day = c.get(Calendar.DAY_OF_MONTH);
			month = c.get(Calendar.MONTH) + 1;
			map.put(key, 0.0);
		}		

		// �����list����ȡ��һ�������б��Լ������ұ�����ݴ���
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[] { 18, 9, 21, 15, 10, 6 });
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (map.containsKey(key)) {
					map.put(key, list.get(i));
				}
			}
		}
		
		for (Object key1 : map.keySet()) {
			series.add((String)key1, 3);
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
		r.setColor(Color.RED);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	private static void setChartSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setChartTitle("���31��");
		renderer.setXTitle("����");
		renderer.setYTitle("������");
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(30);
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(12.5);
		renderer.setShowLegend(false);
		renderer.setShowLabels(true);
		renderer.setShowGrid(true);
		renderer.setXLabels(1);
		renderer.setDisplayChartValues(true);
		renderer.setBarSpacing(0.5);
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
		renderer.setBarSpacing(0.5);
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