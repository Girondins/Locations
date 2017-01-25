package girondins.locations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Girondins on 10/10/15.
 */
public class LocationActivity extends Activity {
    private Controller cont;
    private String user;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MapFragment mapFragment;
    private GoogleMap map;
    private String id;
    private TextView groupName;
    private Button cr8grp;
    private Button joingrp;
    private Button leaveBtn;
    private int count = 0;
    private Timer time;
    private double latitude;
    private double longitude;
    private Group[] groups;
    private ArrayList<Group> gr = new ArrayList<>();
    private String provider;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        init(savedInstanceState);
        time = new Timer();
        time.schedule(new Update(), 0, 10000);
    }

    public void init(Bundle savedState){
        Intent i = getIntent();
        user = i.getCharSequenceExtra("user").toString();
        cont = new Controller(this,user,savedState);
        cont.setMapActivity(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocList();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                10, locationListener);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        latitude = location.getLatitude();
        longitude = location.getLongitude();





        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OMRC());
        cr8grp = (Button) findViewById(R.id.cr8btn);
        joingrp = (Button) findViewById(R.id.joinBtn);
        leaveBtn = (Button) findViewById(R.id.leaveBtn);
        groupName = (TextView) findViewById(R.id.nameOfGroup);
        cr8grp.setOnClickListener(new CreateClick());
        joingrp.setOnClickListener(new JoinClick());
        leaveBtn.setOnClickListener(new LeaveClick());
    }

    public void addMarkers(Group groupPosition){
        Member member;
        map.clear();
        for(int i=0; i<groupPosition.memberSize();i++) {
            member = groupPosition.getMemberIndex(i);
            if(member.getLongitude() != null && member.getLatitude() != null) {
                Log.d(member.getName(), "Lat " + member.getLatitude() + "Long " + member.getLongitude());
                addMarker(new LatLng(Double.parseDouble(member.getLatitude()), Double.parseDouble(member.getLongitude())), groupPosition.getMemberIndex(i).getName());
            }
        }
    }

    public void createMarker(String member, String longitude, String latitude){
        MarkerOptions mo = new MarkerOptions().position(new LatLng(Double.parseDouble(longitude),Double.parseDouble(latitude))).title(member);
        map.addMarker(mo);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        cont.onSavedState(outState);
        super.onSaveInstanceState(outState);
    }

    private class OMRC implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
        }
    }

    private void addMarker(LatLng latLng,String member) {
        MarkerOptions mo = new MarkerOptions().position(latLng).title(member);

        map.addMarker(mo);
        if(count == 0){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        count ++;
        }

    }

    private class LocList implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("UPDATE LOC", location.getLatitude() + " " + location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
           //     addMarker(new LatLng(latitude, longitude),user);
                cont.sendPosition(new LatLng(latitude, longitude));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }



    private class Update extends TimerTask
    {

        @Override
        public void run() {
          //       addMarker(new LatLng(latitude, longitude),user);
            cont.sendPosition(new LatLng(latitude, longitude));
        }
    }

    private class CreateClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CreateDialog create = new CreateDialog();
            create.setController(cont);
            create.show(getFragmentManager(), "CreateDialog");

        }
    }

    private class LeaveClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            groupName.setText("No Group Selected");
            cont.leaveGroup();
        }
    }


    private class JoinClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            cont.getGroups();
        }
    }

    public void showGroup(String grpName){
        groupName.setText(grpName);
    }

    public void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void clearMarker(){
        map.clear();
    }
}
