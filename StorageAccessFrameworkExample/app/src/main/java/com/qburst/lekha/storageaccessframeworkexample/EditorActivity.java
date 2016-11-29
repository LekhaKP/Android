package com.qburst.lekha.storageaccessframeworkexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class EditorActivity extends AppCompatActivity {

    private static final int CREATE_REQUEST_CODE = 40;
    private static final int READ_REQUEST_CODE = 42;
    private Uri mUri;

    private EditText mEditText;
    private ImageView imageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mUri = intent.getData();

        initTitle(mUri);
        initEditText(mUri);
        initSaveButton();
    }

    private void initEditText(Uri uri) {
        mEditText = (EditText) findViewById(R.id.edit_text);

        if (uri == null)
            return;

        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStream = getContentResolver().openInputStream(mUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            mEditText.setText(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.forceClose(inputStream);
        }

    }

    private void initTitle(Uri uri) {
        if (uri == null) {
            setTitle("Untitled");
            return;
        } else {
            Cursor cursor = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                cursor = getContentResolver().query(uri, null, null, null, null, null);
            }

            try {
                cursor.moveToFirst();

                String[] columnNames = cursor.getColumnNames();
                for (String columnName : columnNames) {
                    Log.d("EditorActivity", columnName);
                }

                String displayName = cursor.getString(cursor
                        .getColumnIndex(OpenableColumns.DISPLAY_NAME));
                setTitle(displayName);
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    IOUtil.forceClose(cursor);
                }
            }
        }

    }

    private void initSaveButton() {
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUri == null) {
                    createNewFile();
                } else {
                    try {
                        String text = mEditText.getText().toString();
                        save(mUri, text);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void createNewFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "Untitled.txt");
        startActivityForResult(intent, CREATE_REQUEST_CODE);
    }


    private void save(Uri uri, String text) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(text);
            writer.flush();
        } finally {
            IOUtil.forceClose(outputStream);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK)
                return;

            try {
                String text = mEditText.getText().toString();
                mUri = data.getData();
                initTitle(mUri);
                save(mUri, text);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                Log.i("TAG", "Uri: " + uri.toString());
                showImage(uri);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showImage(Uri uri) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Point p = new Point();
        this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        p.x = displaymetrics.widthPixels / 2;
        p.y = displaymetrics.heightPixels / 3;
        int popupWidth = 80 * displaymetrics.widthPixels / 100;
        int popupHeight = displaymetrics.heightPixels;
        LinearLayout viewGroup = (LinearLayout) this.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pop_layout, viewGroup);
        imageSelected = (ImageView) this.findViewById(R.id.imageView);
        final PopupWindow popup = new PopupWindow(this);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        layout.setBackgroundColor(Color.WHITE);
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - (80 * p.x / 100), p.y / 2);

        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    null;
            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            imageSelected.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                boolean result = delete(mUri);

                if (result) {
                    Toast.makeText(getApplicationContext(), "The file has been deleted",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to delete the file",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean delete(Uri uri) {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            result = DocumentsContract.deleteDocument(getContentResolver(), uri);
        }
        return result;
    }

}
