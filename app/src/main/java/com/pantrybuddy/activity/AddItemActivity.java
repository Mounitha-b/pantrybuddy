package com.pantrybuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.TextRecognizer;
import com.pantrybuddy.R;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.CameraSource;
import com.pantrybuddy.server.Server;
import com.pantrybuddy.stubs.GlobalClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private Button bScanBarcode;
    private TextView eItemName;

    private TextView eDate;
    private Button bDone;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    private TextRecognizer textRecognizer;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String expiryDate;


    RequestQueue queue;
    private JsonObjectRequest jsonObjectRequest;
    GlobalClass globalClass;
    Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        bScanBarcode=findViewById(R.id.btnScanBarCode);
        eItemName=findViewById(R.id.mltItemName);

        eDate=findViewById(R.id.etDate);
        bDone=findViewById(R.id.btnDone);
        surfaceView=findViewById(R.id.svBarcodeScan);
        globalClass= (GlobalClass)getApplicationContext();
        queue = Volley.newRequestQueue(this);


//        bScanDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textRecognizer();
//            }
//        });

        eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year= cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day= cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog= new DatePickerDialog(AddItemActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                 expiryDate=Integer.toString(year)+"-"+Integer.toString(month+1)+"-"+Integer.toString(dayOfMonth);
            }
        };

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId=barcodeData;
                if(productId!=null && !productId.isEmpty()) {
                    String emailId = globalClass.getEmail();
                    Log.d("INFO", "Email of user :" + emailId);
                    //Calling the product API to store the product details.

                    server = new Server(getApplicationContext());
                    server.saveProduct(emailId, productId, expiryDate);
                }

                startActivity(new Intent(AddItemActivity.this,ProfileActivity.class));
            }
        });

        bScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Try to make the barcode like a pop up
                //startActivity(new Intent(AddItemActivity.this, BarcodeScannerActivity.class));
                surfaceView.setVisibility(View.VISIBLE);
                toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                barcodeText = findViewById(R.id.mltItemName);
                initialiseDetectorsAndSources(queue);
            }
        });

    }

//    private void textRecognizer() {
//
//        textRecognizer=new TextRecognizer.Builder(getApplicationContext().build());
//    }

    private void initialiseDetectorsAndSources(RequestQueue queue) {

        Toast.makeText(getApplicationContext(), getString(R.string.msg_point_camera), Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(AddItemActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.release();
                    cameraSource = null;
                }
                ;
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("debug", "barcodeText: "+barcodeText.getText());
                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                eItemName.setText(barcodeText.getText().toString());
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                eItemName.setText(barcodeText.getText().toString());
                                getAPIResult(queue);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                        }
                    });
                }
            }
        });
    }

    private void getAPIResult(RequestQueue queue) {
        String url = "https://api.upcdatabase.org/product/" + barcodeText.getText().toString() + "?apikey=5A7E28020FB2A4F78A8DE783FF2B3444\n";
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {



                    Log.d("INFO", "Webservice called :" + url );
                    String title=response.get("title").toString();
                    String descp=response.get("description").toString();
                    if(title!=null && !title.isEmpty()){
                        barcodeText.setText(title);
                    }else{
                        barcodeText.setText(descp);
                    }


                } catch (JSONException e) {
                    Log.d("ERROR", "ERROR in upc webservice " + e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "ERROR in upc webservice " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources(queue);
    }

}