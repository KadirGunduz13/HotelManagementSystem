package com.hotel.patterns;

public class ManagerSMSObserver implements IObserver {
    @Override
    public void update(String message) {
        System.out.println(">>> [OBSERVER - SMS]: Yöneticiye Mesaj Gönderildi: " + message);
        // İleride buraya gerçek SMS api kodları yazılabilir.
    }
}