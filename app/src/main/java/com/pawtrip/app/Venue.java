package com.pawtrip.app;
public class Venue {
    public int id;
    public String name, type, petRules, phone, openHours, city;
    public double latitude, longitude, pawScore;
    public int reviewCount;
    public String getTypeEmoji() {
        switch (type) {
            case "park":  return "🌳";
            case "cafe":  return "☕";
            case "vet":   return "🏥";
            case "hotel": return "🏨";
            default:      return "📍";
        }
    }
}