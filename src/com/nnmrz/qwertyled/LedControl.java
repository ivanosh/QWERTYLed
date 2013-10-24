package com.nnmrz.qwertyled;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

public abstract class LedControl {

	final static int LED_OFF = 0;
	final static int LED_ON = 255;
	final static int CRITICAL = 7;
	final static int HARDKEYBOARD_YES = 1;
	final static int HARDKEYBOARD_NO = 2;
	static int HARDKEYBOARD_STATE = HARDKEYBOARD_NO;
	static int LED_STATE = LED_OFF;
	static int ALS_STATE = -1;
	static int alsLevel;
    SensorManager sm = null;
	static InputStream is;
	static InputStreamReader ir;
	Sensor lightSensor;
	static Thread chLight;
	static boolean thrStop = false;
    static String sensorPath = "/sys/class/leds/lcd-backlight/als/value";
    private static final String BRIGHTNESS_FILE = "/sys/class/leds/keyboard-backlight/brightness";
    //static Observer fo;
    int siz;
    public static int getBrightness() throws IOException {
      return (int) getValue(BRIGHTNESS_FILE);
    }
    
    public static float getValue(String val) throws IOException {
		float sensor = -1;
    	is = new FileInputStream(sensorPath);
	    ir = new InputStreamReader(is);
	    
	    char[] buf = new char[20];
	    int siz = ir.read(buf, 0, 20);
	    if (siz > 0)
	    {
	    	sensor = Float.parseFloat(String.copyValueOf(buf, 0, siz).replaceAll(",", "."));
	    }
	    is.close();
	    ir.close();
		return sensor;
    	
    }
    
    public static boolean setKeyLight(int val) throws IOException {
    	Log.d(QWERTYLed.tag, "set keylight to value:" + val);
    	FileOutputStream localFileOutputStream = new FileOutputStream("/sys/class/leds/keyboard-backlight/brightness");
        byte[] arrayOfByte = (val + "\n").getBytes("ASCII");
        localFileOutputStream.write(arrayOfByte);
        localFileOutputStream.close();
        LED_STATE = val;
		return true;
    }
    public static boolean init(Context paramContext) throws IOException
    {
    	int uid = paramContext.getApplicationContext().getApplicationInfo().uid;
        Process lp = Runtime.getRuntime().exec("su");
        OutputStream os = lp.getOutputStream();
        //fo = new Observer(sensorPath);
        DataOutputStream dataos = new DataOutputStream(os);
        String str = "chown " + uid + " " + "/sys/class/leds/keyboard-backlight/brightness" + "\n";
        dataos.writeBytes(str);
        dataos.writeBytes("chmod u+rw /sys/class/leds/keyboard-backlight/brightness\n");
        dataos.writeBytes("exit\n");
        dataos.flush();
        dataos.close();
        os.close();
		return true;
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(QWERTYLed.tag, "Sendor accuracy changed:" + sensor.getName() + ": " + accuracy);
	}

	public void onSensorChanged(SensorEvent event) {
		Log.d(QWERTYLed.tag, "Sensor value changed:" + event.values[0]);
		
	}
	
	public static void qwertyStatSwitcher(int stat) {
		 switch (stat)
		 {
		 case HARDKEYBOARD_NO:
			 thrStop = true;
			 //fo.stopWatching();
			 if(HARDKEYBOARD_STATE != HARDKEYBOARD_NO) {
				 Log.d(QWERTYLed.tag, "Keyboard NO");
				 try {
					 setKeyLight(LED_OFF);
					 } catch (IOException e1) {
						 Log.d(QWERTYLed.tag, e1.toString());
						 }
			 }
			 break;
		 case HARDKEYBOARD_YES:
			 thrStop = false;
			 Log.d(QWERTYLed.tag, "HARDKEYBOARD_YES");
			 getBrightnessCycle();
			 //fo.startWatching();
			 break;
			 }
		 }
	public static int getBrightnessCycle() {
		new Thread(run).start();
		return ALS_STATE;
	}
	static Runnable run = new Runnable() {
		public void run() {
			while(!thrStop)
			{
				ALS_STATE = -1;
				try {
					ALS_STATE = getBrightness();
				} catch (IOException e) {
					e.printStackTrace();
					}
				Log.d(QWERTYLed.tag, "ALS_STATE = " + ALS_STATE);
				if(ALS_STATE < CRITICAL) {
					if(LED_STATE != LED_ON)
					{
						Log.d(QWERTYLed.tag, "LED_ON ");
						 try {
							setKeyLight(LED_ON);
						} catch (IOException e) {
							e.printStackTrace();
							}
					}
				}
				else {
					if(LED_STATE != LED_OFF)
					{
						Log.d(QWERTYLed.tag, "LED_OFF ");
						try {
							setKeyLight(LED_OFF);
						} catch (IOException e) {
							e.printStackTrace();
							}
					}
				}
				SystemClock.sleep(500);
			}
		}
	};	
}
