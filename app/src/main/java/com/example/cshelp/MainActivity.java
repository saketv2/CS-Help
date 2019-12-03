package com.example.cshelp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    boolean USER_CHECKED_IN;

    Button checkInButton;
    Button checkOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInButton = findViewById(R.id.checkInButton);
        checkOutButton = findViewById(R.id.checkOutButton);

        // clicking checkin button will trigger checkIn method
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIn();
            }
        });

        // clicking checkout button will trigger checkOut method
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOut();
            }
        });

        // both buttons are initially invisible
        checkInButton.setVisibility(View.GONE);
        checkOutButton.setVisibility(View.GONE);

        // this will change lol (temporary hardcoding)
        USER_CHECKED_IN = false;

        // will update the UI!
        updateUi();
    }

    public void updateUi() {

        //master check in/out button
        if (USER_CHECKED_IN) {
            checkOutButton.setVisibility(View.VISIBLE);
            checkInButton.setVisibility(View.GONE);
        } else {
            checkInButton.setVisibility(View.VISIBLE);
            checkOutButton.setVisibility(View.GONE);
        }

        // update live count of checked in folks

        // update estimated wait time
    }

    public void checkIn() {
        USER_CHECKED_IN = true;
        // web request to server
        updateUi();
    }

    public void checkOut() {
        USER_CHECKED_IN = false;
        // web request to server
        updateUi();
    }

    public int liveCount() {
        return 0;
    }

    public double estimatedTime() {
        return 0;
    }



}
