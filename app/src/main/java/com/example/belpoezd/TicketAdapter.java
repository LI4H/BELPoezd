package com.example.belpoezd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.belpoezd.R;
import com.example.belpoezd.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private ArrayList<Ticket> tickets;

    public TicketAdapter(Context context, int resource, ArrayList<Ticket> tickets) {
        super(context, resource, tickets);
        this.tickets = tickets;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_ticket, parent, false);
        }

        // Получите текущий билет
        Ticket currentTicket = getItem(position);

        // Найдите TextView в макете
        TextView trainNumber = convertView.findViewById(R.id.trainNumber);
        TextView arrivalTime = convertView.findViewById(R.id.arrivalTime);
        TextView direction = convertView.findViewById(R.id.direction);
        TextView platform = convertView.findViewById(R.id.platform);
        TextView track = convertView.findViewById(R.id.track);
        TextView passportNumber = convertView.findViewById(R.id.passportNumber);
        TextView passportSeries = convertView.findViewById(R.id.passportSeries);
        TextView lastName = convertView.findViewById(R.id.lastName);
        TextView firstName = convertView.findViewById(R.id.firstName);
        TextView patronymic = convertView.findViewById(R.id.patronymic);
        TextView selectedDate = convertView.findViewById(R.id.selectedDate);

        // Установите текст в TextView на основе данных билета
        if (currentTicket != null) {
            trainNumber.setText("Поезд " + currentTicket.getTrainNumber());//"Номер поезда: " +
            arrivalTime.setText(currentTicket.getArrivalTime());//"Время прибытия: " +
            direction.setText(currentTicket.getDirection());//"Направление: " +
            platform.setText("Платформа: " + currentTicket.getPlatform());
            track.setText("Путь: " + currentTicket.getTrack());
            passportNumber.setText( currentTicket.getPassportNumber());//"Номер паспорта: " +
            passportSeries.setText("Паспорт: " + currentTicket.getPassportSeries());//"Серия паспорта: " +
            lastName.setText("Фамилия: " + currentTicket.getLastName());
            firstName.setText("Имя: " + currentTicket.getFirstName());
            patronymic.setText("Отчество: " + currentTicket.getPatronymic());
            selectedDate.setText(currentTicket.getSelectedDate());//"Дата: " +
        }

        return convertView;
    }
}
