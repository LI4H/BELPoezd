package com.example.belpoezd;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_search) {
                selectedFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (item.getItemId() == R.id.navigation_tickets) {
                selectedFragment = new TicketsFragment();
            } else if (item.getItemId() == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;
            } else {
                return false;
            }
        });

        // Проверяем наличие дополнительной информации от другой активности
        if (getIntent().hasExtra("fragmentToLoad")) {
            String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
            if ("TicketsFragment".equals(fragmentToLoad)) {
                Log.d(TAG, "onCreate: Loading TicketsFragment with tickets");

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
                                // Создание экземпляра TicketsFragment и передача билетов
                                TicketsFragment ticketsFragment = TicketsFragment.newInstance(tickets);

                                // Замена текущего фрагмента на TicketsFragment
                                if (!isFinishing() && !isDestroyed()) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainer, ticketsFragment)
                                            .commitAllowingStateLoss();
                                }
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
        } else {
            // Если дополнительной информации нет или пользователь не вошел в систему, установите фрагмент по умолчанию
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SearchFragment()).commit();
        }
    }
}
