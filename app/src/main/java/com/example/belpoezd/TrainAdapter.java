package com.example.belpoezd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.belpoezd.R;
import com.example.belpoezd.Train;

import java.util.List;

public class TrainAdapter extends ArrayAdapter<Train> {

    private Context context;
    private int resource;

    public TrainAdapter(@NonNull Context context, int resource, @NonNull List<Train> trains) {
        super(context, resource, trains);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Train train = getItem(position);

        if (train != null) {
            TextView trainNumberTextView = convertView.findViewById(R.id.trainNumberTextView);
            TextView arrivalTimeTextView = convertView.findViewById(R.id.arrivalTimeTextView);
            TextView directionTextView = convertView.findViewById(R.id.directionTextView);
            TextView platformTextView = convertView.findViewById(R.id.platformTextView);
            TextView trackTextView = convertView.findViewById(R.id.trackTextView);

            trainNumberTextView.setText("Train Number: " + train.getTrainNumber());
            arrivalTimeTextView.setText("Arrival Time: " + train.getArrivalTime());
            directionTextView.setText("Direction: " + train.getDirection());
            platformTextView.setText("Platform: " + train.getPlatform());
            trackTextView.setText("Track: " + train.getTrack());
        }

        return convertView;
    }
}
