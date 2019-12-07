package com.example.cshelp;


import androidx.appcompat.app.AppCompatActivity;

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
        refresh();
        // update estimated wait time
    }

    public void refresh() {
        refreshCount();
        //refreshEstimatedTime();
    }

    public void refreshCount() {
        // READ CAPABILITY
        DatabaseReference countChildRef = database.child("count");
        countChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currentCount = ((Long) dataSnapshot.getValue()).intValue();
                TextView countView = findViewById(R.id.numStudentsText);
                countView.setText(Integer.toString(currentCount));
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
        database.child("count").setValue(count++);
        USER_CHECKED_IN = false;
        Log.i("checkOut Button", "The button for checking out was clicked.");
        // web request to server
        updateCountToServer(false);
        updateUi();
    }

    public void updateCountToServer(final boolean increase) {
        DatabaseReference countChildRef = database.child("count");
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
