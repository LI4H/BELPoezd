package com.example.belpoezd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.belpoezd.R;
import com.example.belpoezd.Train;
import com.example.belpoezd.TrainAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private ListView trainListView;
    private TrainAdapter trainAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        trainListView = view.findViewById(R.id.content);
        trainAdapter = new TrainAdapter(getContext(), R.layout.train_item, new ArrayList<>());
        trainListView.setAdapter(trainAdapter);

        // Получение ссылки на базу данных
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trains");

        // Чтение данных из базы данных
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Train> trains = new ArrayList<>();

                for (DataSnapshot trainSnapshot : dataSnapshot.getChildren()) {
                    Train train = trainSnapshot.getValue(Train.class);
                    if (train != null) {
                        trains.add(train);
                    }
                }

                // Выводим данные в лог для отладки
                for (Train train : trains) {
                    Log.d("TrainData", "Train Number: " + train.getTrainNumber());
                    Log.d("TrainData", "Arrival Time: " + train.getArrivalTime());
                    Log.d("TrainData", "Direction: " + train.getDirection());
                    Log.d("TrainData", "Platform: " + train.getPlatform());
                    Log.d("TrainData", "Track: " + train.getTrack());
                }

                // Обновляем список поездов в адаптере
                trainAdapter.clear();
                trainAdapter.addAll(trains);
                trainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
                Log.e("FirebaseError", "Error reading data from Firebase", databaseError.toException());
            }
        });
        trainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получение выбранного поезда
                Train selectedTrain = trainAdapter.getItem(position);

                // Создание Intent для перехода к ConfirmActivity
                Intent intent = new Intent(getContext(), ConfirmActivity.class);

                // Передача данных о выбранном поезде в ConfirmActivity
                intent.putExtra("trainNumber", selectedTrain.getTrainNumber());
                intent.putExtra("arrivalTime", selectedTrain.getArrivalTime());
                intent.putExtra("direction", selectedTrain.getDirection());
                intent.putExtra("platform", selectedTrain.getPlatform());
                intent.putExtra("track", selectedTrain.getTrack());

                // Запуск ConfirmActivity
                startActivity(intent);
            }
        });

        return view;
    }
}
