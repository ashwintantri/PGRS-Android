package com.example.ashwin.pgrs;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class newComplaintActivity extends AppCompatActivity {

    EditText details,date;
    Spinner spinner,type_Spinner;
    DatabaseReference db;
    FirebaseUser currentUser;
    DatabaseReference myRef;
    Button submitComplaint,getLoc;
    Calendar myCalender;
    String type;
    double lat,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    String department;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_complaint_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register complaint");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(newComplaintActivity.this);
        myCalender = Calendar.getInstance();
        spinner = findViewById(R.id.spinner_item);
        type_Spinner = findViewById(R.id.spinner_item_type);
        getLoc = findViewById(R.id.get_loc_id);
        details = findViewById(R.id.dialog_details_id);
        date = findViewById(R.id.dialog_date_id);
        final DatePickerDialog.OnDateSetListener set_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(newComplaintActivity.this,set_date,myCalender.get(Calendar.YEAR),
                        myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        submitComplaint = findViewById(R.id.dialog_submit_id);
        ArrayList<String> fields = new ArrayList<>();
        fields.add("Select department");
        fields.add("Electricity");
        fields.add("Water");
        ArrayList<String> types = new ArrayList<>();
        types.add("Select type");
        types.add("Community");
        types.add("Personal");
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(newComplaintActivity.this,android.R.layout.simple_spinner_dropdown_item,types);
        type_Spinner.setAdapter(stringArrayAdapter);
        type_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(newComplaintActivity.this,android.R.layout.simple_spinner_dropdown_item,fields);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(newComplaintActivity.this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        });
        submitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authority = "None";
                String date_entered = date.getText().toString();
                String details_entered = details.getText().toString();
                String status = "Submitted";
                String type_selected = type;
                int upvoted = 1;
                Complaints cs = new Complaints(department,authority,date_entered,details_entered,status,type_selected,upvoted,mAuth.getCurrentUser().getEmail(),lat,longitude);
                db.push().setValue(cs);
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT);
                startViewComplaintActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:NavUtils.navigateUpFromSameTask(newComplaintActivity.this);return true;
            case R.id.signout_button:FirebaseAuth.getInstance().signOut();startLoginActivity();return true;


        }
        return super.onOptionsItemSelected(item);

    }

    public void startLoginActivity()
    {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
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
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(newComplaintActivity.this,
                                new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if(location!=null)
                                        {
                                            lat = location.getLatitude();
                                            longitude = location.getLongitude();
                                            Toast.makeText(newComplaintActivity.this,"Location detected",Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        menu.findItem(R.id.add_complaint).setVisible(false);
        return true;
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        date.setText(sdf.format(myCalender.getTime()));
    }
    private void startViewComplaintActivity()
    {
        Intent viewComplaint = new Intent(this,SelectActivity.class);
        startActivity(viewComplaint);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
}
