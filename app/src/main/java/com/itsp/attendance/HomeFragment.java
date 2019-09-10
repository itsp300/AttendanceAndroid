package com.itsp.attendance;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment
{
    private Context context;
    private final static String TAG = HomeFragment.class.getName();
    private ImageView rating;
    private TextView ratingPercentageText;
    private TextView studentNameText;
    private TextView attendanceTotalText;
    private TextView lectureTotalText;
    private TextView missedTotalText;
    private final static String STUDENTNAMELABEL = "Name: ";
    private final static String ATTENDANCETOTALLABEL = "Attendance Total: ";
    private final static String LECTURETOTALLABEL = "Lecture Total: ";
    private final static String MISSEDTOTALLABEL = "Lectures Missed: ";

    private JsonObjectRequest summaryObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        studentNameText = view.findViewById(R.id.home_student_name);
        attendanceTotalText = view.findViewById(R.id.home_attendance_total);
        lectureTotalText = view.findViewById(R.id.home_lecture_total);
        missedTotalText = view.findViewById(R.id.home_missed_total);

        int percentage = 0;
        ratingPercentageText = view.findViewById(R.id.rating_percentage);
        ratingPercentageText.setText(percentage + "%");
        int level = 100 * percentage;   // pct goes from 0 to 100
        rating = view.findViewById(R.id.rating_image);

        ratingFillAnimation ratingAnimation = new ratingFillAnimation(rating, 0, level);
//        ratingAnimation.setDuration(1000);
//        rating.startAnimation(ratingAnimation);

        String api_path = "/api/secure/summary";
        summaryObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Config.url + api_path, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            Log.d(TAG, "onResponse: " + response.toString());
                            int attendanceTotal = response.getInt("attendanceTotal");
                            int lectureTotal = response.getInt("lectureTotal");
                            int missedTotal = 0;

                            missedTotal = lectureTotal - attendanceTotal;
                            //int percentage =  (int)((((float)attendanceTotal /(float) lectureTotal) * 100f)) + 1;
                            float percentage = (((float)attendanceTotal /(float) lectureTotal) * 100f);
                            studentNameText.setText(STUDENTNAMELABEL + response.getString("studentName"));
                            attendanceTotalText.setText(ATTENDANCETOTALLABEL + attendanceTotal);
                            lectureTotalText.setText(LECTURETOTALLABEL + lectureTotal);
                            missedTotalText.setText(MISSEDTOTALLABEL + missedTotal);
                            ratingPercentageText.setText(percentage + "%");

                            int level = (int) Math.ceil(percentage) * 100;   // pct goes from 0 to 100

                            ratingFillAnimation ratingAnimation = new ratingFillAnimation(rating, 0, level);
                            ratingAnimation.setDuration(1000);
                            rating.startAnimation(ratingAnimation);

                            Log.d(TAG, "onResponse: Summary data updated");

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse networkResponse = error.networkResponse;
                        if(context != null)
                        {
                            if(networkResponse != null)
                            {
                                String data = new String(networkResponse.data);
                                Log.e(TAG, "onErrorResponse:\nbody:\n" + data);

                                if(networkResponse.statusCode == 500 || networkResponse.statusCode == 401)
                                {
                                    ((MainActivity)getActivity()).login();
                                }
                                else
                                {
                                    Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(context, "Network error!\nNo response received!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Config.accessToken);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(summaryObjectRequest);

        return view;
    }

    class ratingFillAnimation extends Animation
    {
        private ImageView rating;
        private float from;
        private float to;

        public ratingFillAnimation(ImageView rating, float from, float to)
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
            
            float value = (float) smoothStep(from, 1, ((to/10000)*interpolatedTime))*10000;//from + (to - from) * interpolatedTime;
            rating.setImageLevel((int) value);
        }
        
        public  double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(value, max));
        }
        public  double smoothStep(double start, double end, double amount) {
            amount = clamp(amount, 0, 1);
            amount = clamp((amount - start) / (end - start), 0, 1);
            return amount * amount * (3 - 2 * amount);
        }

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        context = null;
    }
}