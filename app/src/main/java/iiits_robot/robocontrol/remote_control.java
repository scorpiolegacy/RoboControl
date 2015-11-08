package iiits_robot.robocontrol;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import iiits_robot.robocontrol.Bluetooth;
import iiits_robot.robocontrol.R;

public class remote_control extends ActionBarActivity {
    public final String TAG = "remote_control";
   // private SeekBar elevation;
    //private TextView debug;
    //private TextView status;
    private Bluetooth bt;
    private int commandid;
    Button dis;


    private static Bitmap imageOriginal, imageScaled;
    private static Matrix matrix;

    private ImageView dialer;
    private int dialerHeight, dialerWidth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        dis=(Button)findViewById(R.id.button);

        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bt.getState()==3)
                    bt.disconnect();
                else
                    Toast.makeText(getApplicationContext(),"Bluetooth not connected",Toast.LENGTH_SHORT);
            }
        });

        // load the image only once
        if (imageOriginal == null) {
            imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.remote_ring);
        }

        // initialize the matrix only once
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            // not needed, you can also post the matrix immediately to restore the old state
            matrix.reset();
        }


        //dialerHeight = dialer.getHeight();
        //dialerWidth = dialer.getWidth();

        dialer = (ImageView) findViewById(R.id.remote_dial);
        ViewTreeObserver vto = dialer.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                dialer.getViewTreeObserver().removeOnPreDrawListener(this);
                dialerHeight = dialer.getMeasuredHeight();
                dialerWidth = dialer.getMeasuredWidth();
                //tv.setText("Height: " + finalHeight + " Width: " + finalWidth);
                return true;
            }
        });
        dialer.setOnTouchListener(new MyOnTouchListener());
        /*dialer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (dialerHeight == 0 || dialerWidth == 0) {
                    dialerHeight = dialer.getHeight();
                    dialerWidth = dialer.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    resize.postScale((float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getWidth(), (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);

                    dialer.setImageBitmap(imageScaled);
                    dialer.setImageMatrix(matrix);
                }
            }
        });*/


        bt = new Bluetooth(this, mHandler);
        commandid=0;
        connectService();
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
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
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

    private class MyOnTouchListener implements View.OnTouchListener {

        private double currAngle;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Log.d(TAG,Integer.toString(commandid + 1)+" Touch Time");

            double x=event.getX();
            double y=event.getY();
            currAngle=getAngle(x,y);

            double boundx=dialerHeight/2d;
            double boundy=dialerWidth/2d;

            x=x-boundx;
            y=boundy-y;

            double C=Math.pow((x/boundx),2d)+Math.pow((y/boundy),2d);

            boundx=0.5*boundx;
            boundy=0.5*boundy;

            double c=Math.pow((x/boundx),2d)+Math.pow((y/boundy),2d);

            if(c<1.0)
            {
                connectService();
            }

            else if(c>1.0 && C<=1.0)
            {
                int angle=((int)(currAngle+22.5))%360;
                angle=angle/45;

                switch(angle)
                {
                    case 0:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"0");
                        break;
                    case 1:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"1");
                        break;
                    case 2:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"2");
                        break;
                    case 3:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"3");
                        break;
                    case 4:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"4");
                        break;
                    case 5:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"5");
                        break;
                    case 6:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"6");
                        break;
                    case 7:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"7");
                        break;
                    case 8:
                        commandid++;
                        Log.d(TAG,Integer.toString(commandid));
                        bt.sendMessage(commandid+" "+"8");
                        break;

                }
            }

            //dialer.setImageMatrix(matrix);

            return true;
        }

    }

    private double getAngle(double x,double y)
    {
        double ax=x-dialerWidth/2d;
        double ay=dialerHeight/2d-y;

        if(ax>=0 && ay>=0)
            return Math.toDegrees(Math.atan(ay/ax));
        else if(ax<0 && ay>=0)
            return 90d+Math.toDegrees(Math.atan(ay/(-ax)));
        else if(ax<0 && ay<0)
            return 180d+Math.toDegrees(Math.atan(ay/ax));
        else
            return 270d+Math.toDegrees(Math.atan(ax/(-ay)));

    }
}

