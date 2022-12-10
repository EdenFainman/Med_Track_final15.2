package com.edentomer.med_track_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //set the title
        getSupportActionBar().setTitle("MedTrack");
        // open sign up activity
        Button buttonSignup=findViewById(R.id.firstPage_Signup_btn); //בלחיצה על כפתור sign up נעביר לדף main activity 3
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this,MainActivity3.class);
                startActivity(intent);
            }


        });
        // open login activity
      Button buttonLogin=findViewById(R.id.firstPage_LogIn_btn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(MainActivity2.this,MainActivity4.class);
              startActivity(intent);
          }
      });
    }
}