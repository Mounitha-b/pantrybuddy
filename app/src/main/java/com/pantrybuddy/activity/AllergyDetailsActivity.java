package com.pantrybuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pantrybuddy.Interfaces.ProductsListener;
import com.pantrybuddy.R;
import com.pantrybuddy.server.Server;
import com.pantrybuddy.stubs.GlobalClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllergyDetailsActivity extends AppCompatActivity implements ProductsListener, IWebService {

    private Button next;
    GlobalClass globalClass;
    Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_details);
        RecyclerView productRecyclerView= findViewById(R.id.productsRecyclerView2);
        next= findViewById(R.id.btnAllergNext);
        globalClass= (GlobalClass)getApplicationContext();

        List<Product> products= new ArrayList<>();

        Product almonds = new Product();
        almonds.image=R.drawable.almonds;
        almonds.name="Almonds";
//        almonds.rating=5f;
//        almonds.manufacturedBy="---";
//        almonds.ingredients="Almonds";
        products.add(almonds);



        Product chocolate = new Product();
        chocolate.image=R.drawable.chocolate;
        chocolate.name="Chocolate";
//        chocolate.rating=4f;
//        chocolate.manufacturedBy="---";
//        chocolate.ingredients="Cocoa";
        products.add(chocolate);

        Product eggs = new Product();
        eggs.image=R.drawable.eggs;
        eggs.name="Eggs";
//        eggs.rating=4f;
//        eggs.manufacturedBy="---";
//        eggs.ingredients="Eggs";
        products.add(eggs);

        Product fish = new Product();
        fish.image=R.drawable.fish;
        fish.name="Fish";
//        fish.rating=2f;
//        fish.manufacturedBy="---";
//        fish.ingredients="Fish";
        products.add(fish);

        Product cowmilk = new Product();
        cowmilk.image=R.drawable.milk;
        cowmilk.name="Cow's Milk";
//        cowmilk.rating=5f;
//        cowmilk.manufacturedBy="---";
//        cowmilk.ingredients="Milk";
        products.add(cowmilk);

        Product walnuts = new Product();
        walnuts.image=R.drawable.walnuts;
        walnuts.name="Walnuts";
//        walnuts.rating=5f;
//        walnuts.manufacturedBy="---";
//        walnuts.ingredients="Walnuts";
        products.add(walnuts);

        Product wheat = new Product();
        wheat.image=R.drawable.wheat;
        wheat.name="Wheat";
//        wheat.rating=2f;
//        wheat.manufacturedBy="---";
//        wheat.ingredients="Wheat";
        products.add(wheat);

        final ProductsAdaptor productsAdaptor= new ProductsAdaptor( products,this);
        productRecyclerView.setAdapter(productsAdaptor);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Product> selectedProducts = productsAdaptor.getSelectedProducts();
                StringBuilder productNames=new StringBuilder();

                for(int i=0;i<selectedProducts.size();i++){
                    if(i==0){
                        productNames.append(selectedProducts.get(i).name);
                    }else{
                        productNames.append(",").append(selectedProducts.get(i).name);
                    }
                }
                //TODO : Save details to DB

                String regLastName = globalClass.getLastName();
                String regPassword = globalClass.getPasssword();
                String regFirstName = globalClass.getFirstName();
                String regMobile = globalClass.getNumber();
                String regEmail = globalClass.getEmail();

                saveAllergyDetails(regEmail,productNames);

            }
        });




    }



    @Override
    public void onProductsAction(Boolean isSelected) {

    }

    public void saveAllergyDetails(String emailId,StringBuilder productNames) {
        if(!productNames.equals("")) {
            server = new Server(this);
            server.saveAllergyDetails(emailId, productNames);
        }
    }

    public void processResponse(JSONObject responseObj) throws JSONException {
        if (responseObj != null) {
            String code = responseObj.get("Code").toString();
            String message = responseObj.get("Message").toString();
            if (code != null && message != null) {
                if (code.equalsIgnoreCase("200")) {

                    //startActivity(new Intent(RegistrationActivity.this, CongratulationsActivity.class));
                    startActivity(new Intent(AllergyDetailsActivity.this, ProfileActivity.class));

                    Toast.makeText(AllergyDetailsActivity.this, getString(R.string.msg_registration_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AllergyDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}