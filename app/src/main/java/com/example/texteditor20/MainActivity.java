package com.example.texteditor20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveButton;
    private Spinner fontSpinner, sizeSpinner;
    private String selectedFont, selectedSize;
    private SharedPreferences sharedPreferences; //для сохранения текущих настроек
    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 1001;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        saveButton = findViewById(R.id.saveButton);
        fontSpinner = findViewById(R.id.fontSpinner);
        sizeSpinner = findViewById(R.id.sizeSpinner);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); //инициализация объекта sharedPreferences

        // Создание адаптера для fontSpinner
        ArrayAdapter<CharSequence> fontAdapter = ArrayAdapter.createFromResource(this, R.array.fonts_array, android.R.layout.simple_spinner_item);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(fontAdapter);

        // Создание адаптера для sizeSpinner
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this, R.array.sizes_array, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        // Установка слушателя выбора элемента для fontSpinner
        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedFont = adapterView.getItemAtPosition(position).toString();
                applyFont();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedFont = "";
            }
        });

        // Установка слушателя выбора элемента для sizeSpinner
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSize = adapterView.getItemAtPosition(position).toString();
                applyFont();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedSize = "";
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                // Проверка выбора шрифта и размера шрифта
                if (TextUtils.isEmpty(selectedFont) || TextUtils.isEmpty(selectedSize)) {
                    Toast.makeText(MainActivity.this, "Please select font and size", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Сохранение текста и настроек в файл
                saveTextToFile(MainActivity.this, "text_file.txt", text, selectedFont, Float.parseFloat(selectedSize));
            }
        });
    }

    private void applyFont() {
        if (!TextUtils.isEmpty(selectedFont) && !TextUtils.isEmpty(selectedSize)) {
            String text = editText.getText().toString();
            Typeface typeface = Typeface.create(selectedFont, Typeface.NORMAL);
            float textSize = Float.parseFloat(selectedSize);
            editText.setTypeface(typeface);
            editText.setTextSize(textSize);
            editText.setText(text);
        }
    }

    private void saveTextToFile(Context context, String fileName, String text, String font, float fontSize) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(text);
            outputStreamWriter.write("Font: " + font);
            outputStreamWriter.write("Size: " + fontSize);

            outputStreamWriter.close();
            fileOutputStream.close();
            Toast.makeText(MainActivity.this, "Text saved to file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedFont = sharedPreferences.getString("font", "");
        selectedSize = sharedPreferences.getString("size", "");
        editText.setText(sharedPreferences.getString("text", ""));
        applyFont();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Сохранение текущих настроек и текста
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("font", selectedFont);
        editor.putString("size", selectedSize);
        editor.putString("text", editText.getText().toString());
        editor.apply();
    }
}