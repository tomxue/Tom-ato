package com.example.tompomodoros;

import java.util.Iterator;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.tompomo.R;

public class History extends Activity {
	private static Map map = new TreeMap<String, Object>();  // TreeMap������ģ��������֮��by Tom Xue
	private static final String TAG = "tomxue";
	private static SQLiteDatabase db;
	private final String DBNAME = "tompomo12.db";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        // �򿪻򴴽�tompomodoros.db���ݿ�
     	db = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
     	db.execSQL("CREATE TABLE if not exists mytable (_id INTEGER PRIMARY KEY AUTOINCREMENT, mydate VARCHAR, mydata SMALLINT)");
		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
		setChartSettings(renderer);
		// (1) during it map was filled, by Tom Xue
		XYMultipleSeriesDataset getBarDataset2 = getBarDataset(this);

		int count = 1;
		// ����Ƚ���Ҫ�������ֶ���X����̶ȡ��ж��������ݣ����Ҫ����ٸ��̶ȣ�����X�����ʾ����ʱ�䣬Ҳ����ʾ��������ͼ
		// (2) then map is further rendered, by Tom Xue
		for (Object key_tmp : map.keySet()) {
			renderer.addXTextLabel(count, key_tmp.toString());
			System.out.println("------map-------");
			System.out.println(key_tmp.toString());
			count++;  
		}

		// show the last 7 data/bars
		if(count < 7){
			renderer.setXAxisMin(0.5);
			renderer.setXAxisMax(7.5);
		}
		else
		{			
			renderer.setXAxisMin(count-7+0.5);
			renderer.setXAxisMax(count-7+7.5);	
		}

		View chart = ChartFactory.getBarChartView(this, getBarDataset2,
				renderer, Type.DEFAULT);
		setContentView(chart);
		
		db.close();
	}

	private static XYMultipleSeriesDataset getBarDataset(Context cxt) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries("���31��");

		Cursor c = db.rawQuery("SELECT _id, mydate, mydata FROM mytable", new String[]{});  
        while (c.moveToNext()) {
            int _id = c.getInt(c.getColumnIndex("_id"));
            String mydate = c.getString(c.getColumnIndex("mydate"));
            int mydata = c.getInt(c.getColumnIndex("mydata"));
            map.put(mydate, mydata);
//          series.add(mydate, mydata);
            Log.v(TAG, "while loop times");
            System.out.println(_id);
            System.out.println(mydate);
            System.out.println(mydata);
            System.out.println("---------------------");
        }  
        c.close();  	
        
        // map -> series, ������ʾ����
        // ����û������޸��ֻ����ڣ���ôdb�е����ݾ�δ���ǰ����������е�
        // Ϊ�˰���������ʾ����������������TreeMap
        Iterator it = map.entrySet().iterator();
        double value_tmp;
        String key_tmp;
        while (it.hasNext()) {
        	              Map.Entry e = (Map.Entry) it.next();
        	              System.out.println("Key: " + e.getKey() + "; Value: "
        	                      + e.getValue());
        	              key_tmp = (String)e.getKey();
        	              value_tmp = Integer.parseInt((e.getValue().toString()));
        	              series.add(key_tmp,value_tmp);
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
		// set it by default
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(7.5);
		
		renderer.setShowLegend(true);
		renderer.setShowLabels(true);
		renderer.setXLabels(1);
		renderer.setBackgroundColor(Color.WHITE);
		// ����ҳ�߿հ׵���ɫ
		renderer.setMarginsColor(Color.GRAY);
		// ����x,y����ʾ������
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// ����������,�����ɫ
		renderer.setAxesColor(Color.RED);
		// ��ʾ����
		renderer.setShowGrid(true);
		// ����x,y���ϵĿ̶ȵ���ɫ
		renderer.setLabelsColor(Color.GREEN);
		// �����Ƿ���ʾ,���������,Ĭ��Ϊ true
		renderer.setShowAxes(true);
		// ��������ͼ֮��ľ���
		renderer.setBarSpacing(2.5);
		
		// by it, the x-axis number 0 10 20 30.. can be hiden
		renderer.setXLabels(RESULT_OK);
		
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