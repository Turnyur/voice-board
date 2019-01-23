package info.androidhive.dano_notice_board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends Activity {
    EditText username,mypassword;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username= (EditText) findViewById(R.id.user_name);
        mypassword= (EditText) findViewById(R.id.pass_word);
        login_btn= (Button) findViewById(R.id.button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_check=username.getText().toString();
                String pass_check=mypassword.getText().toString();
                if((user_check.equals("Johnpaul")||user_check.equals("johnpaul")) && pass_check.equals("1234")){
                    Intent i=new Intent(Login.this,MainActivity.class);
                    startActivity(i);
                    Toast.makeText(getApplicationContext(),
                            "Welcome, "+username.getText().toString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Incorrect username or password", Toast.LENGTH_LONG).show();
                    username.setText("");
                    mypassword.setText("");

                }
            }
        });





    }
}
