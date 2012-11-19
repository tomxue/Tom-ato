package com.example.tompomodoros;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;

public class History extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] titles = new String[] { "First", "Second" };

		List x = new ArrayList();
		List y = new ArrayList();

		x.add(new double[] { 1, 3, 5, 7, 9, 11 });
		x.add(new double[] { 0, 2, 4, 6, 8, 10 });

		y.add(new double[] { 3, 14, 5, 30, 20, 25 });
		y.add(new double[] { 18, 9, 21, 15, 10, 6 });

		XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);

		int[] colors = new int[] { Color.BLUE, Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.DIAMOND };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

		setChartSettings(renderer, "Pomodoros History", "X", "Y", -1, 12, 0,
				35, Color.WHITE, Color.WHITE);

		// View chart = ChartFactory.getLineChartView(this, dataset, renderer);
		View chart = ChartFactory.getBarChartView(this, dataset, renderer,
				Type.DEFAULT);
		setContentView(chart);
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // 有几条线
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i]); // 根据每条线的名称创建
			double[] xV = (double[]) xValues.get(i); // 获取第i条线的数据
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length; // 有几个点

			for (int k = 0; k < seriesLength; k++) // 每条线里有几个点
			{
				series.add(xV[k], yV[k]);
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(fill);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		// font size of "X, Y"
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(16);
		renderer.setLabelsTextSize(16);
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
		renderer.setBarSpacing(0.5);
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