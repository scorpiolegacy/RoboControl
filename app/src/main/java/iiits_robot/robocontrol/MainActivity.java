package iiits_robot.robocontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public ListView menuList;
    public String[] MenuItems={"DTMF","Remote Control","Voice Recognition","Tilt Control","Gesture Control","ShutDown Robot","About"};
    public ArrayAdapter arrayAdapter;
    public Context context;
    private String DEBUG="MainActivity";
    Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=getApplicationContext();
        menuList=(ListView)findViewById(R.id.menu_list);
        arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,MenuItems);
        menuList.setAdapter(arrayAdapter);


        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch ((int) (id)) {
                    case 0:
                        Log.d(DEBUG, "dtmf");
                        connectService();
                        bt.sendMessage("dtmf");
                        bt.disconnect();
                        intent = new Intent(context, dtmf.class);
                        break;
                    case 1:
                        Log.d(DEBUG, "remote_control");
                        intent = new Intent(context, remote_control.class);
                        break;
                    case 2:
                        Log.d(DEBUG, "voice_recognition");
                        intent = new Intent(context, voice_recognition.class);
                        break;
                    case 3:
                        Log.d(DEBUG, "tilt_control");
                        intent = new Intent(context, tilt_control.class);
                        break;
                    case 4:
                        Log.d(DEBUG, "dtmf");
                        intent = new Intent(context, gesture_control.class);
                        break;
                    case 5:
                        Log.d(DEBUG, "Exit");
                        connectService();
                        bt.sendMessage("exit");
                        bt.disconnect();

                    default:
                        Log.d(DEBUG, "default");
                }

                startActivity(intent);
            }
        });

        bt=new Bluetooth(getApplicationContext(),mHandler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                    Log.d(DEBUG, "Btservice started - listening");
                }
                //status.setText("Connected");
            } else {
                t=Toast.makeText(getApplicationContext(),"Bluetooth not enabled",Toast.LENGTH_SHORT);
                t.show();
                Log.w(DEBUG, "Btservice started - bluetooth is not enabled");
                //status.setText("Bluetooth Not enabled");
            }
        } catch (Exception e) {

            Toast t=Toast.makeText(getApplicationContext(),"Unable to start bluetooth",Toast.LENGTH_SHORT);
            t.show();
            Log.e(DEBUG, "Unable to start bt ", e);
            //status.setText("Unable to connect " + e);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(DEBUG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(DEBUG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(DEBUG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(DEBUG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(DEBUG, "MESSAGE_TOAST " + msg);
                    break;
            }
        }
    };
}
