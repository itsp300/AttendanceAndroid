package com.itsp.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{
    private List<Notification> notificationList;
    private Context context;

    NotificationAdapter(List<Notification> notificationList)
    {
        this.notificationList = notificationList;
    }

    public void updateData(List<Notification> notificationList)
    {
        this.notificationList = notificationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (!notificationList.isEmpty())
        {
            Notification notification = notificationList.get(position);

            holder.notificationTitle.setText(notification.getTitle());
            holder.notificationDescription.setText(notification.getDescription());
        }
    }

    @Override
    public int getItemCount()
    {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView notificationTitle;
        TextView notificationDescription;

        ViewHolder(View itemView)
        {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationDescription = itemView.findViewById(R.id.notification_description);
        }

    }
}
