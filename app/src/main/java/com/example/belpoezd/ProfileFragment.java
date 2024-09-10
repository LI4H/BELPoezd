package com.example.belpoezd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.belpoezd.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView lastNameTextView, firstNameTextView, patronymicTextView, passportNumberTextView, passportSeriesTextView;
    private EditText lastNameEditText, firstNameEditText, patronymicEditText, passportNumberEditText, passportSeriesEditText;
    private Button  saveButton, cancelButton;
    private ImageButton editButton, signOutButton;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userDatabaseReference;

    private boolean isEditing = false;

    // Добавлено для слушателя изменений в Firebase
    private ValueEventListener userValueEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        }

        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        patronymicTextView = view.findViewById(R.id.patronymicTextView);
        passportNumberTextView = view.findViewById(R.id.passportNumberTextView);
        passportSeriesTextView = view.findViewById(R.id.passportSeriesTextView);

        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        patronymicEditText = view.findViewById(R.id.patronymicEditText);
        passportNumberEditText = view.findViewById(R.id.passportNumberEditText);
        passportSeriesEditText = view.findViewById(R.id.passportSeriesEditText);

        editButton = view.findViewById(R.id.editButton);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode(true);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    saveUserData();
                } else {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode(false);
            }
        });

        // Добавлено для начала прослушивания изменений в базе данных Firebase
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    updateUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Error reading data", error.toException());
            }
        };
        // кнопки выхода из аккаунта
        ImageButton signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(); // Метод для выхода из аккаунта
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Начало прослушивания изменений в базе данных Firebase
        userDatabaseReference.addValueEventListener(userValueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Прекращение прослушивания изменений в базе данных Firebase
        userDatabaseReference.removeEventListener(userValueEventListener);
    }

    private void toggleEditMode(boolean isEdit) {
        isEditing = isEdit;

        if (isEditing) {
            lastNameTextView.setVisibility(View.GONE);
            firstNameTextView.setVisibility(View.GONE);
            patronymicTextView.setVisibility(View.GONE);
            passportNumberTextView.setVisibility(View.GONE);
            passportSeriesTextView.setVisibility(View.GONE);

            lastNameEditText.setVisibility(View.VISIBLE);
            firstNameEditText.setVisibility(View.VISIBLE);
            patronymicEditText.setVisibility(View.VISIBLE);
            passportNumberEditText.setVisibility(View.VISIBLE);
            passportSeriesEditText.setVisibility(View.VISIBLE);

            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            // Отображение данных из TextView в EditText при редактировании
            lastNameEditText.setText(lastNameTextView.getText());
            firstNameEditText.setText(firstNameTextView.getText());
            patronymicEditText.setText(patronymicTextView.getText());
            passportNumberEditText.setText(passportNumberTextView.getText());
            passportSeriesEditText.setText(passportSeriesTextView.getText());

            // Сделать кнопку выхода невидимой и некликабельной при редактировании
            ImageButton signOutButton = getView().findViewById(R.id.signOutButton);
            signOutButton.setVisibility(View.INVISIBLE);
            signOutButton.setEnabled(false);
        } else {
            lastNameTextView.setVisibility(View.VISIBLE);
            firstNameTextView.setVisibility(View.VISIBLE);
            patronymicTextView.setVisibility(View.VISIBLE);
            passportNumberTextView.setVisibility(View.VISIBLE);
            passportSeriesTextView.setVisibility(View.VISIBLE);

            lastNameEditText.setVisibility(View.GONE);
            firstNameEditText.setVisibility(View.GONE);
            patronymicEditText.setVisibility(View.GONE);
            passportNumberEditText.setVisibility(View.GONE);
            passportSeriesEditText.setVisibility(View.GONE);

            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

            // Сделать кнопку выхода видимой и кликабельной при завершении редактирования
            ImageButton signOutButton = getView().findViewById(R.id.signOutButton);
            signOutButton.setVisibility(View.VISIBLE);
            signOutButton.setEnabled(true);
        }
    }

    private void saveUserData() {
        String lastName = lastNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String patronymic = patronymicEditText.getText().toString();
        String passportNumber = passportNumberEditText.getText().toString();
        String passportSeries = passportSeriesEditText.getText().toString();

        // Проверка на null добавлена для currentUser
        if (currentUser != null) {
            User user = new User(lastName, firstName, patronymic, passportNumber, passportSeries, currentUser.getEmail());

            userDatabaseReference.setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            toggleEditMode(false);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ProfileFragment", "Error saving data", e);
                        }
                    });
        } else {
            Log.e("ProfileFragment", "Current user is null");
        }
    }


    private void updateUI(User user) {
        lastNameTextView.setText(user.getLastName());
        firstNameTextView.setText(user.getFirstName());
        patronymicTextView.setText(user.getPatronymic());
        passportNumberTextView.setText(user.getPassportNumber());
        passportSeriesTextView.setText(user.getPassportSeries());
    }

    // метод для проверки пустых полей
    private boolean checkFields() {
        return !lastNameEditText.getText().toString().isEmpty() &&
                !firstNameEditText.getText().toString().isEmpty() &&
                !patronymicEditText.getText().toString().isEmpty() &&
                !passportNumberEditText.getText().toString().isEmpty() &&
                !passportSeriesEditText.getText().toString().isEmpty();
    }
    // Метод для выхода из аккаунта
    private void signOut() {
        auth.signOut();
        // Переход на экран входа
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
