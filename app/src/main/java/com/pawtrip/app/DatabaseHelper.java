package com.pawtrip.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pawtrip.db";
    private static final int DB_VERSION = 2;

    // Table names
    public static final String TABLE_VENUES = "venues";
    public static final String TABLE_PETS = "pets";
    public static final String TABLE_TRIPS = "trips";
    public static final String TABLE_TRIP_STOPS = "trip_stops";
    public static final String TABLE_HEALTH = "health_records";
    public static final String TABLE_SETTINGS = "settings";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_VENUES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "type TEXT,"
                + "latitude REAL,"
                + "longitude REAL,"
                + "pet_rules TEXT,"
                + "phone TEXT,"
                + "open_hours TEXT,"
                + "paw_score REAL DEFAULT 0,"
                + "review_count INTEGER DEFAULT 0,"
                + "city TEXT,"
                + "is_open INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE " + TABLE_PETS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "breed TEXT,"
                + "age INTEGER,"
                + "weight REAL,"
                + "health_notes TEXT,"
                + "is_senior INTEGER DEFAULT 0,"
                + "owner_email TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TRIPS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "origin TEXT,"
                + "destination TEXT,"
                + "date TEXT,"
                + "pet_id INTEGER,"
                + "notes TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TRIP_STOPS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "trip_id INTEGER,"
                + "venue_id INTEGER,"
                + "stop_order INTEGER,"
                + "arrival_time TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HEALTH + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "pet_id INTEGER,"
                + "record_type TEXT,"
                + "description TEXT,"
                + "date TEXT,"
                + "next_due TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SETTINGS + " ("
                + "key TEXT PRIMARY KEY,"
                + "value TEXT)");

        insertSampleVenues(db);
    }

    private void insertSampleVenues(SQLiteDatabase db) {
        String[][] venues = {
            // Chennai
            {"Besant Nagar Beach Park","park","13.0002","80.2706","Dogs allowed on leash","","6:00 AM - 9:00 PM","4.7","Chennai"},
            {"Cubbon Park Chennai","park","13.0100","80.2100","Pets welcome on leash","","5:00 AM - 8:00 PM","4.5","Chennai"},
            {"Cuddles Pet Café","cafe","13.0067","80.2574","All pets welcome","044-12345678","9:00 AM - 10:00 PM","4.9","Chennai"},
            {"Apollo Vet Clinic Adyar","vet","13.0050","80.2550","Emergency 24/7","044-98765432","24 hours","4.6","Chennai"},
            {"Blue Cross Vet Hospital","vet","13.0200","80.2300","24/7 emergency care","044-22430089","24 hours","4.9","Chennai"},
            {"Pet Paradise Hotel Chennai","hotel","13.0100","80.2600","Pets stay free","044-11223344","Check-in 2PM","4.5","Chennai"},
            {"Elliot's Beach Park","park","12.9987","80.2727","Morning pet walks allowed","","5:00 AM - 9:00 PM","4.6","Chennai"},
            {"Theosophical Society Gardens","park","13.0035","80.2719","Peaceful walks, leash required","","6:00 AM - 6:00 PM","4.4","Chennai"},
            {"Paws & Claws Pet Shop Café","cafe","13.0521","80.2458","Dog-friendly café","044-55667788","10:00 AM - 9:00 PM","4.7","Chennai"},
            {"VetCare Animal Hospital","vet","13.0678","80.2123","All animals treated","044-33445566","8:00 AM - 10:00 PM","4.5","Chennai"},

            // Coimbatore
            {"Ukkadam Lake Park","park","10.9925","76.9691","Dogs allowed, open space","","5:00 AM - 8:00 PM","4.6","Coimbatore"},
            {"VOC Park Coimbatore","park","11.0011","76.9554","Large park, pets on leash","","6:00 AM - 8:00 PM","4.5","Coimbatore"},
            {"Pawsome Pet Café Coimbatore","cafe","11.0168","76.9558","Pet-friendly seating, treats for dogs","0422-1234567","9:00 AM - 10:00 PM","4.8","Coimbatore"},
            {"Blue Cross Animal Hospital CBE","vet","11.0085","76.9678","Full emergency care","0422-9876543","24 hours","4.7","Coimbatore"},
            {"PAWS Vet Clinic","vet","10.9950","76.9800","Specialist vet care","0422-5556677","9:00 AM - 8:00 PM","4.6","Coimbatore"},
            {"Staywell Pet Hotel","hotel","11.0200","76.9500","Dogs and cats welcome","0422-7778899","Check-in 1PM","4.4","Coimbatore"},
            {"Singanallur Lake Trail","park","11.0050","77.0200","Walking trail, pets allowed","","5:30 AM - 7:30 PM","4.5","Coimbatore"},
            {"Fun Paws Pet Store","cafe","11.0300","76.9600","Pets welcome inside store","0422-4445566","10:00 AM - 8:00 PM","4.3","Coimbatore"},

            // Bangalore
            {"Cubbon Park Bangalore","park","12.9763","77.5929","Pets welcome, huge green space","","6:00 AM - 6:00 PM","4.8","Bangalore"},
            {"Lalbagh Botanical Garden","park","12.9507","77.5848","Leashed pets allowed on paths","","6:00 AM - 7:00 PM","4.7","Bangalore"},
            {"Heads Up For Tails Café Indiranagar","cafe","12.9716","77.6412","Premium dog-friendly café","080-55667788","10:00 AM - 10:00 PM","4.9","Bangalore"},
            {"Charlie's Vet Hospital","vet","12.9850","77.6001","24hr emergency, specialist care","080-22334455","24 hours","4.8","Bangalore"},
            {"Cessna Lifeline Vet Hospital","vet","12.9200","77.6800","Multi-specialist animal hospital","080-66778899","24 hours","4.9","Bangalore"},
            {"The Leela Palace Bangalore","hotel","12.9609","77.6387","Pets allowed with deposit","080-25217234","Check-in 2PM","4.7","Bangalore"},
            {"Turahalli Forest Trail","park","12.8900","77.5200","Off-leash trail, dog-friendly","","6:00 AM - 6:00 PM","4.6","Bangalore"},
            {"Dogspot Café Koramangala","cafe","12.9352","77.6245","India's most dog-friendly café","080-33445566","9:00 AM - 11:00 PM","4.9","Bangalore"},
            {"CUPA Animal Hospital","vet","12.9741","77.5940","Non-profit, affordable care","080-22947301","9:00 AM - 5:00 PM","4.8","Bangalore"},
            {"Windflower Prakruthi Resort","hotel","13.0500","77.5800","Pet-friendly resort, garden rooms","080-77889900","Check-in 12PM","4.6","Bangalore"},

            // Mumbai
            {"Sanjay Gandhi National Park","park","19.2147","72.9107","Leashed pets allowed","","7:00 AM - 6:00 PM","4.6","Mumbai"},
            {"Carter Road Promenade","park","19.0660","72.8295","Evening pet walk spot","","Open all day","4.7","Mumbai"},
            {"The Dogist Café Bandra","cafe","19.0550","72.8350","Mumbai's top dog café","022-44556677","10:00 AM - 11:00 PM","4.8","Mumbai"},
            {"Bombay Veterinary College Hospital","vet","19.0330","72.8654","Teaching hospital, affordable","022-23087300","9:00 AM - 5:00 PM","4.5","Mumbai"},
            {"Cessna Pet Clinic Andheri","vet","19.1136","72.8697","24hr emergency","022-66778899","24 hours","4.7","Mumbai"},
            {"Taj Lands End Mumbai","hotel","19.0413","72.8208","Pets with prior approval","022-66681234","Check-in 2PM","4.6","Mumbai"},

            // Pondicherry
            {"Promenade Beach Pondicherry","park","11.9342","79.8366","Morning dog walks popular","","Open all day","4.8","Pondicherry"},
            {"Auroville Walking Trails","park","12.0049","79.8107","Nature trails, pets welcome","","6:00 AM - 6:00 PM","4.7","Pondicherry"},
            {"Café des Arts Pondy","cafe","11.9350","79.8300","French café, dogs at terrace tables","0413-2222333","8:00 AM - 10:00 PM","4.7","Pondicherry"},
            {"PAWS Vet Pondicherry","vet","11.9400","79.8200","Full service vet","0413-9876543","9:00 AM - 8:00 PM","4.6","Pondicherry"},
            {"Villa Shanti Pondy","hotel","11.9347","79.8318","Boutique hotel, pets allowed","0413-2211123","Check-in 2PM","4.8","Pondicherry"}
        };

        for (String[] v : venues) {
            ContentValues cv = new ContentValues();
            cv.put("name", v[0]);
            cv.put("type", v[1]);
            cv.put("latitude", Double.parseDouble(v[2]));
            cv.put("longitude", Double.parseDouble(v[3]));
            cv.put("pet_rules", v[4]);
            cv.put("phone", v[5]);
            cv.put("open_hours", v[6]);
            cv.put("paw_score", Double.parseDouble(v[7]));
            cv.put("city", v[8]);
            cv.put("review_count", 10);
            db.insert(TABLE_VENUES, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP_STOPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    // ── VENUE METHODS ──
    public List<Venue> getAllVenues() {
        List<Venue> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_VENUES, null);
        while (c.moveToNext()) {
            Venue v = new Venue();
            v.id = c.getInt(c.getColumnIndexOrThrow("id"));
            v.name = c.getString(c.getColumnIndexOrThrow("name"));
            v.type = c.getString(c.getColumnIndexOrThrow("type"));
            v.latitude = c.getDouble(c.getColumnIndexOrThrow("latitude"));
            v.longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"));
            v.petRules = c.getString(c.getColumnIndexOrThrow("pet_rules"));
            v.phone = c.getString(c.getColumnIndexOrThrow("phone"));
            v.openHours = c.getString(c.getColumnIndexOrThrow("open_hours"));
            v.pawScore = c.getDouble(c.getColumnIndexOrThrow("paw_score"));
            v.city = c.getString(c.getColumnIndexOrThrow("city"));
            list.add(v);
        }
        c.close();
        return list;
    }

    public List<Venue> getVenuesByType(String type) {
        List<Venue> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_VENUES + " WHERE type=?", new String[]{type});
        while (c.moveToNext()) {
            Venue v = new Venue();
            v.id = c.getInt(c.getColumnIndexOrThrow("id"));
            v.name = c.getString(c.getColumnIndexOrThrow("name"));
            v.type = c.getString(c.getColumnIndexOrThrow("type"));
            v.latitude = c.getDouble(c.getColumnIndexOrThrow("latitude"));
            v.longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"));
            v.petRules = c.getString(c.getColumnIndexOrThrow("pet_rules"));
            v.phone = c.getString(c.getColumnIndexOrThrow("phone"));
            v.openHours = c.getString(c.getColumnIndexOrThrow("open_hours"));
            v.pawScore = c.getDouble(c.getColumnIndexOrThrow("paw_score"));
            v.city = c.getString(c.getColumnIndexOrThrow("city"));
            list.add(v);
        }
        c.close();
        return list;
    }

    public List<Venue> getVenuesByCity(String city) {
        List<Venue> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
            "SELECT * FROM " + TABLE_VENUES + " WHERE city LIKE ?",
            new String[]{"%" + city + "%"});
        while (c.moveToNext()) {
            Venue venue = new Venue();
            venue.id = c.getInt(c.getColumnIndexOrThrow("id"));
            venue.name = c.getString(c.getColumnIndexOrThrow("name"));
            venue.type = c.getString(c.getColumnIndexOrThrow("type"));
            venue.latitude = c.getDouble(c.getColumnIndexOrThrow("latitude"));
            venue.longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"));
            venue.petRules = c.getString(c.getColumnIndexOrThrow("pet_rules"));
            venue.phone = c.getString(c.getColumnIndexOrThrow("phone"));
            venue.openHours = c.getString(c.getColumnIndexOrThrow("open_hours"));
            venue.pawScore = c.getDouble(c.getColumnIndexOrThrow("paw_score"));
            venue.city = c.getString(c.getColumnIndexOrThrow("city"));
            list.add(venue);
        }
        c.close();
        return list;
    }

    public long insertVenue(Venue v) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", v.name); cv.put("type", v.type);
        cv.put("latitude", v.latitude); cv.put("longitude", v.longitude);
        cv.put("pet_rules", v.petRules); cv.put("phone", v.phone);
        cv.put("open_hours", v.openHours); cv.put("paw_score", v.pawScore);
        cv.put("city", v.city);
        return db.insert(TABLE_VENUES, null, cv);
    }

    public void updatePawScore(int venueId, double newScore) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("paw_score", newScore);
        db.update(TABLE_VENUES, cv, "id=?", new String[]{String.valueOf(venueId)});
    }

    // ── PET METHODS ──
    public long insertPet(Pet p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", p.name); cv.put("breed", p.breed);
        cv.put("age", p.age); cv.put("weight", p.weight);
        cv.put("health_notes", p.healthNotes);
        cv.put("is_senior", p.age > 7 ? 1 : 0);
        cv.put("owner_email", p.ownerEmail);
        return db.insert(TABLE_PETS, null, cv);
    }

    public List<Pet> getAllPets() {
        List<Pet> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PETS, null);
        while (c.moveToNext()) {
            Pet p = new Pet();
            p.id = c.getInt(c.getColumnIndexOrThrow("id"));
            p.name = c.getString(c.getColumnIndexOrThrow("name"));
            p.breed = c.getString(c.getColumnIndexOrThrow("breed"));
            p.age = c.getInt(c.getColumnIndexOrThrow("age"));
            p.weight = c.getDouble(c.getColumnIndexOrThrow("weight"));
            p.healthNotes = c.getString(c.getColumnIndexOrThrow("health_notes"));
            p.ownerEmail = c.getString(c.getColumnIndexOrThrow("owner_email"));
            list.add(p);
        }
        c.close();
        return list;
    }

    // ── TRIP METHODS ──
    public long insertTrip(Trip t) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", t.title); cv.put("origin", t.origin);
        cv.put("destination", t.destination); cv.put("date", t.date);
        cv.put("pet_id", t.petId); cv.put("notes", t.notes);
        return db.insert(TABLE_TRIPS, null, cv);
    }

    public List<Trip> getAllTrips() {
        List<Trip> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TRIPS + " ORDER BY id DESC", null);
        while (c.moveToNext()) {
            Trip t = new Trip();
            t.id = c.getInt(c.getColumnIndexOrThrow("id"));
            t.title = c.getString(c.getColumnIndexOrThrow("title"));
            t.origin = c.getString(c.getColumnIndexOrThrow("origin"));
            t.destination = c.getString(c.getColumnIndexOrThrow("destination"));
            t.date = c.getString(c.getColumnIndexOrThrow("date"));
            t.notes = c.getString(c.getColumnIndexOrThrow("notes"));
            list.add(t);
        }
        c.close();
        return list;
    }

    public void deleteTrip(int tripId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TRIPS, "id=?", new String[]{String.valueOf(tripId)});
        db.delete(TABLE_TRIP_STOPS, "trip_id=?", new String[]{String.valueOf(tripId)});
    }

    // ── HEALTH METHODS ──
    public long insertHealthRecord(HealthRecord h) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pet_id", h.petId); cv.put("record_type", h.recordType);
        cv.put("description", h.description); cv.put("date", h.date);
        cv.put("next_due", h.nextDue);
        return db.insert(TABLE_HEALTH, null, cv);
    }

    public List<HealthRecord> getHealthRecords(int petId) {
        List<HealthRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_HEALTH + " WHERE pet_id=?",
                new String[]{String.valueOf(petId)});
        while (c.moveToNext()) {
            HealthRecord h = new HealthRecord();
            h.id = c.getInt(c.getColumnIndexOrThrow("id"));
            h.recordType = c.getString(c.getColumnIndexOrThrow("record_type"));
            h.description = c.getString(c.getColumnIndexOrThrow("description"));
            h.date = c.getString(c.getColumnIndexOrThrow("date"));
            h.nextDue = c.getString(c.getColumnIndexOrThrow("next_due"));
            list.add(h);
        }
        c.close();
        return list;
    }

    // ── SETTINGS ──
    public void saveSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("key", key);
        cv.put("value", value);
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getSetting(String key, String defaultVal) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT value FROM " + TABLE_SETTINGS + " WHERE key=?", new String[]{key});
        if (c.moveToFirst()) {
            String val = c.getString(0);
            c.close();
            return val;
        }
        c.close();
        return defaultVal;
    }
}
