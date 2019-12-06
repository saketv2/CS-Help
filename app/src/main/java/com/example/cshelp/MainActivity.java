package com.example.cshelp;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    boolean USER_CHECKED_IN;
    private DatabaseReference database;
    private Integer count;
    Button checkInButton;
    Button checkOutButton;
    ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInButton = findViewById(R.id.checkInButton);
        checkOutButton = findViewById(R.id.checkOutButton);
        refreshButton = findViewById(R.id.refreshButton);

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

        // refresh button will call the refresh method
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
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

        // WRITE CAPABILITY
        database.child("count").setValue(321);

        // READ CAPABILITY
        DatabaseReference countChildRef = database.child("count");
        countChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentCount = dataSnapshot.getValue().toString();
                Log.i("SDKJAFHKJ", currentCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        USER_CHECKED_IN = true;
        Log.i("checkIn Button", "The button for checking in was clicked.");
        // web request to server
        updateUi();
    }

    public void checkOut() {
        database.child("count").setValue(count++);
        USER_CHECKED_IN = false;
        Log.i("checkOut Button", "The button for checking out was clicked.");
        // web request to server
        updateUi();
    }

    public void refresh() {
        Log.i("refresh Button", "The button for refreshing was clicked.");
        refreshET(getEstimatedTime());
        refreshLiveCount(getLiveCount());
    }

    public void refreshET(double latestET) {
        // refreshes the estimated time textview
    }

    public void refreshLiveCount(int latestCount) {
        //refreshes the count textview
    }

    public int getLiveCount() {
        return 0;
    }

    public double getEstimatedTime() {
        // will get from server
        return 0;
    }



}
