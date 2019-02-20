package com.example.vyaparivyvasayicongressapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText name, address, phone, email;
    TextView area_code_label, area_label;
    Spinner area_spinner, area_code_spinner, status_spinner;
    Button button;
    Context context;
    HashMap<String, String> User_Details = new HashMap<>();
    HashMap<Integer, String> status_map = new HashMap<>();
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressBar bar;
    ArrayList<String> AreaList = new ArrayList<>();
    HashMap<Object, Object> object;
    ArrayAdapter<String> area_adapter;
    ArrayAdapter<String> area_code_adapter;
    ArrayAdapter<CharSequence> status_adapter;
    ArrayList<String> area = new ArrayList<>();
    String area_code = "", areas = "", contact = "";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String status_selected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = this;
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        button = findViewById(R.id.register);
        bar = findViewById(R.id.bar);
        area_code_label = findViewById(R.id.area_code_label);
        area_label = findViewById(R.id.area_label);
        area_code_spinner = findViewById(R.id.area_code_spinner);
        area_spinner = findViewById(R.id.area_spinner);
        status_spinner = findViewById(R.id.spinner);
        setInVisibles();
        // Create an ArrayAdapter using the string array and a default spinner layout
        status_adapter = ArrayAdapter.createFromResource(context,
                R.array.status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        status_spinner.setAdapter(status_adapter);
        database = FirebaseDatabase.getInstance();
        status_map.put(1, "president");
        status_map.put(2, "general_secretary");
        status_map.put(3, "vice_president");
        status_map.put(4, "secretary");
        status_map.put(5, "treasurer");
        status_map.put(6, "executive_member");
        status_map.put(7, "members");
        getAreaList();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(name.getText())) {
                    name.setError("Name is required!");
                    return;
                } else if (TextUtils.isEmpty(address.getText())) {
                    address.setError("Address is required!");
                    return;
                } else if (TextUtils.isEmpty(phone.getText()) || phone.getText().length() != 10) {
                    phone.setError("Enter a valid phone number!");
                    return;
                } else if (TextUtils.isEmpty(email.getText())) {
                    email.setError("Email is required!");
                    return;
                } else if (status_spinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Please select the status!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (status_spinner.getSelectedItemPosition() == 7)
                    updateMember();
                else {
                    status_selected = (String) status_spinner.getSelectedItem();
                    checkStatus(status_spinner.getSelectedItemPosition());
                }

            }
        });


    }

    void updateMember() {
        contact = phone.getText().toString();
        reference = database.getReference("Area/" + areas + '/' + area_code + "/members/" + contact);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Toast.makeText(context, "Member Already Registered!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RegisterMember();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void checkStatus(int selected_position) {
        final String status = status_map.get(selected_position);
        reference = database.getReference("Area/" + areas + '/' + area_code + '/' + status);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datasnap", String.valueOf(dataSnapshot));
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Object> object = (HashMap<String, Object>) dataSnapshot.getValue();
                    boolean registered = false;
                    registered = (boolean) object.get("registered");

                    if (registered)
                        Toast.makeText(context, status_selected + " already registered!", Toast.LENGTH_SHORT).show();
                    else
                        RegisterUser(status);
                } else {
                    RegisterUser(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void getAreaList() {

        reference = database.getReference("ListArea");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setVisibles();
                if (dataSnapshot.exists()) {
                    object = (HashMap<Object, Object>) dataSnapshot.getValue();
                    for (Map.Entry<Object, Object> entry : object.entrySet()) {
                        String key = (String) entry.getKey();
                        area.add(key);
                    }
                    area_adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, area);
                    area_code_adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item,
                            (List<String>) object.get(area.get(0)));
                    area_spinner.setAdapter(area_adapter);
                    area_code_spinner.setAdapter(area_code_adapter);
                    setAreaAdapter();
                    setAreaCodeAdapter();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Database read error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setAreaAdapter() {
        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = (String) area_spinner.getSelectedItem();
                areas = selected_item;
                area_code_adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item,
                        (List<String>) object.get(selected_item));
                area_code_spinner.setAdapter(area_code_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setAreaCodeAdapter() {
        area_code_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area_code = (String) area_code_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void RegisterUser(String status) {
        reference = database.getReference("Area/" + areas + '/' + area_code + "/" + status);
        HashMap<String, Object> details = new HashMap<>();
        details.put("name", name.getText().toString());
        details.put("address", address.getText().toString());
        details.put("phone", phone.getText().toString());
        details.put("email", email.getText().toString());
        details.put("timestamp", dateFormat.format(new Date()));
        details.put("status",status_spinner.getSelectedItem());
        details.put("registered", true);
        reference.updateChildren(details, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(context, "Registration success!", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });

    }

    void RegisterMember() {

        reference = database.getReference("Area/" + areas + '/' + area_code + "/members/" + contact);
        HashMap<String, Object> details = new HashMap<>();
        details.put("name", name.getText().toString());
        details.put("address", address.getText().toString());
        details.put("phone", phone.getText().toString());
        details.put("email", email.getText().toString());
        details.put("status",status_spinner.getSelectedItem());
        details.put("timestamp", dateFormat.format(new Date()));
        reference.updateChildren(details, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(context, "Registration success!", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });
    }

    void clearFields() {
        name.setText("");
        phone.setText("");
        email.setText("");
        address.setText("");
    }

    void setVisibles() {
        bar.setVisibility(View.GONE);
        area_label.setVisibility(View.VISIBLE);
        area_spinner.setVisibility(View.VISIBLE);
        area_code_label.setVisibility(View.VISIBLE);
        area_code_spinner.setVisibility(View.VISIBLE);
    }

    void setInVisibles() {
        bar.setVisibility(View.VISIBLE);
        area_label.setVisibility(View.INVISIBLE);
        area_spinner.setVisibility(View.INVISIBLE);
        area_code_label.setVisibility(View.INVISIBLE);
        area_code_spinner.setVisibility(View.INVISIBLE);
    }
}
