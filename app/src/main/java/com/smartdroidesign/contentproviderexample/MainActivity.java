package com.smartdroidesign.contentproviderexample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView contactNames;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactNames = findViewById(R.id.contact_names);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS);
        Log.d(TAG, "onCreate: checkSelfPermission = " + hasReadContactPermission);

        // Check permission
        if(hasReadContactPermission == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "onCreate: permission granted");
            READ_CONTACTS_GRANTED = true;
        } else {
            Log.d(TAG, "onCreate: requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "fab onClick: starts");
                String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                // Content Resolver is queried for data, and it returns a cursor.
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, // Source of data.
                        projection, // String[] array to store the name of the columns retrieved.
                        null, // String that contains a filter to determine which rows are returned. (It's a WHERE clause, and null = get me all rows(
                        null, // Array of values used to replace the placeholders in the selection string
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY); // This is like the ORDER BY clause

                if (cursor != null) {
                    List<String> contacts = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                    }
                    cursor.close();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.contact_detail, R.id.name, contacts);
                    contactNames.setAdapter(adapter);
                }
                Log.d(TAG, "fab onClick: ends");
            }
        });
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: starts");
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS: {
                // If request is cancelled, the rest array is empty.
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Permission was granted
                    // Do the contacts related task you need to do
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    READ_CONTACTS_GRANTED = true;
                } else {
                    // permission denied!
                    // functionality that depends on this permission
                    Log.d(TAG, "onRequestPermissionsResult: permission refused");
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
