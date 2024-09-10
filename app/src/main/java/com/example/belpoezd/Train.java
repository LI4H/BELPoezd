package com.example.belpoezd;

public class Train {
    private String trainNumber;
    private String arrivalTime;
    private String direction;
    private String platform;
    private String track;

    // Обязательный пустой конструктор для Firebase
    public Train() {}

    public Train(String trainNumber, String arrivalTime, String direction, String platform, String track) {
        this.trainNumber = trainNumber;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
        this.platform = platform;
        this.track = track;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDirection() {
        return direction;
    }

    public String getPlatform() {
        return platform;
    }

    public String getTrack() {
        return track;
    }
}
