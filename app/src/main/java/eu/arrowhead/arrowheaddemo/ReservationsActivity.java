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

public class ReservationsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        TimePickerFragment.TimePickerListener,
        ReadyToChargeFragment.ReadyToChargeListener,
        ServerEndpointFragment.ServerEndpointListener{

    private SharedPreferences prefs;
    private GoogleMap mMap;
    private Marker marker;
    private Button reserveCharging, readyToCharge;

    private static String BASE_URL = "https://echarger.evopro.hu/ocpp-app/EVCharging/";
    private static String RESERVE_URL = BASE_URL + "charging";
    private static String READY_URL = BASE_URL + "readyForCharge";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

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
                DialogFragment newFragment = new UserInputFragment();
                newFragment.show(getSupportFragmentManager(), UserInputFragment.TAG);
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogFragment newFragment = new ServerEndpointFragment();
                newFragment.show(getSupportFragmentManager(), ServerEndpointFragment.TAG);
                return false;
            }
        });

        //Setting up the "Reserve Charging" button
        reserveCharging = (Button) findViewById(R.id.reserve_charging_button);
        reserveCharging.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utility.isConnected(ReservationsActivity.this)){
                    Toast.makeText(ReservationsActivity.this, R.string.no_internet_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    if(prefs.getInt("userIdPos", -1) == -1){
                        Toast.makeText(ReservationsActivity.this, R.string.no_user_id_warning, Toast.LENGTH_LONG).show();
                    }
                    else if(prefs.getInt("evIdPos", -1) == -1){
                        Toast.makeText(ReservationsActivity.this, R.string.no_ev_id_warning, Toast.LENGTH_LONG).show();
                    }
                    else{
                        JSONObject requestPayload = null;
                        try {
                            requestPayload = compileChargingRequestPayload();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, RESERVE_URL, requestPayload,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response){
                                                Log.i("charge_request_response", response.toString());
                                                ChargingResponse chargingResponse = Utility.fromJsonObject(response.toString(), ChargingResponse.class);
                                                if(chargingResponse.getOccpChargePointStatus() == null ||
                                                        !chargingResponse.getOccpChargePointStatus().equals("Rejected")){
                                                    Toast.makeText(ReservationsActivity.this, R.string.cpms_rejected_the_request, Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    prefs.edit().putString("chargingReqId", chargingResponse.getChargingRequestId()).apply();
                                                    ChargingResponseFragment newFragment =
                                                            ChargingResponseFragment.newInstance(chargingResponse.getChargingRequestId(), chargingResponse.getOccpChargePointStatus());
                                                    newFragment.show(getSupportFragmentManager(), ChargingResponseFragment.TAG);

                                                    double latitude = chargingResponse.getChargePointLocation().getLatitude();
                                                    double longitude = chargingResponse.getChargePointLocation().getLongitude();
                                                    LatLng chargingStation = new LatLng(latitude, longitude);
                                                    marker = mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
                                                    Toast.makeText(ReservationsActivity.this, R.string.charging_station_displayed, Toast.LENGTH_LONG).show();

                                                    prefs.edit().putBoolean("isThereReservation", true).apply();
                                                    prefs.edit().putLong("latitude", Double.doubleToRawLongBits(latitude)).apply();
                                                    prefs.edit().putLong("longitude", Double.doubleToRawLongBits(longitude)).apply();

                                                    reserveCharging.setEnabled(false);
                                                    readyToCharge.setEnabled(true);
                                                }
                                            }},
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.i("charge_request_error", error.toString());
                                                Toast.makeText(ReservationsActivity.this,
                                                        "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                            }}
                                );
                        Networking.getInstance(ReservationsActivity.this).addToRequestQueue(jsObjRequest);
                        Toast.makeText(ReservationsActivity.this, R.string.request_sent, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        readyToCharge = (Button) findViewById(R.id.ready_to_charge_button);
        readyToCharge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utility.isConnected(ReservationsActivity.this)){
                    Toast.makeText(ReservationsActivity.this, R.string.no_internet_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
                }
            }
        });

        readyToCharge.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mMap.clear();
                reserveCharging.setEnabled(true);
                readyToCharge.setEnabled(false);
                prefs.edit().putBoolean("isThereReservation", false).apply();
                prefs.edit().remove("userIdPos").apply();
                prefs.edit().remove("evIdPos").apply();
                prefs.edit().remove("evId").apply();
                return false;
            }
        });

        prefs = this.getSharedPreferences("eu.arrowhead.arrowheaddemo", Context.MODE_PRIVATE);
        if(prefs.getBoolean("isThereReservation", false)){
            reserveCharging.setEnabled(false);
            readyToCharge.setEnabled(true);
        }
        else{
            reserveCharging.setEnabled(true);
            readyToCharge.setEnabled(false);
        }

        if(!prefs.getString("base_url", "").isEmpty()){
            BASE_URL = prefs.getString("base_url", "");
        }
    }

    public JSONObject compileChargingRequestPayload() throws JSONException {
        double latitude;
        double longitude;
        try{
            android.location.Location myLocation = mMap.getMyLocation();
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
        }
        catch(NullPointerException ex){
            //Static default values (same as the map center)
            latitude = 43.782391;
            longitude = 11.250345;
        }
        JSONObject location = new JSONObject();
        location.put("latitude", latitude);
        location.put("longitude", longitude);

        String userId;
        switch(prefs.getInt("userIdPos", -1)){
            case 0:
                userId = "43e4baf7";
                break;
            case 1:
                userId = "e136137e";
                break;
            case 2:
                userId = "3df01758";
                break;
            case 3:
                userId = "fe44a1a8";
                break;
            default: userId = "43e4baf7";
        }

        int evIdPos = prefs.getInt("evIdPos", -1);
        String chargerId;
        if(evIdPos == 0 || evIdPos == 1){
            chargerId = "eACC0010";
        }
        else{
            chargerId = "eACC0025";
        }

        JSONObject chargingRequest = new JSONObject();
        chargingRequest.put("userId", userId);
        chargingRequest.put("evId", prefs.getString("evId", "HCS-DA-BEST-007"));
        chargingRequest.put("location", location);
        chargingRequest.put("chargerId", chargerId);
        Log.i("charge_request_payload", chargingRequest.toString());
        return chargingRequest;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*//Placing 2 markers on the map, representing the 2 charging stations
        LatLng chargingStation1 = new LatLng(43.778428, 11.250622);
        LatLng chargingStation2 = new LatLng(43.786662, 11.250310);
        googleMap.addMarker(new MarkerOptions().position(chargingStation1).title("Charging station 1"));
        googleMap.addMarker(new MarkerOptions().position(chargingStation2).title("Charging Station 2"));
        googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);*/

        //Move the camera to the venue where the demo will be presented
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.782391, 11.250345)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
        enableMyLocation();

        if(prefs.getBoolean("isThereReservation", false)){
            double latitude = Double.longBitsToDouble(prefs.getLong("latitude", 0));
            double longitude = Double.longBitsToDouble(prefs.getLong("longitude", 0));
            LatLng chargingStation = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
            mPermissionDenied = false;
        }
    }

    //Callback method for the TimePickerFragment
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String latestStopTime = Utility.createLatestStopTime(hourOfDay, minute);
        prefs.edit().putString("latestStopTime", latestStopTime).apply();

        DialogFragment newFragment = new ReadyToChargeFragment();
        newFragment.show(getSupportFragmentManager(), ReadyToChargeFragment.TAG);
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
                JSONObject requestPayload = null;
                try {
                    requestPayload = compileReadyToChargePayload(currentCharge, minTarget);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, READY_URL, requestPayload,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response){
                                        Log.i("readyto_charge_response", response.toString());
                                        //TODO check the response to show the right toast (+if-else)
                                        Toast.makeText(ReservationsActivity.this, R.string.cpms_accepted_request, Toast.LENGTH_SHORT).show();
                                        reserveCharging.setEnabled(true);
                                        readyToCharge.setEnabled(false);
                                        marker.remove();
                                        prefs.edit().putBoolean("isThereReservation", false).apply();
                                    }},
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("ready_to_charge_error", error.toString());
                                        Toast.makeText(ReservationsActivity.this,
                                                "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                    }}
                        );
                Networking.getInstance(ReservationsActivity.this).addToRequestQueue(jsObjRequest);

                /*//TEST CODE for skipping network call
                Toast.makeText(ReservationsActivity.this, R.string.cpms_accepted_request, Toast.LENGTH_SHORT).show();
                reserveCharging.setEnabled(true);
                readyToCharge.setEnabled(false);
                marker.remove();
                prefs.edit().putBoolean("isThereReservation", false).apply();*/
            }
        }
    }

    public JSONObject compileReadyToChargePayload(double currentCharge, double minTarget) throws JSONException {
        JSONObject stateOfCharge = new JSONObject();
        stateOfCharge.put("current", currentCharge);
        stateOfCharge.put("minTarget", minTarget);

        String chargingReqId = prefs.getString("chargingReqId", "");
        String latestStopTime = prefs.getString("latestStopTime", "");

        JSONObject readyToCharge = new JSONObject();
        readyToCharge.put("chargingRequestId", chargingReqId);
        readyToCharge.put("latestStopTime", latestStopTime);
        readyToCharge.put("stateOfCharge", stateOfCharge);
        Log.i("ready_to_charge_payload", readyToCharge.toString());
        return readyToCharge;
    }

    @Override
    public void onFragmentPositiveClick(DialogFragment dialog) {
        EditText serverEndpoint = (EditText) dialog.getDialog().findViewById(R.id.server_endpoint_edittext);
        if(!serverEndpoint.getText().toString().isEmpty()){
            BASE_URL = serverEndpoint.getText().toString();
            prefs.edit().putString("base_url", serverEndpoint.getText().toString()).apply();
        }
    }

    /*@Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String latestStopTime = Utility.createLatestStopTime(hourOfDay, minute);
        prefs.edit().putString("latestStopTime", latestStopTime).apply();
        JSONObject requestPayload = null;
        try {
            requestPayload = compileChargingRequestPayload();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, RESERVE_URL, requestPayload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response){
                                ChargingResponse chargingResponse = Utility.fromJsonObject(response.toString(), ChargingResponse.class);
                                prefs.edit().putString("chargingReqId", chargingResponse.getChargingRequestId()).apply();
                                ChargingResponseFragment newFragment =
                                        ChargingResponseFragment.newInstance(chargingResponse.getChargingRequestId(), chargingResponse.getOccpChargePointStatus());
                                newFragment.show(getSupportFragmentManager(), ChargingResponseFragment.TAG);

                                double latitude = chargingResponse.getChargePointLocation().getLatitude();
                                double longitude = chargingResponse.getChargePointLocation().getLongitude();
                                LatLng chargingStation = new LatLng(latitude, longitude);
                                marker = mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
                                Toast.makeText(ReservationsActivity.this, R.string.charging_station_displayed, Toast.LENGTH_LONG).show();

                                prefs.edit().putBoolean("isThereReservation", true).apply();
                                prefs.edit().putLong("latitude", Double.doubleToRawLongBits(latitude)).apply();
                                prefs.edit().putLong("longitude", Double.doubleToRawLongBits(longitude)).apply();

                                reserveCharging.setEnabled(false);
                                readyToCharge.setEnabled(true);
                            }},
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ReservationsActivity.this,
                                        "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }}
                );
        Networking.getInstance(ReservationsActivity.this).addToRequestQueue(jsObjRequest);
        Toast.makeText(ReservationsActivity.this, R.string.request_sent, Toast.LENGTH_SHORT).show();

        String status = "Accepted";
        String id = "c46as-asd54-asd54-asd45-asd645";
        prefs.edit().putString("chargingReqId", id).apply();
        ChargingResponseFragment newFragment = ChargingResponseFragment.newInstance(id, status);
        newFragment.show(getSupportFragmentManager(), ChargingResponseFragment.TAG);
        LatLng chargingStation = new LatLng(43.778428, 11.250622);
        marker = mMap.addMarker(new MarkerOptions().position(chargingStation).title("Charging station"));
        Toast.makeText(ReservationsActivity.this, R.string.charging_station_displayed, Toast.LENGTH_LONG).show();
        prefs.edit().putBoolean("isThereReservation", true).apply();
        prefs.edit().putLong("latitude", Double.doubleToRawLongBits(43.778428)).apply();
        prefs.edit().putLong("longitude", Double.doubleToRawLongBits(11.250622)).apply();
        reserveCharging.setEnabled(false);
        readyToCharge.setEnabled(true);
    }*/
}
