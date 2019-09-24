package com.itsp.attendance.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.itsp.attendance.R;
import com.itsp.attendance.Utility;

public class DashboardFragment extends Fragment
{
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardViewModel =
                ViewModelProviders.of(getActivity()).get(DashboardViewModel.class);
        dashboardViewModel.context = getContext();
        dashboardViewModel.activity = getActivity();

        final TextView textStudentName = root.findViewById(R.id.dash_text_studName);
        final TextView textAttendedTotal = root.findViewById(R.id.dash_text_attendedTotal);
        final TextView textClassTotal = root.findViewById(R.id.dash_text_classTotal);
        final TextView textMissedTotal = root.findViewById(R.id.dash_text_missedTotal);
        final TextView textRatingPercentage = root.findViewById(R.id.dash_text_ratingIndicator);
        final ImageView imageRating = root.findViewById(R.id.dash_image_rating);

        dashboardViewModel.getData().observe(this, new Observer<DashboardData>()
        {
            @Override
            public void onChanged(@Nullable DashboardData newData)
            {
                Utility.ASSERT(newData.studentName != null);
                Utility.ASSERT(newData.attendedTotal != null);
                Utility.ASSERT(newData.classTotal != null);
                Utility.ASSERT(newData.missedTotal != null);

                textStudentName.setText(newData.studentName);
                textAttendedTotal.setText(newData.attendedTotal);
                textClassTotal.setText(newData.classTotal);
                textMissedTotal.setText(newData.missedTotal);
                textRatingPercentage.setText((Math.round(newData.ratingPercentage * 100.0f) / 100.0f) + "%");

                int level = (int) Math.ceil(newData.ratingPercentage) * 100;

                RatingFillAnimation ratingAnimation = new RatingFillAnimation(imageRating, 0, level);
                ratingAnimation.setDuration(1000);
                imageRating.startAnimation(ratingAnimation);

            }
        });

        // NOTE(Morne): Attempts to retrieve new data from the server, in the meantime we display the previous data.
        dashboardViewModel.loadData();

        return root;
    }

    class RatingFillAnimation extends Animation
    {
        private ImageView rating;
        private float from;
        private float to;

        public RatingFillAnimation(ImageView rating, float from, float to)
        {
            super();
            this.rating = rating;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);

            float value = (float) smoothStep(from, 1, ((to / 10000) * interpolatedTime)) * 10000;//from + (to - from) * interpolatedTime;
            rating.setImageLevel((int) value);
        }

        public double clamp(double value, double min, double max)
        {
            return Math.max(min, Math.min(value, max));
        }

        public double smoothStep(double start, double end, double amount)
        {
            amount = clamp(amount, 0, 1);
            amount = clamp((amount - start) / (end - start), 0, 1);
            return amount * amount * (3 - 2 * amount);
        }
    }
}