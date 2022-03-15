package com.example.barberbusiness.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barberbusiness.MapsActivity;
import com.example.barberbusiness.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class AddBarbershopActivity extends AppCompatActivity{

    private ImageView backBtn;
    private TextInputEditText shopName, email, password, phoneNumber, address;
    private MaterialButton addButton;
    private String barberShopAddress;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barbershop);

        backBtn = findViewById(R.id.addBarbershopBackButton);
        backBtn.setOnClickListener(backBtnListener);
        addButton = findViewById(R.id.addBarbershopAddBtn);
        addButton.setOnClickListener(addClickListener);

        shopName = findViewById(R.id.addBarbershopNameEditText);
        email = findViewById(R.id.addBarbershopEmailEditText);
        password = findViewById(R.id.addBarbershopPasswordEditText);
        phoneNumber = findViewById(R.id.addBarbershopPhoneNumberEditText);
        address = findViewById(R.id.addBarbershopAddressEditText);
        address.setOnClickListener(addressClickListener);
    }



    private View.OnClickListener addressClickListener = new View.OnClickListener() { //open google map to select location
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivityForResult(intent,1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) { // get data from google map activity
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            String result = data.getStringExtra("address");
            String coordinates[] = result.split("/");
            address.setText(coordinates[0]);
            latitude = coordinates[1];
            longitude = coordinates[2];
            barberShopAddress = coordinates[0] + "/" + latitude + "/" + longitude;
            Log.d("debug", "onActivityResult: latitude = " + latitude + "  longitude = " + longitude);

        }
    }

    private View.OnClickListener backBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private View.OnClickListener addClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(shopName.length() > 0 && phoneNumber.length() > 0 && email.length() > 5 && address.length() > 0 && password.length() > 7 ) {
                //Start ProgressBar first (Set visibility VISIBLE)
                addButton.setClickable(false);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Starting Write and Read data with URL
                        //Creating array for parameters
                        String[] field = new String[6];
                        field[0] = "ShopName";
                        field[1] = "PhoneNumber";
                        field[2] = "Email";
                        field[3] = "Address";
                        field[4] = "Password";
                        field[5] = "Status";
                        //Creating array for data
                        String[] data = new String[6];
                        data[0] = String.valueOf(shopName.getText());
                        data[1] = String.valueOf(phoneNumber.getText());
                        data[2] = String.valueOf(email.getText());
                        data[3]= barberShopAddress;
                        data[4]= String.valueOf(password.getText());
                        data[5]= "hidden";
                        Log.d("php" , data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5]);
                        PutData putData = new PutData("http://192.168.100.6/barbershop-php/registerBarbershop.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if(result.equals("Register Success")){
                                    Log.d("php", result);
                                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                                    startActivity(intent);
                                }
                                else { // All fields are required
                                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                    Log.d("php", result);
                                    addButton.setClickable(true);
                                }
                            }
                        }
                        //End Write and Read data with URL
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(),"All fields are required !",Toast.LENGTH_LONG).show();
            }
        }
    };


}