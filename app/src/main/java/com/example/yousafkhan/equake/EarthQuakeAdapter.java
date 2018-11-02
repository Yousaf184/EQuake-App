package com.example.yousafkhan.equake;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {

    public EarthQuakeAdapter(Context context, ArrayList<EarthQuake> list) {
        super(context, 0, list);
    }

    // split the location string in to two parts
    private String[] splitLocation(String location) {

        // example location = 88 km N of new york, America

        int index = location.indexOf("of");

        // example location_one = 88 km N of
        String location_one = location.substring(0, index + 2);

        // example location_two = new york, America
        String location_two = location.substring(index + 3);

        return new String[]{location_one, location_two};
    }

    // convert time in milliseconds in to formatted date and time
    private String[] formatDateTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US);

        // example formattedDate = Nov 2, 2018 2:43 AM
        String formattedDate = sdf.format(date);

        int index = formattedDate.indexOf(":");

        // example eDate = Nov 2, 2018
        String eDate = formattedDate.substring(0, index - 2);

        // example eTime = 2:43 AM
        String eTime  = formattedDate.substring(index - 1);

        return new String[]{eDate, eTime};
    }

    // format magnitude value to one digit after the decimal
    private String formatMagnitude(double magnitude) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(magnitude);
    }

    // returns the background color for magnitude textview
    public int getMagnitudeColor(double magnitude) {
        int mag = (int) Math.floor(magnitude);
        int magnitudeColorID = -1;

        if(mag >= 0 && mag <= 2) {
            magnitudeColorID = R.color.magnitude1;
        } else if(mag > 2 && mag <= 3) {
            magnitudeColorID = R.color.magnitude2;
        } else if(mag > 3 && mag <= 4) {
            magnitudeColorID = R.color.magnitude3;
        } else if(mag > 4 && mag <= 5) {
            magnitudeColorID = R.color.magnitude4;
        } else if(mag > 5 && mag <= 6) {
            magnitudeColorID = R.color.magnitude5;
        } else if(mag > 6 && mag <= 7) {
            magnitudeColorID = R.color.magnitude6;
        } else if(mag > 7 && mag <= 8) {
            magnitudeColorID = R.color.magnitude7;
        } else if(mag > 8 && mag <= 9) {
            magnitudeColorID = R.color.magnitude8;
        } else if(mag > 9 && mag <= 10) {
            magnitudeColorID = R.color.magnitude9;
        } else if(mag > 10) {
            magnitudeColorID = R.color.magnitude10plus;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);

            holder = new ViewHolder();
            holder.magnitudeText = convertView.findViewById(R.id.magnitude_text);
            holder.location_one_text = convertView.findViewById(R.id.location_text_one);
            holder.location_two_text = convertView.findViewById(R.id.location_text_two);
            holder.dateText = convertView.findViewById(R.id.date_text);
            holder.timeText = convertView.findViewById(R.id.time_text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EarthQuake eq = getItem(position);

        String magnitude = formatMagnitude(eq.getMagnitude());

        holder.magnitudeText.setText(magnitude);

        // split location in to two parts
        String[] splitLocation = splitLocation(eq.getLocation());

        holder.location_one_text.setText(splitLocation[0]);
        holder.location_two_text.setText(splitLocation[1]);

        // get formatted date time from time in milliseconds
        String[] dateTime = formatDateTime(eq.getTime());

        holder.dateText.setText(dateTime[0]);
        holder.timeText.setText(dateTime[1]);

        // set color of magnitude textView background shape
        //get the background
        GradientDrawable magnitudeBackground = (GradientDrawable) holder.magnitudeText.getBackground();
        // get the appropriate background color for magnitude textview
        int magBgColor = getMagnitudeColor(eq.getMagnitude());
        // set the background color
        magnitudeBackground.setColor(magBgColor);

        return convertView;
    }

    private static class ViewHolder {
        private TextView magnitudeText;
        private TextView location_one_text;
        private TextView location_two_text;
        private TextView dateText;
        private TextView timeText;
    }
}
