package joelholm.testground;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.nio.ByteBuffer;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor sensor;
    private SensorManager SM;

    //Stuff from engineering priject website
    private UsbManager usbManager;
    private UsbDevice deviceFound;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterfaceFound = null;
    private UsbEndpoint endpointOut = null;
    private UsbEndpoint endpointIn = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Pause Button setup
        ImageButton pause = (ImageButton)findViewById(R.id.pause_button);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Pop.class));
            }
        });

        //setting up sound
        final ToneGenerator sound;
        sound = new ToneGenerator(AudioManager.STREAM_MUSIC , 20);
        //Up button
        Button upButton = (Button)findViewById(R.id.upButton);
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Start
                    Log.d("T","Start");
                    sound.startTone(ToneGenerator.TONE_DTMF_3, 500);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    Log.d("T","End");
                }
                return false;
            }
        });
        //Down button
        Button downButton = (Button)findViewById(R.id.downButton);
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Start
                    Log.d("T","Start");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    Log.d("T","End");
                }
                return false;
            }
        });
        //Counterclockwise Button
        ImageButton counterClockwise = (ImageButton)findViewById(R.id.counterClockwise);
        counterClockwise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Start
                    Log.d("T","Start");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    Log.d("T","End");
                }
                return false;
            }
        });
        //Clockwise button
        ImageButton clockwise = (ImageButton)findViewById(R.id.clockwise);
        clockwise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Start
                    Log.d("T","Start");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    Log.d("T","End");
                }
                return false;
            }
        });

        //Acceleration Stuff
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, sensor , SensorManager.SENSOR_DELAY_NORMAL);



        //Arm the quadrotor




    }

    //Acceleration Functions
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)  {

    }
    @Override
    public void onSensorChanged(SensorEvent event)  {
        /*
        * Here is where we had the x, y, and z variables (Float values : Range -9.81 to 9.81)
        * Use event.values[0] for x
        * event.values[1] for y
        * and event.values[2] for z
        */




    }


}
