package eu.arrowhead.arrowheaddemo;


import android.content.Intent;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReservationsActivity extends FragmentActivity implements OnMapReadyCallback, UserIdDialogFragment.UserIdDialogListener {

    private com.getbase.floatingactionbutton.FloatingActionButton carFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        com.getbase.floatingactionbutton.FloatingActionButton userIdFab =
                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_user_id_fab);
        userIdFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new UserIdDialogFragment();
                newFragment.show(getSupportFragmentManager(), UserIdDialogFragment.TAG);
            }
        });

        //Setting up the "Reserve Charging" button
        Button sendRequest = (Button) findViewById(R.id.reserve_charging_button);
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

        //Move the camera to the venue where the demo will be presented
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.782391, 11.250345)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        //A few charging stations in Florence
        LatLng chargingStation1 = new LatLng(43.780349, 11.253357);
        LatLng chargingStation2 = new LatLng(43.783184, 11.254752);
        LatLng chargingStation3 = new LatLng(43.786662, 11.250310);

        googleMap.addMarker(new MarkerOptions().position(chargingStation1).title("Charging station 1"));
        googleMap.addMarker(new MarkerOptions().position(chargingStation2).title("Charging Station 2"));
        googleMap.addMarker(new MarkerOptions().position(chargingStation3).title("Charging Station 3"));
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Toast.makeText(ReservationsActivity.this, "Yaay it works", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
