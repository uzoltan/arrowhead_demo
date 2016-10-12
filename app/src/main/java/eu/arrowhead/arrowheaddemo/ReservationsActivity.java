package eu.arrowhead.arrowheaddemo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eu.arrowhead.arrowheaddemo.messages.ChargingRequest;
import eu.arrowhead.arrowheaddemo.messages.Location;

public class ReservationsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        UserInputDialogFragment.UserIdDialogListener, TimePickerFragment.TimePickerListener {

    private SharedPreferences prefs;
    private Location selectedMarkerLocation;
    private String userId, EVId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Setting up the user input FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.user_input_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new UserInputDialogFragment();
                newFragment.show(getSupportFragmentManager(), UserInputDialogFragment.TAG);
            }
        });

        //Setting up the "Reserve Charging" button
        Button sendRequest = (Button) findViewById(R.id.reserve_charging_button);
        sendRequest.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                userId = prefs.getString("userId", null);
                EVId = prefs.getString("EVId", null);

                if(userId == null || userId.isEmpty()){
                    Toast.makeText(ReservationsActivity.this, R.string.no_user_id_warning, Toast.LENGTH_LONG).show();
                }
                else if(EVId == null || EVId.isEmpty()){
                    Toast.makeText(ReservationsActivity.this, R.string.no_ev_id_warning, Toast.LENGTH_LONG).show();
                }
                else if(selectedMarkerLocation == null){
                    Toast.makeText(ReservationsActivity.this, R.string.no_charging_station_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
                }

            }
        });

        prefs = this.getSharedPreferences("eu.arrowhead.arrowheaddemo", Context.MODE_PRIVATE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Move the camera to the venue where the demo will be presented
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.782391, 11.250345)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        //Placing 2 markers on the map, representing the 2 charging stations
        LatLng chargingStation1 = new LatLng(43.778428, 11.250622);
        LatLng chargingStation2 = new LatLng(43.786662, 11.250310);
        googleMap.addMarker(new MarkerOptions().position(chargingStation1).title("Charging station 1"));
        googleMap.addMarker(new MarkerOptions().position(chargingStation2).title("Charging Station 2"));
        googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    //Callback methods for the UserInputDialog
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText userId = (EditText) dialog.getDialog().findViewById(R.id.user_id_edittext);
        EditText EVId = (EditText) dialog.getDialog().findViewById(R.id.licence_plate_edittext);

        if(!userId.getText().toString().isEmpty()){
            prefs.edit().putString("userId", userId.getText().toString()).apply();
        }
        if(!EVId.getText().toString().isEmpty()){
            prefs.edit().putString("EVId", EVId.getText().toString()).apply();
        }

        Toast.makeText(ReservationsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    //Callback method for a marker click on the map, saving the location
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latlng = marker.getPosition();
        selectedMarkerLocation = new Location(latlng.latitude, latlng.longitude);
        return false;
    }

    //Callback method for the TimePickerDialog
    //Here we send the charging request with all the necessary information
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();

        //User entered a time, which is already happened today so we assume it's on tomorrow
        if(cal.get(Calendar.HOUR_OF_DAY) > hourOfDay ||
                (cal.get(Calendar.HOUR_OF_DAY) == hourOfDay && cal.get(Calendar.MINUTE) > minute)){
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        String time = cal.getTime().toString();
        Log.i("date test", time);
        Date latestStopTime = new Date();
        try {
            latestStopTime = sdf.parse(time);
        } catch (ParseException e) {
            Log.i("date test", "ERROR");
            e.printStackTrace();
        }
        Log.i("date test", latestStopTime.toString());

        sdf.applyPattern("YYYY-MM-dd'T'HH:MM:SSXXX");
        String finalTime = sdf.format(latestStopTime);
        Log.i("date test", finalTime);

        //TODO kiválasztott időre valami sanity check
        ChargingRequest request = compileChargingRequest();

        Toast.makeText(ReservationsActivity.this, "Sending Request...", Toast.LENGTH_LONG).show();
    }

    public ChargingRequest compileChargingRequest(){
        //TODO dátum ebben a formátumban YYYY-MM-DDTHH:MM:SS+HH:MM
        return new ChargingRequest(userId, EVId, null, selectedMarkerLocation);
    }


}
