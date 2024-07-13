package com.example.todo.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todo.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE ="todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop the order Table
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        //create Table again
        onCreate(db);
    }

    public void openDatabase(){
        try {
            db = this.getWritableDatabase();
            Log.d("DatabaseHandler", "Database opened successfully: " + db.getPath());
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error opening database", e);
        }
    }

    public void insertTask(ToDoModel task){
        if (db == null) {
            Log.e("DatabaseHandler", "Database is not opened yet.");
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTask(){
        if (db == null) {
            Log.e("DatabaseHandler", "Database is not opened yet.");
            return new ArrayList<>();
        }
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null,null,null,null,null,null,null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        int idIndex = cur.getColumnIndex(ID);
                        int taskIndex = cur.getColumnIndex(TASK);
                        int statusIndex = cur.getColumnIndex(STATUS);

                        if(idIndex != -1 && taskIndex != -1 && statusIndex != -1){
                            task.setId(cur.getInt(idIndex));
                            task.setTask(cur.getString(taskIndex));
                            task.setStatus(cur.getInt(statusIndex));
                            taskList.add(task);
                        }else{
                            Log.e("Error", "Column not found");
                        }
                    }while (cur.moveToNext());
                }
            }
        }
        catch (Exception e){
            Log.e("Error", "Exception while getting task", e);
        }
        finally {
            db.endTransaction();
            if(cur != null) {
                cur.close();
            }
        }
        return taskList;

    }
    public void updateStatus(int id, int status){
        if (db == null) {
            Log.e("DatabaseHandler", "Database is not opened yet.");
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task){
        if (db == null) {
            Log.e("DatabaseHandler", "Database is not opened yet.");
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        if (db == null) {
            Log.e("DatabaseHandler", "Database is not opened yet.");
            return;
        }
        db.delete(TODO_TABLE, ID + "=?", new String[] {String.valueOf(id)});
    }

}
