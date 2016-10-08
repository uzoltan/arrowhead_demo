package eu.arrowhead.arrowheaddemo;

import android.app.TimePickerDialog;
import android.content.Intent;

import java.util.Calendar;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReservationsActivity extends FragmentActivity implements OnMapReadyCallback, OnItemSelectedListener {

    private GoogleMap mMap;
    private Button setTime, sendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Setting up the car choosing spinner object
        Spinner spinner = (Spinner) findViewById(R.id.car_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.car_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Setting up the "Set Time" button
        setTime = (Button) findViewById(R.id.set_time_button);
        setTime.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tpd = new TimePickerDialog(ReservationsActivity.this, null, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                tpd.show();
            }
        });

        //Setting up the "Send Request" button
        sendRequest = (Button) findViewById(R.id.send_request_button);
        sendRequest.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationsActivity.this, ResponseActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Move the camera to the venue where the demo will be presented
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.782391, 11.250345)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        //A few charging stations in Florence
        LatLng chargingStation1 = new LatLng(43.780349, 11.253357);
        LatLng chargingStation2 = new LatLng(43.783184, 11.254752);
        LatLng chargingStation3 = new LatLng(43.786662, 11.250310);

        mMap.addMarker(new MarkerOptions().position(chargingStation1).title("Charging station 1"));
        mMap.addMarker(new MarkerOptions().position(chargingStation2).title("Charging Station 2"));
        mMap.addMarker(new MarkerOptions().position(chargingStation3).title("Charging Station 3"));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
