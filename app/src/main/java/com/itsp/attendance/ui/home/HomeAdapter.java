package com.itsp.attendance.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.itsp.attendance.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<Subject> subjectList;
    private Context context;

    HomeAdapter(List<Subject> subjectList) {
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
            subjectThumbnail = itemView.findViewById(R.id.home_image_subjectPicture);
            subjectCode = itemView.findViewById(R.id.home_text_subjectCode);
            subjectAttendance = itemView.findViewById(R.id.home_text_attendedTotal);
            subjectTotal = itemView.findViewById(R.id.home_text_classesTotal);
            subjectCardView = itemView.findViewById(R.id.subject_card_view);
        }
        
    }
}
