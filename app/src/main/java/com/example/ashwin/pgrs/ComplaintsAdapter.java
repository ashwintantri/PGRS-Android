package com.example.ashwin.pgrs;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsViewHolder>
{
    Context c;
    ArrayList<Complaints> ar;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public ComplaintsAdapter(Context c,ArrayList<Complaints> ar)
    {
        this.c = c;
        this.ar = ar;
    }
    public void swapItems(ArrayList<Complaints> repoListItems) {
        this.ar = repoListItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ComplaintsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        View v = LayoutInflater.from(c).inflate(R.layout.complaint_list_item,parent,false);
        return new ComplaintsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ComplaintsViewHolder holder, final int position)
    {
        holder.status.setText(ar.get(position).getStatus());
        holder.details.setText(ar.get(position).getDetails());
        holder.date.setText(ar.get(position).getDate());
        holder.department.setText(ar.get(position).getDept());
        holder.type.setText(ar.get(position).getType());
        holder.upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.upvoteButton.setEnabled(false);
                Toast.makeText(c,"Upvoted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return ar.size();
    }
}
