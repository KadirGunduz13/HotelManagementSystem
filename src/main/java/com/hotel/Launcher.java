package com.hotel;

public class Launcher {
    public static void main(String[] args) {
        // Bu sınıf Application'dan türemediği için JVM önce burayı çalıştırır,
        // kütüphaneleri yükler, sonra Main sınıfını çağırır.
        Main.main(args);
    }
}