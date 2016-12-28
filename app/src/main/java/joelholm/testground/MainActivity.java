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

    //Stuff from engineering project website
    private UsbManager usbManager;
    private UsbDevice deviceFound;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterfaceFound = null;
    private UsbEndpoint endpointOut = null;
    private UsbEndpoint endpointIn = null;
    SeekBar bar;
    ToggleButton buttonLed;

    //Variables for controls
    boolean up = false,down = false,counter = false,clock = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Engineering website stuff
        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);





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
                    //sound.startTone(ToneGenerator.TONE_DTMF_3, 500);
                    up = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    up = false;
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
                    down = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    down = false;
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
                    counter = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    counter = false;
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
                    clock = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Stop
                    clock = false;
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
        //Testing x accelerometer
        //Log.d("T", String.valueOf(event.values[0]));

        int x,y;
        x = 30 + (int)(event.values[0] * 20);
        y = 71 + (int)(event.values[1] * 20);

        sendCommand(x);
        sendCommand(y);

        if ( up == true ){
            Log.d("T","up");
            sendCommand(1);
        }
        if ( down == true ){
            Log.d("T","down");
            sendCommand(2);
        }
        if ( counter == true ){
            Log.d("T","counter");
            sendCommand(4);
        }
        if ( clock == true ){
            Log.d("T","clock");
            sendCommand(3);
        }



    }




    //Functions from http://www.theengineeringprojects.com/2015/10/usb-communication-between-android-and-arduino.html
    //Then use sendCommand(' '); to send a char value, or int, idk, to an arduino board.

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (deviceFound != null && deviceFound.equals(device)) {
                setDevice(null);
            }
        }
    }

    private void setDevice(UsbDevice device) {
        usbInterfaceFound = null;
        endpointOut = null;
        endpointIn = null;

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbif = device.getInterface(i);

            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;

            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                            tOut = usbif.getEndpoint(j);
                        } else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                            tIn = usbif.getEndpoint(j);
                        }
                    }
                }

                if (tOut != null && tIn != null) {
                    // This interface have both USB_DIR_OUT
                    // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                    usbInterfaceFound = usbif;
                    endpointOut = tOut;
                    endpointIn = tIn;
                }
            }

        }

        if (usbInterfaceFound == null) {
            return;
        }

        deviceFound = device;

        if (device != null) {
            UsbDeviceConnection connection =
                    usbManager.openDevice(device);
            if (connection != null &&
                    connection.claimInterface(usbInterfaceFound, true)) {
                usbDeviceConnection = connection;
                Thread thread = new Thread();
                thread.start();

            } else {
                usbDeviceConnection = null;
            }
        }
    }

    private void sendCommand(int control) {
        synchronized (this) {

            if (usbDeviceConnection != null) {
                byte[] message = new byte[1];
                message[0] = (byte)control;
                usbDeviceConnection.bulkTransfer(endpointOut,
                        message, message.length, 0);
            }
        }
    }

    //@Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        UsbRequest request = new UsbRequest();
        request.initialize(usbDeviceConnection, endpointIn);
        while (true) {
            request.queue(buffer, 1);
            if (usbDeviceConnection.requestWait() == request) {
                byte rxCmd = buffer.get(0);
                if(rxCmd!=0){
                    bar.setProgress((int)rxCmd);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                break;
            }
        }

    }

}
