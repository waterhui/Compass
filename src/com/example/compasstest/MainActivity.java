package com.example.compasstest;

import android.R.layout;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener{

	ImageView iv;
	TextView tv;
	SensorManager sensorManager;
	float curDegree = 0f;
	int tv_curDegree = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.compass);
        iv = (ImageView) findViewById(R.id.iv_compass);
        tv = (TextView) findViewById(R.id.tv_compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
        //透明导航栏  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	sensorManager.registerListener (
    			this, 
    			sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
    			SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop() {
    	sensorManager.unregisterListener(this);
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	sensorManager.unregisterListener(this);
    	super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		switch (sensorType) {
			case Sensor.TYPE_ORIENTATION:
				float degree = event.values[0];
		//		Log.i("xxx",Float.toString(degree));
				//该动画绕着图片的中心点,从curDegree转到-degree位置
				RotateAnimation ra = new RotateAnimation (
						curDegree, 
						-degree, 
						RotateAnimation.RELATIVE_TO_SELF, 	//绕着自身的X轴坐标
						0.5f, 	//X轴的一半
						RotateAnimation.RELATIVE_TO_SELF, 	//绕着自身的Y轴坐标
						0.5f); 	//Y轴的一半
				ra.setDuration(300);
				iv.startAnimation(ra);
				curDegree = -degree;

				String location = getLocation((int)degree);
				if(tv_curDegree != (int)degree) {
					tv_curDegree = (int)degree;
					tv.setText( location + " " + tv_curDegree + "°");
				}
				break;
				
			default:
				break;
		}
	}
	
	private int getLocationNum(int degree) {
		/* (0 2 4 6 8 10 12 14) / 2
		 * 23 - 67	 东北
		 * 68 - 111	 东
		 * 112 - 157 东南
		 * 158 - 201 南
		 * 202 - 247 西南
		 * 248 - 291 西
		 * 292 - 337 西北
		 * 338 - 22	 北
		 */
		int location_num[] = {
				23, 67,
				68, 111,
				112, 157,
				158, 201,
				202, 247,
				248, 291,
				292, 337,
				338, 22
		};
		for (int i = 0; i < location_num.length - 2; i += 2) {
			if(degree >= location_num[i] && degree <= location_num[i + 1]) {
				return i / 2;
			}
		}		
		return location_num.length / 2 - 1;
	}
	private String getLocation(int degree) {
		String location[] = {
				"东北",
				"东",
				"东南",
				"南",
				"西南",
				"西",
				"西北",
				"北"
		};
		int num = getLocationNum(degree);
		
		return location[num];
	}
}
