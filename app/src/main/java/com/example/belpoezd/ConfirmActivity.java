package com.example.belpoezd;

import com.example.belpoezd.JavaMailSender;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.belpoezd.User;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConfirmActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private TextView selectedDateTextView;
    private Calendar selectedDate;
    private ImageButton payButton;
    private String trainNumber;
    private String arrivalTime;
    private String direction;
    private String platform;
    private String track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ConfirmActivity", "ConfirmActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            trainNumber = intent.getStringExtra("trainNumber");
            arrivalTime = intent.getStringExtra("arrivalTime");
            direction = intent.getStringExtra("direction");
            platform = intent.getStringExtra("platform");
            track = intent.getStringExtra("track");
            Log.d("ConfirmActivity", "Train Number: " + trainNumber);
            Log.d("ConfirmActivity", "Arrival Time: " + arrivalTime);
            Log.d("ConfirmActivity", "Direction: " + direction);
            Log.d("ConfirmActivity", "Platform: " + platform);
            Log.d("ConfirmActivity", "Track: " + track);
            setTextViewText(R.id.trainNumberTextView, "Номер поезда: " + trainNumber);
            setTextViewText(R.id.arrivalTimeTextView, "Время прибытия: " + arrivalTime);
            setTextViewText(R.id.directionTextView, "Направление: " + direction);
            setTextViewText(R.id.platformTextView, "Платформа: " + platform);
            setTextViewText(R.id.trackTextView, "Путь: " + track);
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        ImageButton selectDateButton = findViewById(R.id.selectDateButton);

        selectedDate = Calendar.getInstance();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        payButton = findViewById(R.id.payButton);
        payButton.setVisibility(View.INVISIBLE);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTicket();
            }
        });

        // Получение данных пользователя из Firebase
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Установка данных пользователя в соответствующие TextView
                        String passportNumber = user.getPassportNumber();
                        String passportSeries = user.getPassportSeries();
                        String lastName = user.getLastName();
                        String firstName = user.getFirstName();
                        String patronymic = user.getPatronymic();

                        setTextViewText(R.id.patronymicTextView, patronymic);
                        setTextViewText(R.id.firstNameTextView, firstName);
                        setTextViewText(R.id.lastNameTextView, lastName);
                        setTextViewText(R.id.passportSeriesTextView, passportSeries);
                        setTextViewText(R.id.passportNumberTextView, passportNumber);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ConfirmActivity", "Error reading user data from Firebase", error.toException());
            }
        });
    }

    private void setTextViewText(int textViewId, String text) {
        TextView textView = findViewById(textViewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (isValidDate(selectedDate)) {
                    updateSelectedDateTextView();
                    payButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ConfirmActivity.this, "Выбранная дата недопустима", Toast.LENGTH_SHORT).show();
                }
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L);

        datePickerDialog.show();
    }

    private void updateSelectedDateTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());
        selectedDateTextView.setText("Выбранная дата: " + formattedDate);
    }

    private boolean isValidDate(Calendar date) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_MONTH, 30);

        boolean isValid = !date.before(Calendar.getInstance()) && !date.after(currentDate);
        return isValid;
    }

    private void createTicket() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        updateSelectedDateTextView();

                        if (isValidDate(selectedDate)) {
                            handlePayment(user, selectedDate.getTime());
                            Toast.makeText(ConfirmActivity.this, "Оплата произошла успешно!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ConfirmActivity.this, "Выбранная дата недопустима", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ConfirmActivity.this, "Данные пользователя недоступны", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ConfirmActivity.this, "Ошибка получения данных пользователя: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handlePayment(User user, Date selectedDate) {
        // Ваша логика обработки успешной оплаты
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());

        Ticket ticket = new Ticket(
                trainNumber,
                arrivalTime,
                direction,
                platform,
                track,
                user.getPassportNumber(),
                user.getPassportSeries(),
                user.getLastName(),
                user.getFirstName(),
                user.getPatronymic(),
                formattedDate,
                user.getUid()
        );
        saveTicketToFirebase(ticket);
        // Отправка билета на почту
        sendTicketByEmail(ticket);
        navigateToTicketsFragment();
    }

    private void saveTicketToFirebase(Ticket ticket) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")
                    .child(currentUser.getUid())
                    .push();

            if (ticket != null) {
                ticketsRef.setValue(ticket);
            }
        }
    }

    private void navigateToTicketsFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "TicketsFragment");
        startActivity(intent);
        finish();
    }
    private void sendTicketByEmail(Ticket ticket) {
        // Формирование текста сообщения
        String emailSubject = "Ваш билет на поезд";
        String emailBody = "Спасибо за покупку!\n\n" +
                "Детали вашего билета:\n" +
                "Номер поезда: " + ticket.getTrainNumber() + "\n" +
                "Время прибытия: " + ticket.getArrivalTime() + "\n" +
                "Дата: " + ticket.getSelectedDate() + "\n" +
                "Направление: " + ticket.getDirection() + "\n" +
                "Платформа: " + ticket.getPlatform() + "\n" +
                "Путь: " + ticket.getTrack() + "\n" +
                "Пассажир: " + ticket.getFirstName() + " " + ticket.getLastName() + "\n" +
                "Серия и номер паспорта: " + ticket.getPassportSeries() + " " + ticket.getPassportNumber() + "\n\n" +
                "С уважением,\nBELPoezd";

        // Получение данных пользователя из Firebase
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Отправка электронного письма через JavaMail API
                        sendEmail(user.getEmail(), emailSubject, emailBody);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ConfirmActivity", "Error reading user data from Firebase", error.toException());
            }
        });
    }

    // Метод для отправки электронного письма с использованием JavaMail API
    private void sendEmail(String toEmail, String subject, String body) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Создание объекта JavaMail
                    JavaMailSender sender = new JavaMailSender("BELPoezd2.0@gmail.com", "2177647Lev");

                    // Отправка письма
                    sender.sendMail(subject, body, "BELPoezd2.0@gmail.com", toEmail);
                } catch (Exception e) {
                    Log.e("ConfirmActivity", "Error sending email", e);
                }
                return null;
            }
        }.execute();
    }
    }

