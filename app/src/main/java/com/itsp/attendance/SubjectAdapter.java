package com.itsp.attendance;

import android.content.Context;
import android.graphics.*;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    
    private List<Subject> subjectList;
    private Context context;
    
    SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }
    
    public void updateData(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subject, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!subjectList.isEmpty()) {
            Subject subject = subjectList.get(position);
            
            holder.subjectCode.setText(subject.getCode());
            
            try {
                byte[] decodedString = Base64.decode(subject.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.subjectThumbnail.setImageBitmap(decodedByte);
            } catch(IllegalArgumentException e) {
                int resourceID = context.getResources().getIdentifier(subject.getThumbnail(), "drawable", context.getPackageName());
                // Check if the drawable was found, else use placeholder one.
                if(resourceID == 0) {
                    resourceID = context.getResources().getIdentifier("placeholder", "drawable", context.getPackageName());
                }
                holder.subjectThumbnail.setImageResource(resourceID);
            }
            
            holder.subjectAttendance.setText(Subject.ATTENDANCE_LABEL + subject.getAttendance());
            holder.subjectTotal.setText(Subject.TOTAL_LABEL + subject.getTotal());
        }
    }
    
    @Override
    public int getItemCount() {
        return subjectList.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        ImageView subjectThumbnail;
        TextView subjectCode;
        TextView subjectAttendance;
        TextView subjectTotal;
        CardView subjectCardView;
        
        ViewHolder(View itemView) {
            super(itemView);
            subjectThumbnail = itemView.findViewById(R.id.subject_thumbnail);
            subjectCode = itemView.findViewById(R.id.subject_code);
            subjectAttendance = itemView.findViewById(R.id.subject_attendance);
            subjectTotal = itemView.findViewById(R.id.subject_total);
            subjectCardView = itemView.findViewById(R.id.subject_card_view);
        }
        
    }
}
