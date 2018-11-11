package com.example.dadac.testrosbridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dadac.testrosbridge.RosBridgeActivity;

public class MainActivity extends AppCompatActivity {

    private Button DC_Button_JumpToRos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DC_Button_JumpToRos = (Button) findViewById(R.id.DC_Button_JumpToRos);
    }


    public void JumpToActivity(View view) {
        Intent myIntentRos = new Intent(MainActivity.this, RosBridgeActivity.class);
        startActivity(myIntentRos);
    }
}
//