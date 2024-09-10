package com.example.belpoezd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private ListView ticketsListView;
    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Инициализация элементов управления
        fromSpinner = view.findViewById(R.id.fromSpinner);
        toSpinner = view.findViewById(R.id.toSpinner);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        ticketsListView = view.findViewById(R.id.ticketsListView);

        // Наполняем выпадающие списки данными (вам нужно подставить свои данные)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.directions_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(v -> searchTickets());

        // Обработчик нажатия на элемент списка
        ticketsListView.setOnItemClickListener((parent, listView, position, id) -> {
            // Получение выбранного билета
            String selectedTicket = (String) parent.getItemAtPosition(position);
            Log.d("SelectedTicket", "Выбран билет: " + selectedTicket);
            // Получение соответствующего поезда для выбранного билета
            Train selectedTrain = getTrainFromTicket(selectedTicket);
            // Проверка, что поезд не равен null, прежде чем создать Intent
            if (selectedTrain != null) {
                // Создание Intent для перехода к ConfirmActivity
                Intent intent = new Intent(requireContext(), ConfirmActivity.class);
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
    private void setupListView(ArrayList<String> tickets) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                tickets
        );
        ticketsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void searchTickets() {
        // Получение введенных пользователем параметров
        String fromDirection = fromSpinner.getSelectedItem().toString();
        String toDirection = toSpinner.getSelectedItem().toString();
        // Вызов метода для поиска билетов из Firebase
        findTicketsFromFirebase(fromDirection, toDirection);
    }

    private void findTicketsFromFirebase(String fromDirection, String toDirection) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trains");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> ticketsList = new ArrayList<>();
                for (DataSnapshot trainSnapshot : dataSnapshot.getChildren()) {
                    Train train = trainSnapshot.getValue(Train.class);

                    Log.d("DirectionCheck", "Selected From: " + fromDirection + ", To: " + toDirection);
                    Log.d("DirectionCheck", "Train Direction: " + train.getDirection());

                    if (trainMatches(train, fromDirection, toDirection)) {
                        String ticketInfo = String.format("Поезд №%s, Направление: %s, Время прибытия: %s",
                                train.getTrainNumber(), train.getDirection(), train.getArrivalTime());

                        ticketsList.add(ticketInfo);

                        // Добавьте следующую строку для отладки
                        Log.d("TicketList", "Добавлен билет: " + ticketInfo);
                    } else {
                        // Добавьте следующую строку для отладки
                        Log.d("TicketList", "Билет не соответствует условиям поиска");
                    }
                }

                Log.d("TicketList", "Tickets in the list: " + ticketsList.toString());

                if (ticketsList.isEmpty()) {
                    Toast.makeText(requireContext(), "Билеты не найдены", Toast.LENGTH_SHORT).show();
                } else {
                    setupListView(ticketsList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private boolean trainMatches(Train train, String fromDirection, String toDirection) {
        String trainDirection = train.getDirection().toLowerCase();

        // Приводим выбранные направления к нижнему регистру для унификации
        fromDirection = fromDirection.toLowerCase();
        toDirection = toDirection.toLowerCase();

        // Разбиваем направление поезда на части для более точного сравнения
        String[] directions = trainDirection.split("-");

        // Проверяем, что выбранные направления соответствуют частям направления поезда
        return (directions.length == 2 && directions[0].contains(fromDirection) && directions[1].contains(toDirection)) ||
                (directions.length == 2 && directions[1].contains(fromDirection) && directions[0].contains(toDirection));
    }

    // Метод для получения объекта Train из строки билета
    private Train getTrainFromTicket(String ticketInfo) {
        // Извлекаем номер поезда из строки билета
        String trainNumber = extractTrainNumberFromTicket(ticketInfo);

        // Проверяем, что номер поезда не пустой
        if (trainNumber != null) {
            // Ищем поезд в базе данных Firebase по номеру
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trains");
            databaseReference.orderByChild("trainNumber").equalTo(trainNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Проверяем, что найден хотя бы один поезд
                    if (dataSnapshot.exists()) {
                        // Получаем первый найденный поезд (если номер уникален, то будет только один)
                        Train selectedTrain = dataSnapshot.getChildren().iterator().next().getValue(Train.class);

                        // Создаем Intent для перехода к ConfirmActivity
                        Intent intent = new Intent(requireContext(), ConfirmActivity.class);
                        // Передаем данные о выбранном поезде в ConfirmActivity
                        intent.putExtra("trainNumber", selectedTrain.getTrainNumber());
                        intent.putExtra("arrivalTime", selectedTrain.getArrivalTime());
                        intent.putExtra("direction", selectedTrain.getDirection());
                        intent.putExtra("platform", selectedTrain.getPlatform());
                        intent.putExtra("track", selectedTrain.getTrack());

                        // Запускаем ConfirmActivity
                        startActivity(intent);
                    } else {
                        // Если поезд не найден, выводим сообщение
                        Toast.makeText(requireContext(), "Поезд не найден", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Failed to read value.", databaseError.toException());
                }
            });
        }

        // Если номер поезда пустой, возвращаем null
        return null;
    }

    // Пример метода для извлечения номера поезда из строки билета
    private String extractTrainNumberFromTicket(String ticketInfo) {
        int trainNumberStartIndex = ticketInfo.indexOf("Поезд №") + 7;
        int trainNumberEndIndex = ticketInfo.indexOf(",", trainNumberStartIndex);

        if (trainNumberStartIndex != -1 && trainNumberEndIndex != -1) {
            return ticketInfo.substring(trainNumberStartIndex, trainNumberEndIndex).trim();
        }

        return null;
    }
}
