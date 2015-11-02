package iiits_robot.robocontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    private final String DEBUG="LoginActivity";
    private String Username;
    private String Password;
    private EditText getUsername;
    private EditText getPassword;
    public Button login_button;
    private String auth_usr="Robot", auth_pass="1234";
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button=(Button)findViewById(R.id.login_button);
        getUsername=(EditText)findViewById(R.id.usr_name);
        getPassword=(EditText)findViewById(R.id.pass_word);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Username=getUsername.getText().toString();
                Password=getPassword.getText().toString();

                if(Username.equals(auth_usr) && Password.equals(auth_pass))
                {
                    Context context=getApplicationContext();
                    Toast toast=Toast.makeText(context,"Welcome "+Username,Toast.LENGTH_LONG);
                    toast.show();

                    intent=new Intent(context,MainActivity.class);
                    startActivity(intent);
                    Log.d(DEBUG,"Started MainActivity");
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
