package com.example.ashwin.pgrs;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;

public class TechnicianDashboardActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Technician technician;
    ComplaintsTechAdapter complaintsAdapter;
    RecyclerView recyclerView;
    String dept;
    Query queryComplaint;
    ArrayList<Complaints> complaints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        mAuth = FirebaseAuth.getInstance();
        complaints = new ArrayList<>();
        recyclerView = findViewById(R.id.tech_rv_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(TechnicianDashboardActivity.this));
        complaintsAdapter = new ComplaintsTechAdapter(TechnicianDashboardActivity.this,complaints);
        recyclerView.setAdapter(complaintsAdapter);
        progressBar = findViewById(R.id.technician_progress);
        getSupportActionBar().setTitle("Assigned Tasks");
        Query query = FirebaseDatabase.getInstance().getReference("Technicians").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        query.addValueEventListener(valueEventListener);
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            progressBar.setVisibility(GONE);
            complaints.clear();
            for(DataSnapshot ds:dataSnapshot.getChildren())
            {
                technician = ds.getValue(Technician.class);
                dept = technician.getDepartment();
            }
            queryComplaint = FirebaseDatabase.getInstance().getReference().orderByChild("dept").equalTo(dept);
            queryComplaint.addListenerForSingleValueEvent(valueEventListenerComplaint);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener valueEventListenerComplaint = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            progressBar.setVisibility(GONE);
            complaints.clear();
            for(DataSnapshot ds:dataSnapshot.getChildren())
            {
                Complaints c = ds.getValue(Complaints.class);
                if((c.getAuthority().equals(technician.getName()))&&complaints.size()<5)
                {
                    complaints.add(c);
                }
                else if(complaints.size()>5)
                {
                    queryComplaint.removeEventListener(this);
                }
            }
            complaintsAdapter.swapItems(complaints);
            Collections.reverse(complaints);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signout_button:mAuth.signOut();startActivity(new Intent(TechnicianDashboardActivity.this,LoginActivity.class));
            finish();return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        menu.findItem(R.id.add_complaint).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(TechnicianDashboardActivity.this,"Click on sign out",Toast.LENGTH_SHORT).show();
    }
}
