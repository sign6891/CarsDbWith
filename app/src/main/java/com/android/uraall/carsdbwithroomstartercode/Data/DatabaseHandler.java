package com.android.uraall.carsdbwithroomstartercode.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.android.uraall.carsdbwithroomstartercode.Model.Car;
import com.android.uraall.carsdbwithroomstartercode.Utils.Util;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }


    //SQLiteDatabase - Для управления базой данных SQLite существует класс SQLiteDatabase.


    @Override
    //onCreate() — вызывается при первом создании базы данных
    public void onCreate(SQLiteDatabase db) {
        //SQL - Structured Query Language
        String CREATE_CARS_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "("
                + Util.KEY_ID + " INTEGER PRIMARY KEY,"// автозаполнение id новыми значениями
                + Util.KEY_NAME + " TEXT,"
                + Util.KEY_PRICE + " TEXT" + ")";
        //метод execSQL() позволяет выполнять любой допустимый код на языке SQL применимо
        // к таблицам базы данных
        db.execSQL(CREATE_CARS_TABLE);

    }

    @Override
    //onUpgrade() — вызывается при модификации базы данных или ее удалении
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        onCreate(db);
    }

    //Для вставки новой записи в базу данных SQLite используется метод insert():
    public long insertCar(String name, String price) {


        SQLiteDatabase db = this.getWritableDatabase();

        //Для создания новой строки понадобится объект ContentValues, точнее, его метод put()
        ContentValues contentValues = new ContentValues();

        //Объект ContentValues представляет собой пару name/value данных.
        contentValues.put(Util.KEY_NAME, name);
        contentValues.put(Util.KEY_PRICE, price);


        long id = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();


        return id;
    }

    //Для чтения данных используют вызов метода query()
    public Car getCar(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Если какой-то параметр для запроса вас не интересует, то оставляете null

        //Объект Cursor, возвращаемый методом query(), обеспечивает доступ к набору записей
        // результирующей выборки. Для обработки возвращаемых данных объект Cursor имеет набор
        // методов для чтения каждого типа данных — getString(), getInt() и getFloat().
        Cursor cursor = db.query(Util.TABLE_NAME, new String[]{Util.KEY_ID, Util.KEY_NAME,
                        Util.KEY_PRICE}, Util.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Car car = new Car(Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));

        cursor.close();

        return car;
    }

    public List<Car> getAllCars() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Car> carsList = new ArrayList<>();

        String selectAllCars = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllCars, null);

        //moveToFirst() — перемещает курсор на первую строку в результате запроса;
        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(Integer.parseInt(cursor.getString(0)));
                car.setName(cursor.getString(1));
                car.setPrice(cursor.getString(2));

                carsList.add(car);
                //moveToNext() — перемещает курсор на следующую строку;
            } while (cursor.moveToNext());
        }

        cursor.close();

        return carsList;
    }

    //Для обновления записей в базе данных используют соответственно метод update()
    public int updateCar(Car car) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.KEY_NAME, car.getName());
        contentValues.put(Util.KEY_PRICE, car.getPrice());

        //имя таблицы, обновленный объект ContentValues и оператор WHERE, указывающий на
        // строку (строки), которую нужно обновить.
        return db.update(Util.TABLE_NAME, contentValues, Util.KEY_ID + "=?",
                new String[]{String.valueOf(car.getId())});
    }

    //Для удаления записей в базе данных используют соответственно методы delete()
    public void deleteCar(Car car) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.TABLE_NAME, Util.KEY_ID + "=?",
                new String[]{String.valueOf(car.getId())});

        db.close();
    }

    public int getCarsCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}
