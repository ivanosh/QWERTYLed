package com.nnmrz.qwertyled;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class QWERTYLed extends Activity implements OnClickListener{
    final static String tag = "AKL LOGS";
	InputStream is;
    String sensorPath = "/sys/class/leds/lcd-backlight/als/value";
    Button startBtn, stopBtn, aboutBtn;
    ToggleButton tBtn;
    int siz;
  
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(tag,"onCreateActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = (Button) findViewById(R.id.start_button);
        stopBtn = (Button) findViewById(R.id.stop_button);
        aboutBtn = (Button) findViewById(R.id.about_button);
        //tBtn = (ToggleButton) findViewById(R.id.toggleButton);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
        
    }
    @Override
    public void onDestroy() {
    	Log.d(tag,"onDestroyActivity");
    	super.onDestroy();
    }
	public void onClick(View v) {
		switch(v.getId())
		{
		/*case R.id.toggleButton:
			if (tBtn.isChecked())
				stopService(new Intent(this, LedControlService.class));
			else
				startService(new Intent(this, LedControlService.class));*/
		case R.id.start_button:
			startService(new Intent(this, LedControlService.class));
			break;
		case R.id.stop_button:
			stopService(new Intent(this, LedControlService.class));
			break;
		case R.id.about_button:
		    startActivity(new Intent(this, AboutActivity.class));
			break;
		}
	}
}