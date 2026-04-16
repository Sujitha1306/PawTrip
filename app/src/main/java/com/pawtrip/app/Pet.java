package com.pawtrip.app;
public class Pet {
    public int id, age;
    public String name, breed, healthNotes, ownerEmail;
    public double weight;
    public boolean isSenior() { return age > 7; }
}