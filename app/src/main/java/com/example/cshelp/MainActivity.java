package com.example.cshelp;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    boolean USER_CHECKED_IN;
    private DatabaseReference database;
    DatabaseReference countChildRef;

    Button checkInButton;
    Button checkOutButton;
    TextView countView;
    TextView timeView;
    int countLocalStore;


    List<int[]> dataPoints;
    WeightedObservedPoints observedPoints;
    PolynomialCurveFitter curveFitModel;
    double[] fittedCoefficients;

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

        // loads up dataPoints ArrayList from csv from res/raw
        loadData();
        // populates fittedCoefficients double array
        fitModel();
        Log.i("FITTED MODEL",fittedCoefficients[0] + " + " + fittedCoefficients[1] + "*X" + " + " + fittedCoefficients[2] + "*X^2");
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



    private void loadData() {
        dataPoints = new ArrayList<int[]>();

        InputStream is = getResources().openRawResource(R.raw.timedata);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        int currentObservationStudents;
        int currentObservationTime;

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");

                try {
                    currentObservationStudents = Integer.parseInt(tokens[0].trim());
                    currentObservationTime = Integer.parseInt(tokens[1].trim());
                } catch (NumberFormatException nfe) {
                    currentObservationStudents = -1;
                    currentObservationTime = -1;
                }

                if (currentObservationStudents >= 0 && currentObservationTime >= 0) {
                    int[] currentDataPoint = {currentObservationStudents, currentObservationTime};
                    dataPoints.add(currentDataPoint);
                    Log.i("MainActivity" ,"Just loaded " + currentObservationStudents + ", " + currentObservationTime);
                }
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
    }


    public void fitModel() {
        observedPoints = new WeightedObservedPoints();
        for (int[] dataPoint : dataPoints) {
            observedPoints.add(dataPoint[0], dataPoint[1]);
        }
        curveFitModel = PolynomialCurveFitter.create(2);
        fittedCoefficients = curveFitModel.fit(observedPoints.toList());
    }


    public void refreshEstimatedTime() {

        double a0 = fittedCoefficients[0];
        double a1 = fittedCoefficients[1];
        double a2 = fittedCoefficients[2];

        double raw_y = a0 + a1 * countLocalStore + a2 * Math.pow(countLocalStore,2);
        Log.i("predicted time: ", Double.toString(raw_y));
        int roundedIntTime = (int) Math.round(raw_y);

        timeView.setText(Integer.toString(roundedIntTime) + " minutes");
        if (roundedIntTime >= 15) {
            timeView.setTextColor(Color.RED);
        } else if (roundedIntTime < 15 && roundedIntTime > 9) {
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
