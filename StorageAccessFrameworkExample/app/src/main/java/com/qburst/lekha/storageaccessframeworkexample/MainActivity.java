package com.qburst.lekha.storageaccessframeworkexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {

    private static EditText textView;

    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNewButton();
        initOpenButton();
    }

    private void initOpenButton() {
        Button button = (Button) findViewById(R.id.open_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocument();
            }
        });
    }

    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpg");
        startActivityForResult(intent, OPEN_REQUEST_CODE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        textView = (EditText) findViewById(R.id.fileText);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == OPEN_REQUEST_CODE) {
                Uri uri = resultData.getData();
                launchEditorActivity(uri);
            }

        }
    }

    private void launchEditorActivity(Uri uri) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void initNewButton() {
        Button button = (Button) findViewById(R.id.new_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditorActivity(null);            }
        });
    }

}
