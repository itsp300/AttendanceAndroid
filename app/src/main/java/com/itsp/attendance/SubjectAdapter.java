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
    private List<Subject> subjectList;
    private Context context;

    SubjectAdapter(List<Subject> subjectList) {
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
        int resourceId = context.getResources().getIdentifier(subject.getThumbnail(), "drawable", context.getPackageName());
        // Check if the drawable was found, else use placeholder one.
        if (resourceId == 0)
        {
            resourceId = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
        }
        holder.subjectThumbnail.setImageResource(resourceId);
        holder.subjectAttendance.setText(Subject.ATTENDANCE_LABEL + subject.getAttendance());
        holder.subjectTotal.setText(Subject.TOTAL_LABEL + subject.getTotal());
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView subjectThumbnail;
        TextView subjectCode;
        TextView subjectAttendance;
        TextView subjectTotal;
        CardView subjectCardView;

        ViewHolder(View itemView)
        {
            super(itemView);
            subjectThumbnail = itemView.findViewById(R.id.subject_thumbnail);
            subjectCode = itemView.findViewById(R.id.subject_code);
            subjectAttendance = itemView.findViewById(R.id.subject_attendance);
            subjectTotal = itemView.findViewById(R.id.subject_total);
            subjectCardView = itemView.findViewById(R.id.subject_card_view);
        }

    }
}
