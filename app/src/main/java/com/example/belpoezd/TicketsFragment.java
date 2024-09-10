package com.example.belpoezd;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicketsFragment extends Fragment {

    private static final String TAG = "TicketsFragment";
    private ArrayList<Ticket> userTickets;
    private ArrayAdapter<Ticket> ticketAdapter;

    public TicketsFragment() {
        userTickets = new ArrayList<>();
    }

    public static TicketsFragment newInstance(ArrayList<Ticket> tickets) {
        TicketsFragment fragment = new TicketsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("tickets", tickets);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        ListView listView = view.findViewById(R.id.listViewTickets);
        ticketAdapter = new TicketAdapter(requireContext(), R.layout.list_item_ticket, userTickets);
        listView.setAdapter(ticketAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Загрузка билетов из Firebase при создании фрагмента и при нажатии на фрагмент с билетами
        loadTicketsFromFirebase();
    }

    private void loadTicketsFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Получение ссылки на базу данных Firebase для текущего пользователя
            String userId = currentUser.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ticketsRef = database.getReference("tickets").child(userId);

            // Чтение билетов из базы данных
            ticketsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Ticket> tickets = new ArrayList<>();
                    for (DataSnapshot ticketSnapshot : snapshot.getChildren()) {
                        Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                        if (ticket != null) {
                            tickets.add(ticket);
                        }
                    }

                    if (!tickets.isEmpty()) {
                        Log.d(TAG, "onDataChange: Tickets loaded successfully");
                        // Очистим массив и добавим новые билеты
                        userTickets.clear();
                        userTickets.addAll(tickets);
                        // Уведомим адаптер об изменениях
                        ticketAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "onDataChange: No tickets available");
                        // Обработка ситуации, когда нет доступных билетов
                        // Например, вы можете отобразить сообщение об отсутствии билетов
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: Unable to read tickets from Firebase", error.toException());
                }
            });
        }
    }
}
