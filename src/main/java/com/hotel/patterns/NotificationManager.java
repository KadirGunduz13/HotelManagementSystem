package com.hotel.patterns;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static NotificationManager instance;
    private List<IObserver> observers = new ArrayList<>();

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) instance = new NotificationManager();
        return instance;
    }

    // Abone Ekle
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    // Herkese Haber Ver
    public void notifyAll(String message) {
        for (IObserver observer : observers) {
            observer.update(message);
        }
    }
}