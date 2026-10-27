package com.example.midexam;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etStudentId;
    private CheckBox cbEnrolled;
    private TextView tvResult;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        cbEnrolled = findViewById(R.id.cbEnrolled);
        tvResult = findViewById(R.id.tvResult);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String id = etStudentId.getText().toString().trim();
            if (name.isEmpty() || id.isEmpty()) {
                toast("Name dan Student ID wajib diisi");
                return;
            }
            if (dbHelper.addStudent(id, name, cbEnrolled.isChecked())) {
                toast("Data berhasil ditambahkan");
                clearInput();
                showAll();
            } else {
                toast("Gagal menambah (Student ID mungkin sudah ada)");
            }
        });

        btnEdit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String id = etStudentId.getText().toString().trim();
            if (name.isEmpty() || id.isEmpty()) {
                toast("Name dan Student ID wajib diisi");
                return;
            }
            if (dbHelper.updateStudent(id, name, cbEnrolled.isChecked())) {
                toast("Data berhasil diupdate");
                clearInput();
                showAll();
            } else {
                toast("Student ID tidak ditemukan");
            }
        });

        btnDelete.setOnClickListener(v -> {
            String id = etStudentId.getText().toString().trim();
            if (id.isEmpty()) {
                toast("Isi Student ID yang mau dihapus");
                return;
            }
            if (dbHelper.deleteStudent(id)) {
                toast("Data berhasil dihapus");
                clearInput();
                showAll();
            } else {
                toast("Student ID tidak ditemukan");
            }
        });

        btnView.setOnClickListener(v -> showAll());
    }

    private void showAll() {
        Cursor c = dbHelper.getAllStudents();
        if (c.getCount() == 0) {
            tvResult.setText("Belum ada data.");
            c.close();
            return;
        }
        StringBuilder sb = new StringBuilder();
        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            String name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            boolean enrolled = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ENROLLED)) == 1;
            sb.append("ID: ").append(id).append("\n")
                    .append("Name: ").append(name).append("\n")
                    .append("Enrolled: ").append(enrolled ? "Yes" : "No").append("\n\n");
        }
        c.close();
        tvResult.setText(sb.toString());
    }

    private void clearInput() {
        etName.setText("");
        etStudentId.setText("");
        cbEnrolled.setChecked(false);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}