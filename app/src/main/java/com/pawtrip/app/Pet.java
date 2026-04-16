package com.pawtrip.app;
public class Pet {
    public int id, age;
    public String name, breed, healthNotes, ownerEmail;
    public double weight;
    public String photoUri  = "";   // gallery URI of pet photo
    public String proofUri  = "";   // URI of vaccination/ownership doc
    public boolean isSenior() { return age > 7; }
}