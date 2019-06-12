package com.itsp.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    List<Subject> subjectList;
    Context context;

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subject,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);

        holder.subjectCode.setText(subject.getCode());
        holder.subjectAttendance.setText(subject.getAttendance());
        holder.subjectTotal.setText(subject.getTotal());
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView subjectCode;
        TextView subjectAttendance;
        TextView subjectTotal;
        CardView subjectCardView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            subjectCode = (TextView)itemView.findViewById(R.id.subject_code);
            subjectAttendance = (TextView)itemView.findViewById(R.id.subject_attendance);
            subjectTotal = (TextView)itemView.findViewById(R.id.subject_total);
            subjectCardView = (CardView)itemView.findViewById(R.id.subject_card_view);
        }

    }
}
