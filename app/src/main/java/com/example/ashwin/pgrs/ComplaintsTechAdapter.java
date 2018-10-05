package com.example.ashwin.pgrs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ComplaintsTechAdapter extends RecyclerView.Adapter<ComplaintsTechViewHolder>
{
    Context c;
    ArrayList<Complaints> ar;

    public ComplaintsTechAdapter(Context c,ArrayList<Complaints> ar)
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
    public ComplaintsTechViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        View v = LayoutInflater.from(c).inflate(R.layout.complaints_tech_list_item,parent,false);
        return new ComplaintsTechViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ComplaintsTechViewHolder holder, int position) {
        holder.details.setText(ar.get(position).getDetails());
        holder.date.setText(ar.get(position).getDate());
        holder.department.setText(ar.get(position).getDept());
        holder.type.setText(ar.get(position).getType());
        holder.markAsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.markAsDone.setEnabled(false);
                Toast.makeText(c,"Marked as done!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return ar.size();
    }
}
