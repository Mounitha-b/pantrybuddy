package com.pantrybuddy.activity;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.pantrybuddy.R;
import com.pantrybuddy.server.Server;
import com.pantrybuddy.stubs.GlobalClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements IWebService{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageView imageAddItem;
    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Your Pantry");

        globalClass= (GlobalClass)getApplicationContext();
        Log.d("debug", "onCreate: Fetching details of the user");
        String emailId = globalClass.getEmail();
        Server server = new Server(this);
        JSONObject resp = new JSONObject();

        server.fetchUserProducts(emailId);
        imageAddItem=(ImageView)findViewById(R.id.imageView);
        imageAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AddItemActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem addProduct = menu.findItem(R.id.addIcon);
        addProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                  @Override
                                                  public boolean onMenuItemClick(MenuItem item) {
                                                      startActivity(new Intent(ProfileActivity.this, AddItemActivity.class));
                                                      return true;
                                                  }
                                              });

        return true;
    }


    @Override
    public void processResponse(JSONObject responseObj) throws JSONException {
        if (responseObj != null) {
            Log.d("response", "processResponse: " + responseObj);
            String code = responseObj.get("Code").toString();
            String message = responseObj.get("Message").toString();

            if (code != null && message != null) {
                JSONArray product_details = responseObj.getJSONArray("product_details");
                ImageView imageView = findViewById(R.id.imageView);
                TextView textView = findViewById(R.id.tvLoginMsg1);
                TextView textView1 = findViewById(R.id.tvPantryMsg1);


                if(product_details == null ||product_details.length() == 0){
                    textView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.GONE);
                    textView1.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    Log.d("response", "processResponse: " + responseObj);
                    ArrayList<UserProduct> exampleList = new ArrayList<>();

                    for(int i=0; i< product_details.length(); i++) {
                        JSONObject object =  product_details.getJSONObject(i);
                        exampleList.add(new UserProduct(object.getString("product_name"),object.getString("manufacturer"),object.getString("expiry_date"), object.getInt("count"), object.getString("image"), object.getString("ingredients"), object.getString("serving_size")));
                    }
                    mRecyclerView = findViewById(R.id.recycleView);
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

                    mAdapter = new ProductAdapter(this, exampleList);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                    boolean includeEdge = true;
                    mRecyclerView.addItemDecoration(new SpacesItemDecorator(1, 50, includeEdge));


                }



            }
        }
    }

}