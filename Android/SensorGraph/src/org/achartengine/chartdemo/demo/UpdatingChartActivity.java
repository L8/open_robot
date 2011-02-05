package org.achartengine.chartdemo.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class UpdatingChartActivity extends Activity {
	
	private static final int randoModulus = 2;
	 
	private static final boolean UPDATE_MODE_MOVING_WINDOW = true;
	 
	 String[] titles;
	 List<Date[]> dates;
	 List<double[]> values;
	 List<double[]> yValues;
	 
	 private Button mAdd;
	 
	 private boolean timerOn;

	 public static final String TYPE = "type";
	 
	  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	  

	  private GraphicalView mChartView;
	  
	  private Handler mHandler = new Handler();

	  @Override
	  protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	  }

	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);

	  }

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    titles = new String[] { "" };
	    dates = new ArrayList<Date[]>();
	    values = new ArrayList<double[]>();
	    yValues = new ArrayList<double[]>();
	    
	    double[] seedPositions = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }; 
	    values.add(seedPositions);
	    
	    Random r = new Random();
	    
	    double[] seedValues = new double[seedPositions.length];
	    for (int i = 0; i < seedPositions.length; i++) {
	    	seedValues[i] = r.nextInt() % randoModulus; 
	    }
	    
	    yValues.add(seedValues);
	    
	    setContentView(R.layout.chart);
	    
	    mAdd = (Button) findViewById(R.id.addButton);
	    
	    mAdd.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if (!timerOn) {
	        		 mHandler.removeCallbacks(mUpdateTimeTask);
	                 mHandler.postDelayed(mUpdateTimeTask, 100);
	                 mAdd.setText("OFF");
	        	} else {
	        		 mHandler.removeCallbacks(mUpdateTimeTask);
	        		 mAdd.setText("ON");
	        	}
	        	timerOn = !timerOn;
	        }
	      });
	    
	    /*
	    Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
	        new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
	        new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
	        new Date(97, 9, 1), new Date(98, 0, 1), new Date(98, 3, 1), new Date(98, 6, 1),
	        new Date(98, 9, 1), new Date(99, 0, 1), new Date(99, 3, 1), new Date(99, 6, 1),
	        new Date(99, 9, 1), new Date(100, 0, 1), new Date(100, 3, 1), new Date(100, 6, 1),
	        new Date(100, 9, 1), new Date(100, 11, 1) };
	    dates.add(dateValues);

	    
	    values.add(new double[] { 4.9, 5.3, 3.2, 4.5, 6.5, 4.7, 5.8, 4.3, 4, 2.3, -0.5, -2.9, 3.2,
	        5.5, 4.6, 9.4, 4.3, 1.2, 0, 0.4, 4.5, 3.4, 4.5, 4.3, 4 });
	    */
	    
	 
	  
	  }

	  @Override
	  protected void onResume() {
	    super.onResume();
	    if (mChartView == null) {
	    	
	    	   //mDataset = buildDateDataset(titles, dates, values);
	    	mDataset = buildDataset(titles, values, yValues);
			    int[] colors = new int[] { Color.BLUE };
			    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
			    mRenderer = buildRenderer(colors, styles);

	    	
	    	
	    	
	      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
	      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
	      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
	          LayoutParams.FILL_PARENT));

	    } else {
	      mChartView.repaint();
	    }
	  }
	  
	  protected void addValue() {
		  double[] theXValues = values.get(0);
		  double[] theYValues = yValues.get(0);
		  
		  Random r = new Random();
		  if (UPDATE_MODE_MOVING_WINDOW) {
			  for (int i = 0; i < theXValues.length - 1; i++) {
				  theXValues[i] = theXValues[i + 1];
			  }
			  theXValues[theXValues.length - 1] = theXValues[theXValues.length - 2] + 1;
			  
			  
			  for (int i = 0; i < theYValues.length - 1; i++) {
				  theYValues[i] = theYValues[i + 1];
			  }
			  theYValues[theYValues.length - 1] = r.nextInt() % randoModulus;
			  
			  yValues.set(0, theYValues);
			  
		  } else {
			  double[] newXValues = new double[theXValues.length + 1];
			  double[] newYValues = new double[theYValues.length + 1];
			  
			  for (int i = 0; i < theXValues.length; i++) { 
				  newXValues[i] = theXValues[i];
			  }
			  newXValues[theXValues.length] = theXValues[theXValues.length - 1] + 1;  //r.nextInt() % randoModulus;
			  values.set(0, newXValues);
			  
			  for (int i = 0; i < theYValues.length; i++) { 
				  newYValues[i] = theYValues[i];
			  }
			  newYValues[theYValues.length] = r.nextInt() % randoModulus;
			  yValues.set(0, newYValues);
			  
			  titles = new String[] { "This is the new title:  "  + Double.toString(newYValues[newYValues.length - 1]) };
			  Log.d("Test", Integer.toString(newXValues.length) + ", " + Integer.toString(newYValues.length));
		  }

		  
		  mDataset = buildDataset(titles, values, yValues); 
    	 
    	  LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
    	  layout.removeView(mChartView);
    	  
    	  mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
	      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
	          LayoutParams.FILL_PARENT));
	      
	      
	      mChartView.repaint();
	  }

		 public XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
			      List<double[]> yValues) {
			    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			    int length = titles.length;
			    for (int i = 0; i < length; i++) {
			      XYSeries series = new XYSeries(titles[i]);
			      Date[] xV = xValues.get(i);
			      double[] yV = yValues.get(i);
			      int seriesLength = xV.length;
			      for (int k = 0; k < seriesLength; k++) {
			        series.add(xV[k].getTime(), yV[k]);
			      }
			      dataset.addSeries(series);
			    }
			    return dataset;
			  }

		 protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
			      List<double[]> yValues) {
			    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			    int length = titles.length;
			    for (int i = 0; i < length; i++) {
			      XYSeries series = new XYSeries(titles[i]);
			      double[] xV = xValues.get(i);
			      double[] yV = yValues.get(i);
			      int seriesLength = xV.length;
			      for (int k = 0; k < seriesLength; k++) {
			        series.add(xV[k], yV[k]);
			      }
			      dataset.addSeries(series);
			    }
			    return dataset;
			  }
		 
		 protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
			    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
			    int length = colors.length;
			    for (int i = 0; i < length; i++) {
			      XYSeriesRenderer r = new XYSeriesRenderer();
			      r.setColor(colors[i]);
			      r.setPointStyle(styles[i]);
			      renderer.addSeriesRenderer(r);
			    }
			    return renderer;
			  }
		 
		 
		 private Runnable mUpdateTimeTask = new Runnable() {
			   public void run() {
			      addValue();
			     mHandler.postDelayed(this, 100);
			      
			   }
			};
}

	
