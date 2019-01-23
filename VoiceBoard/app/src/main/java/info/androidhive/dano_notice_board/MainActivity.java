package info.androidhive.dano_notice_board;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    boolean flag = false;
    String hold_result = "";
    Button sendButton;
    public static boolean decFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


        // hide the action bar
        getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });


    }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
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
     */
    public void delay(int d) {

        for (int counter = 0; counter <= d; counter++) {
            for (int k = 0; k < 1235; k++) ;
            //Do nothing
        }
    }

    private void makeToast() {
        Toast.makeText(getApplicationContext(),
                "Invalid command, Please make a valid command", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    hold_result = result.get(0);


                    txtSpeechInput.setText(hold_result);
                    flag = true;
                    //delay(00);
                    if (!(hold_result.equals("") || hold_result.equals(" "))) {
                        Intent i = new Intent(MainActivity.this, BTCommandActivity.class);
                        i.putExtra("command", hold_result);
                        i.putExtra("decFlag",flag);
                        startActivity(i);
                    } else {
                        makeToast();
                    }
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
