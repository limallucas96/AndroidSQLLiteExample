package com.example.lucas.androidsqlliteexample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = "test";

    private EditText editText;
    private Button button;
    private ListView listView;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayAdapter<String> taskAdapter;
    private ArrayList<String> listTasks;
    private ArrayList<Integer> listIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextSearch_id);
        button = findViewById(R.id.buttonSearch_id);
        listView = findViewById(R.id.listView_id);

        try {

            //creating database
            sqLiteDatabase = openOrCreateDatabase("taskaap", MODE_PRIVATE, null);

            //creating table
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tasks" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "task VARCHAR)");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String taskText = editText.getText().toString();
                    saveTask(taskText);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, ""+listIds.get(position));
                    removeTask(listIds.get(position));
                }
            });

            getTasks();

        }catch (Exception e){
            Log.i(TAG, e.toString());
        }
    }

    private void saveTask(String task){
        try{
            if(!task.isEmpty()){
                sqLiteDatabase.execSQL("INSERT INTO tasks (task) VALUES ('" + task + "')");
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                getTasks();
                editText.setText("");
            }
            else{
                Toast.makeText(this, "Cannot insert null value", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getTasks(){
        try{
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tasks ORDER BY id DESC", null);

            int indexIdColumn = cursor.getColumnIndex("id");
            int indexTaskColumn = cursor.getColumnIndex("task");

            listTasks = new ArrayList<String>();
            listIds = new ArrayList<Integer>();

            taskAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listTasks);

            listView.setAdapter(taskAdapter);

            cursor.moveToFirst();

            while (cursor != null){
                Log.i(TAG, String.format("%s, %s", cursor.getString(indexIdColumn), cursor.getString(indexTaskColumn)));
                listTasks.add(cursor.getString(indexTaskColumn));
                listIds.add(Integer.valueOf(cursor.getString(indexIdColumn)));
                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removeTask(Integer id){
        try{
            sqLiteDatabase.execSQL("DELETE FROM tasks WHERE id ="+id);
            getTasks();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
