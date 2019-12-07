package com.example.cshelp;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    boolean USER_CHECKED_IN;
    private DatabaseReference database;
    DatabaseReference countChildRef;

    Button checkInButton;
    Button checkOutButton;
    TextView countView;
    TextView timeView;
    int countLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();
        countChildRef = database.child("count");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInButton = findViewById(R.id.checkInButton);
        checkOutButton = findViewById(R.id.checkOutButton);

        countView = findViewById(R.id.numStudentsText);
        timeView = findViewById(R.id.etaText);

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

        USER_CHECKED_IN = false;

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

        refresh();
    }

    public void refresh() {
        refreshCount();
    }

    public void refreshEstimatedTime() {
        int timeRaw = countLocalStore * 2 + 5;
        timeView.setText(Integer.toString(timeRaw) + " minutes");
        if (timeRaw >= 30) {
            timeView.setTextColor(Color.RED);
        } else if (timeRaw < 30 && timeRaw > 10) {
            timeView.setTextColor(Color.YELLOW);
        } else {
            timeView.setTextColor(Color.GREEN);
        }
    }

    public void refreshCount() {
        // READ CAPABILITY
        countChildRef = database.child("count");
        countChildRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentCount = ((Long) dataSnapshot.getValue()).intValue();
                Log.i("REFRESH", Integer.toString(currentCount));
                countView.setText(Integer.toString(currentCount));
                countLocalStore = currentCount;
                refreshEstimatedTime();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void checkIn() {
        USER_CHECKED_IN = true;
        Log.i("checkIn Button", "The button for checking in was clicked.");
        // web request to server
        updateCountToServer(true);
        updateUi();
    }

    public void checkOut() {
        USER_CHECKED_IN = false;
        Log.i("checkOut Button", "The button for checking out was clicked.");
        // web request to server
        updateCountToServer(false);
        updateUi();
    }


    public void updateCountToServer(final boolean increase) {
        countChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentCount = ((Long) dataSnapshot.getValue()).intValue();
                if (increase) {
                    database.child("count").setValue(currentCount + 1);
                } else {
                    database.child("count").setValue(currentCount - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
