package com.example.ashwin.pgrs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ComplaintsTechViewHolder extends RecyclerView.ViewHolder
{
    TextView details, date,type,department;
    Button markAsDone;

    public ComplaintsTechViewHolder(View itemView)
    {
        super(itemView);
        department = itemView.findViewById(R.id.dept_id_result);
        date = itemView.findViewById(R.id.date_id_result);
        details = itemView.findViewById(R.id.details_id_result);
        type = itemView.findViewById(R.id.type_id_result);
        markAsDone = itemView.findViewById(R.id.mark_button_id);
    }
}
