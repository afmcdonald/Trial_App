package com.example.aidan.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 1/31/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TITLE = "contactManager",
    TABLE_CONTACTS = "contacts",
    KEY_ID = "id",
    KEY_TITLE = "title",
    KEY_PRICE = "price",
    KEY_KEYWORDS= "keywords",
    KEY_DESCRIPTION = "description",
    KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context){
        super(context, DATABASE_TITLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE+ " TEXT," + KEY_PRICE + " TEXT," + KEY_KEYWORDS+ " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_IMAGEURI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void createContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, contact.getTitle());
        values.put(KEY_PRICE, contact.getPrice());
        values.put(KEY_KEYWORDS, contact.getKeywords());
        values.put(KEY_DESCRIPTION, contact.getDescription());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();

    }

    public Contact getContact(int id) {//reading one contact
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_TITLE, KEY_PRICE, KEY_KEYWORDS, KEY_DESCRIPTION, KEY_IMAGEURI}, KEY_ID + "=?", new String[] {String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        //get the key id
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return contact;
    }

    public void deleteContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[] {String.valueOf(contact.getId())});
        db.close();
    }

    public int getContactsCount(){
        //EX: SELECT * FROM CONTACTS
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);//second parameter is selection args (not needed)
        int count = cursor.getCount();
        db.close();
        cursor.close();


        return count;

    }

    public int updateContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, contact.getTitle());
        values.put(KEY_PRICE, contact.getPrice());
        values.put(KEY_KEYWORDS, contact.getKeywords());
        values.put(KEY_DESCRIPTION, contact.getDescription());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

        int rowsAffected = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[] {String.valueOf(contact.getId())});//how many rows were affected
        db.close();
        return rowsAffected;
    }

    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        if (cursor.moveToFirst()){
            Contact contact;
            do {
                contacts.add(new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)))); //add it to the list

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
