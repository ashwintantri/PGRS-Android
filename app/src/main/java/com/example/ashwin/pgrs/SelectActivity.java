package com.example.ashwin.pgrs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SelectActivity extends AppCompatActivity implements OnMapReadyCallback{
    TextView viewComplaint;
    RecyclerView rv;
    DatabaseReference db;
    ComplaintsAdapter complaintsAdapter;
    FirebaseUser currentUser;
    SwipeRefreshLayout mSwipe;
    Query query;
    ViewPager viewPager;
    Marker marker;
    GoogleMap googleMapInit;
    GoogleMap gMap;
    ValueEventListener valueEventListener;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    double lat,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<Complaints> cr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);
        getSupportActionBar().setTitle("Submitted Complaints");
        mAuth = FirebaseAuth.getInstance();
        Bundle mapViewBundle = null;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SelectActivity.this);
        //progressBar = findViewById(R.id.progress_id);
        currentUser = mAuth.getCurrentUser();
        //c = new Complaints();
        final SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.maps_list_id);
        supportMapFragment.getMapAsync(SelectActivity.this);
        //mSwipe = findViewById(R.id.swipe_refresh);
        cr = new ArrayList<>();
        //db = FirebaseDatabase.getInstance().getReference();
        //rv = findViewById(R.id.complaint_recyclerview_id);
        //rv.setLayoutManager(new LinearLayoutManager(SelectActivity.this));
        //complaintsAdapter = new ComplaintsAdapter(SelectActivity.this,cr);
        //rv.setAdapter(complaintsAdapter);
        ActivityCompat.requestPermissions(SelectActivity.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},1);
        query = FirebaseDatabase.getInstance().getReference();
        //startActivity(getIntent());

        //complaintsAdapter.notifyDataSetChanged();
    }



    private double getDistance(double LAT1, double LONG1, double LAT2, double LONG2)
    {
        return 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2)), 2) + Math.cos(LAT2 * (3.14159 / 180)) * Math.cos(LAT1 * (3.14159 / 180)) * Math.sin(Math.pow(((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2), 2))));
    }






    @Override
    protected void onStop() {
        super.onStop();
        query.removeEventListener(valueEventListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 1:
            {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(SelectActivity.this,
                                new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if(location!=null)
                                        {
                                            lat = location.getLatitude();
                                            longitude = location.getLongitude();
                                            Toast.makeText(SelectActivity.this,"Location detected",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on Sign Out", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.add_complaint:startNewComplaintActivity();return true;
            case R.id.signout_button:
                FirebaseAuth.getInstance().signOut();
                startLoginActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }


    public void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startNewComplaintActivity()
    {
        Intent startNewComplaint = new Intent(this, newComplaintActivity.class);
        startActivity(startNewComplaint);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapInit = googleMap;

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //Toast.makeText(SelectActivity.this,"Changed",Toast.LENGTH_SHORT).show();

                //progressBar.setVisibility(View.GONE);
                cr.clear();
                googleMapInit.clear();
                googleMapInit.setMinZoomPreference(16);
                LatLng currentLoc = new LatLng(lat,longitude);
                googleMapInit.addMarker(new MarkerOptions().position(currentLoc).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                googleMapInit.addCircle(new CircleOptions().center(currentLoc).radius(600).strokeColor(0x220000FF)
                        .fillColor(0x220000FF)
                        .strokeWidth(5));
                googleMapInit.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
                for(DataSnapshot complaintSnapshot : dataSnapshot.getChildren())
                {
                    Complaints c = complaintSnapshot.getValue(Complaints.class);
                    if(getDistance(c.getLat(),c.getLongitude(),lat,longitude)<500) {
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longitude),14.0f));
                        googleMapInit.addMarker(new MarkerOptions().position(new LatLng(c.getLat(),c.getLongitude())).title("Department: "+c.getDept()+"\n"+"Details: "+c.getDetails()+"\n"+"Status: "+c.getStatus()+"\n"+"Authority: "+c.getAuthority()));
                        googleMapInit.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(final Marker marker) {
                                Context context = getApplicationContext();
                                LinearLayout info = new LinearLayout(context);
                                info.setOrientation(LinearLayout.VERTICAL);
                                TextView title = new TextView(context);
                                title.setTextColor(Color.BLACK);
                                title.setGravity(Gravity.CENTER);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());
                                Button button = new Button(context);
                                button.setText("Volunteer");
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(SelectActivity.this,"We'll get back to you shortly!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Button button1 = new Button(context);
                                button1.setText("Upvote");
                                button1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(SelectActivity.this,"Upvoted",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                info.addView(title);
                                info.addView(button);
                                info.addView(button1);
                                return info;
                            }
                        });
                        cr.add(c);
                    }
//                    if(cr.size()>4)
//                    {
//                        query.removeEventListener(this);
//                    }
                }
                //Collections.reverse(cr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        query.addValueEventListener(valueEventListener);

    }

}
