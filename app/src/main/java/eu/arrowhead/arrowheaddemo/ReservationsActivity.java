package eu.arrowhead.arrowheaddemo;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import eu.arrowhead.arrowheaddemo.Utility.Networking;
import eu.arrowhead.arrowheaddemo.Utility.PermissionUtils;
import eu.arrowhead.arrowheaddemo.Utility.Utility;
import eu.arrowhead.arrowheaddemo.messages.ChargingResponse;
import eu.arrowhead.arrowheaddemo.messages.Location;
import eu.arrowhead.arrowheaddemo.messages.ReadyForCharge;

import static eu.arrowhead.arrowheaddemo.R.id.map;

public class ReservationsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        UserInputFragment.UserIdDialogListener,
        TimePickerFragment.TimePickerListener,
        ReadyToChargeFragment.ReadyToChargeListener {

    private SharedPreferences prefs;
    private Location appointedChargerLocation;
    private String userId, EVId;
    private GoogleMap mMap;

    private static String BASE_URL = "something";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        //Setting up the user input FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.user_input_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new UserInputFragment();
                newFragment.show(getSupportFragmentManager(), UserInputFragment.TAG);
            }
        });

        //Setting up the "Reserve Charging" button
        Button reserveCharging = (Button) findViewById(R.id.reserve_charging_button);
        reserveCharging.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isConnected(ReservationsActivity.this)){
                    Toast.makeText(ReservationsActivity.this, R.string.no_internet_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    userId = prefs.getString("userId", null);
                    EVId = prefs.getString("EVId", null);

                    if(userId == null || userId.isEmpty()){
                        Toast.makeText(ReservationsActivity.this, R.string.no_user_id_warning, Toast.LENGTH_LONG).show();
                    }
                    else if(EVId == null || EVId.isEmpty()){
                        Toast.makeText(ReservationsActivity.this, R.string.no_ev_id_warning, Toast.LENGTH_LONG).show();
                    }
                    else{
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
                    }
                }
            }
        });

        Button readyToCharge = (Button) findViewById(R.id.ready_to_charge_button);
        readyToCharge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utility.isConnected(ReservationsActivity.this)){
                    Toast.makeText(ReservationsActivity.this, R.string.no_internet_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    DialogFragment newFragment = new ReadyToChargeFragment();
                    newFragment.show(getSupportFragmentManager(), ReadyToChargeFragment.TAG);
                }
            }
        });

        prefs = this.getSharedPreferences("eu.arrowhead.arrowheaddemo", Context.MODE_PRIVATE);
        boolean isThereReservation = prefs.getBoolean("isThereReservation ", false);
        if(isThereReservation){
            reserveCharging.setEnabled(false);
            readyToCharge.setEnabled(true);
        }
        else{
            reserveCharging.setEnabled(true);
            readyToCharge.setEnabled(false);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*//Move the camera to the venue where the demo will be presented
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.782391, 11.250345)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        //Placing 2 markers on the map, representing the 2 charging stations
        LatLng chargingStation1 = new LatLng(43.778428, 11.250622);
        LatLng chargingStation2 = new LatLng(43.786662, 11.250310);
        googleMap.addMarker(new MarkerOptions().position(chargingStation1).title("Charging station 1"));
        googleMap.addMarker(new MarkerOptions().position(chargingStation2).title("Charging Station 2"));
        googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);*/

        mMap = googleMap;
        enableMyLocation();

        boolean isThereReservation = prefs.getBoolean("isThereReservation", false);
        if(isThereReservation){
            double latitude = Double.longBitsToDouble(prefs.getLong("latitude", 0));
            double longitude = Double.longBitsToDouble(prefs.getLong("longitude", 0));
            LatLng chargingStation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    //Callback methods for the UserInputFragment
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

        //Show a "Saved" toast if we actually saved at least one input
        if(!userId.getText().toString().isEmpty() || !EVId.getText().toString().isEmpty()){
            Toast.makeText(ReservationsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    //Callback method for the TimePickerFragment
    //Here we send the charging request with all the necessary information
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String latestStopTime = Utility.createLatestStopTime(hourOfDay, minute);
        JSONObject requestPayload = null;
        try {
            requestPayload = compileChargingRequestPayload(latestStopTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, BASE_URL, requestPayload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response){
                                ChargingResponse chargingResponse = Utility.fromJsonObject(response.toString(), ChargingResponse.class);
                                prefs.edit().putString("chargingReqId", chargingResponse.getChargingReqId()).apply();
                                ChargingResponseFragment newFragment =
                                        ChargingResponseFragment.newInstance(chargingResponse.getChargingReqId(), chargingResponse.getStatus());
                                newFragment.show(getSupportFragmentManager(), ChargingResponseFragment.TAG);

                                double latitude = chargingResponse.getChargerLocation().getLatitude();
                                double longitude = chargingResponse.getChargerLocation().getLongitude();
                                LatLng chargingStation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
                                Toast.makeText(ReservationsActivity.this, R.string.charging_station_displayed, Toast.LENGTH_LONG).show();

                                prefs.edit().putLong("latitude", Double.doubleToRawLongBits(latitude)).apply();
                                prefs.edit().putLong("longitude", Double.doubleToRawLongBits(longitude)).apply();
                            }},
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ReservationsActivity.this,
                                        "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }}
                );
        //Networking.getInstance(ReservationsActivity.this).addToRequestQueue(jsObjRequest);
        Toast.makeText(ReservationsActivity.this, R.string.request_sent, Toast.LENGTH_SHORT).show();

        //TODO remove the test fragment when BASE_URL is known
        String status = "Accepted";
        String id = "c46as-asd54-asd54-asd45-asd645";
        ChargingResponseFragment newFragment = ChargingResponseFragment.newInstance(id, status);
        newFragment.show(getSupportFragmentManager(), ChargingResponseFragment.TAG);
        LatLng chargingStation = new LatLng(47.372290, 19.525751);
        mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
        Toast.makeText(ReservationsActivity.this, R.string.charging_station_displayed, Toast.LENGTH_LONG).show();
    }

    public JSONObject compileChargingRequestPayload(String latestStopTime) throws JSONException {
        android.location.Location myLocation = mMap.getMyLocation();
        JSONObject location = new JSONObject();
        location.put("longitude", myLocation.getLongitude());
        location.put("latitude", myLocation.getLatitude());

        JSONObject chargingRequest = new JSONObject();
        chargingRequest.put("userId", userId);
        chargingRequest.put("EVId", EVId);
        chargingRequest.put("latestStopTime", latestStopTime);
        chargingRequest.put("location", location);
        return chargingRequest;
    }


    //Callback methods for the ReadyToChargeFragment
    @Override
    public void onDialogOkClick(DialogFragment dialog) {
        EditText currentChargeText = (EditText) dialog.getDialog().findViewById(R.id.current_charge_edittext);
        EditText minTargetText = (EditText) dialog.getDialog().findViewById(R.id.min_charge_target_edittext);

        if(currentChargeText.getText().toString().isEmpty() || minTargetText.getText().toString().isEmpty()){
            Toast.makeText(ReservationsActivity.this, R.string.empty_imput_field_warning, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            dialog.show(getSupportFragmentManager(), ReadyToChargeFragment.TAG);
        }
        else{
            double currentCharge = Double.parseDouble(currentChargeText.getText().toString());
            double minTarget = Double.parseDouble(minTargetText.getText().toString());
            if(currentCharge > 100.0 || minTarget > 100.0){
                Toast.makeText(ReservationsActivity.this, R.string.high_values_warning, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                dialog.show(getSupportFragmentManager(), ReadyToChargeFragment.TAG);
            }
            else if(currentCharge > minTarget){
                Toast.makeText(ReservationsActivity.this, R.string.min_charge_target_warning, Toast.LENGTH_LONG).show();
                dialog.dismiss();
                dialog.show(getSupportFragmentManager(), ReadyToChargeFragment.TAG);
            }
            else{
                String chargingReqId = prefs.getString("chargingReqId", null);
                String URL = BASE_URL + "/" + chargingReqId;
                JSONObject requestPayload = null;
                try {
                    requestPayload = compileReadyToChargePayload(chargingReqId, currentCharge, minTarget);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, URL, requestPayload,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response){
                                        Toast.makeText(ReservationsActivity.this, R.string.cpms_accepted_request, Toast.LENGTH_SHORT).show();
                                    }},
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(ReservationsActivity.this,
                                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                    }}
                        );
                //Networking.getInstance(ReservationsActivity.this).addToRequestQueue(jsObjRequest);

                //TODO remove this when BASE_URL is known
                Toast.makeText(ReservationsActivity.this, R.string.cpms_accepted_request, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    public JSONObject compileReadyToChargePayload(String chargingReqId, double currentCharge, double minTarget) throws JSONException {
        JSONObject stateOfCharge = new JSONObject();
        stateOfCharge.put("current", currentCharge);
        stateOfCharge.put("minTarget", minTarget);

        JSONObject readyToCharge = new JSONObject();
        readyToCharge.put("chargingReqId", chargingReqId);
        readyToCharge.put("stateOfCharge", stateOfCharge);
        return readyToCharge;
    }
}
