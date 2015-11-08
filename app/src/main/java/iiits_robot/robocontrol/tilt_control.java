package iiits_robot.robocontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import iiits_robot.robocontrol.Bluetooth;

import java.util.Timer;


public class tilt_control extends ActionBarActivity implements SensorEventListener {
    public SensorManager sensorManager;
    public String TAG="Tilt_Control";
    Bluetooth bt;
    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object
    TextView orientation;
    Button calibrate;
    Button start;
    Button dis;
    int calibration_status=0;
    float calx=0f,caly=0f,calz=0f,noise_factorx=2.5f,noise_factory=2.0f;
    int commandid;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_control);

        calibrate=(Button)findViewById(R.id.calib);
        start=(Button)findViewById(R.id.start_ctrl);
        dis=(Button)findViewById(R.id.button2);


        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        orientation=(TextView)findViewById(R.id.orientation);
        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bt.getState()==3)
                    bt.disconnect();
                else
                    Toast.makeText(getApplicationContext(),"Already Disconnected",Toast.LENGTH_SHORT).show();
            }
        });

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibration_status = 1;
                commandid=0;
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calibration_status==1 || calibration_status==2) {
                    calibration_status = 2;
                    connectService();
                }
                else
                {
                    Toast toast;
                    toast=Toast.makeText(getApplicationContext(),"First Calibrate Accelerometer",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
        // add listener. The listener will be HelloAndroid (this) class
        //sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		/*	More sensor speeds (taken from api docs)
		    SENSOR_DELAY_FASTEST get sensor data as fast as possible
		    SENSOR_DELAY_GAME	rate suitable for games
		 	SENSOR_DELAY_NORMAL	rate (default) suitable for screen orientation changes
		*/
        bt=new Bluetooth(this,mHandler);

    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

        // check sensor type

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            //Log.d(TAG,Integer.toString(commandid)+" Tilt Time");

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText("Acceleration(X): " + x);
            yCoor.setText("Acceleration(Y): " + y);
            zCoor.setText("Acceleration(Z): " + z);

            if(calibration_status==2) {
                // assign directions
                float deltax=x-calx;
                float deltay=y-caly;
                int fx=0;
                int fy=0;

                if(Math.abs(deltax)>noise_factorx)
                    fx=1;
                if(Math.abs(deltay)>noise_factory)
                    fy=1;

                if(fx==1 && fy==0) {
                    if (deltax > 0) {
                        //move backward
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 6");
                       // Toast.makeText(getApplicationContext(), "Backward", Toast.LENGTH_SHORT).show();
                        orientation.setText("Backward");

                    } else {
                        //move forward
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 2");
                        //Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show();
                        orientation.setText("Forward");
                    }
                }
                else if(fy==1 && fx==0) {
                    if (deltay > 0) {
                        //turn left right
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 0");
                        //Toast.makeText(getApplicationContext(), "Left Fast", Toast.LENGTH_SHORT).show();
                        orientation.setText("Left Right");

                    } else {
                        //turn left fast
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 4");
                        //Toast.makeText(getApplicationContext(), "Left Fast", Toast.LENGTH_SHORT).show();
                        orientation.setText("Left Fast");

                    }
                }
                else if(fx==1 && fy==1)
                {
                    if(deltax>0 && deltay>0)
                    {
                        //backwards right
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 7");
                        //Toast.makeText(getApplicationContext(), "Backwards right", Toast.LENGTH_SHORT).show();
                        orientation.setText("Backwards right");
                    }
                    else if(deltax>0 && deltay<0)
                    {
                        //backwards left
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 5");
                        //Toast.makeText(getApplicationContext(), "Backwards left", Toast.LENGTH_SHORT).show();
                        orientation.setText("Backwards left");
                    }
                    else if(deltay<0)
                    {
                        //forwards left
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 3");
                        //Toast.makeText(getApplicationContext(), "Forwards left", Toast.LENGTH_SHORT).show();
                        orientation.setText("Forwards left");
                    }
                    else
                    {
                        //forwards right
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" 1");
                        //Toast.makeText(getApplicationContext(), "Forwards right", Toast.LENGTH_SHORT).show();
                        orientation.setText("Forwards right");
                    }
                }

                else
                {
                    //stop
                    commandid++;
                    Log.d(TAG,Integer.toString(commandid));
                    bt.sendMessage(commandid+" 8");
                    orientation.setText("Stop");
                }



            }

            if(calibration_status==1){

                //calibrate
                calx=x;
                caly=y;
                calz=z;
            }
        }
    }

    public void connectService() {
        try {
            //status.setText("Connecting...");
            Toast t=Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT);
            t.show();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                if(bt.getState()!=3) {
                    bt.start();
                    bt.connectDevice("raspberrypi-0");
                    t = Toast.makeText(getApplicationContext(), "Btservice started - listening", Toast.LENGTH_SHORT);
                    t.show();
                    //Log.d(TAG, "Btservice started - listening");
                }
                //status.setText("Connected");
            } else {
                t=Toast.makeText(getApplicationContext(),"Bluetooth not enabled",Toast.LENGTH_SHORT);
                t.show();
                //Log.w(TAG, "Btservice started - bluetooth is not enabled");
                //status.setText("Bluetooth Not enabled");
            }
        } catch (Exception e) {

            Toast t=Toast.makeText(getApplicationContext(),"Unable to start bluetooth",Toast.LENGTH_SHORT);
            t.show();
            Log.e(TAG, "Unable to start bt ", e);
            //status.setText("Unable to connect " + e);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    //Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    //Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    //Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    //Log.d(TAG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    //Log.d(TAG, "MESSAGE_TOAST " + msg);
                    break;
            }
        }
    };
}
