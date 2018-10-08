package com.example.ashwin.pgrs;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class newComplaintActivity extends AppCompatActivity implements OnMapReadyCallback{

    EditText details,date;
    Spinner spinner,type_Spinner;
    DatabaseReference db;
    String photoPath;
    GoogleMap googleMap;
    ProgressBar progressBar;
    EditText editText;
    FirebaseUser currentUser;
    static final int REQUEST_TAKE_PHOTO = 1;
    DatabaseReference myRef;
    Button submitComplaint,getLoc,capturePhoto;
    Calendar myCalender;
    String type;
    StorageReference storageReference;
    String mCurrentPhotoPath ="";
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
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_id);
        supportMapFragment.getMapAsync(newComplaintActivity.this);
        spinner = findViewById(R.id.spinner_item);
        progressBar = findViewById(R.id.progress_upload_id);
        type_Spinner = findViewById(R.id.spinner_item_type);
        getLoc = findViewById(R.id.get_loc_id);
        details = findViewById(R.id.dialog_details_id);
        date = findViewById(R.id.dialog_date_id);
        editText = findViewById(R.id.desc_id);
        capturePhoto = findViewById(R.id.capture_photo);
        myRef = FirebaseDatabase.getInstance().getReference();
        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(newComplaintActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(newComplaintActivity.this,android.R.layout.simple_spinner_dropdown_item,fields);
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
                progressBar.setVisibility(View.VISIBLE);
                final String authority = "None";
                final String date_entered = date.getText().toString();
                final String details_entered = details.getText().toString();
                final String status = "Submitted";
                final String type_selected = type;
                final String desc = editText.getText().toString();
                final String key = myRef.push().getKey();
                storageReference = FirebaseStorage.getInstance().getReference(key);
                UploadTask uploadTask = storageReference.putFile(Uri.fromFile(new File(mCurrentPhotoPath)));
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();
                        photoPath = downloadUri.toString();
                        //Toast.makeText(newComplaintActivity.this,photoPath,Toast.LENGTH_SHORT).show();
                        int upvoted = 1;
                        Complaints cs = new Complaints(photoPath,department,desc,authority,date_entered,details_entered,status,type_selected,upvoted,mAuth.getCurrentUser().getEmail(),lat,longitude);
                        db.child(key).setValue(cs);
                        progressBar.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(newComplaintActivity.this);
                        builder.setMessage("Complaint successfully registered.\n\nWe hope it will be resolved soon!").setTitle("Confirmation");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(newComplaintActivity.this,SelectActivity.class));
                                finish();
                            }
                        });
                        builder.show();
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(newComplaintActivity.this,SelectActivity.class));
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ashwin.pgrs.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:startActivity(new Intent(newComplaintActivity.this,SelectActivity.class));finish();return true;
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
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longitude),14.0f));
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,longitude)).title("My location"));
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
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
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
}
