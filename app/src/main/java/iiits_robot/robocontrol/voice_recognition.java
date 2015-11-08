package iiits_robot.robocontrol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import iiits_robot.robocontrol.R;

public class voice_recognition extends ActionBarActivity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Bluetooth bt;
    private String TAG="voice_recognition";
    int commandid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        commandid=0;

        // hide the action bar
        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        bt=new Bluetooth(getApplicationContext(),mHandler);

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Log.d( TAG,Integer.toString(commandid+1)+"prompt_speech");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //Iterator <String> it=result.iterator();
                    String y="";

                    /*while(it.hasNext())
                    {
                        y=y+it.next();
                    }*/
                    y=result.get(0);
                    txtSpeechInput.setText(y);
                    commandid++;
                    Log.d(TAG,Integer.toString(commandid));
                    bt.sendMessage(commandid+" "+y);
                    //Log.d("voice",y);

                    if(bt.getState()==3) {
                        //Log.d("hhjj","state");

                        if(y.indexOf("disconnect")>=0) {
                            bt.disconnect();
                            Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_SHORT).show();
                        }
                        else if(y.indexOf("connect")>=0)
                        {
                            connectService();
                        }
                    }
                    else if(y.indexOf("disconnect")>=0)
                    {
                        Toast t=Toast.makeText(getApplicationContext(),"already disconnected",Toast.LENGTH_SHORT);
                        t.show();
                    }
                    else if(y.indexOf("connect")>=0)
                    {
                        connectService();
                    }


                }
                break;
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voice_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
