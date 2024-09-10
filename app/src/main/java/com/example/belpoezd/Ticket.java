package com.example.belpoezd;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Ticket implements Parcelable {
    private String trainNumber;
    private String arrivalTime;
    private String direction;
    private String platform;
    private String track;
    private String passportNumber;
    private String passportSeries;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String selectedDate;
    private String userId; // Идентификатор пользователя, которому принадлежит билет
    public Ticket() {
        // Необходим для чтения из базы данных Firebase
    }
    public Ticket(String trainNumber, String arrivalTime, String direction, String platform, String track,
                  String passportNumber, String passportSeries,
                  String lastName, String firstName, String patronymic,
            String selectedDate,
            String userId) {
        this.trainNumber = trainNumber;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
        this.platform = platform;
        this.track = track;

        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;

        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;

        this.selectedDate = selectedDate;

        this.userId = userId;
    }
    protected Ticket(Parcel in) {
        trainNumber = in.readString();
        arrivalTime = in.readString();
        direction = in.readString();
        platform = in.readString();
        track = in.readString();

        passportNumber = in.readString();
        passportSeries = in.readString();

        lastName = in.readString();
        firstName = in.readString();
        patronymic = in.readString();


        selectedDate = in.readString();

        userId = in.readString();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trainNumber);
        dest.writeString(arrivalTime);
        dest.writeString(direction);
        dest.writeString(platform);
        dest.writeString(track);

        dest.writeString(passportNumber);
        dest.writeString(passportSeries);


        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeString(patronymic);


        dest.writeString(selectedDate);

        dest.writeString(userId);
    }

    // Геттер и сеттер для selectedDate

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    // Геттеры и сеттеры для полей паспорта и ФИО
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }


    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }//534tr
}
