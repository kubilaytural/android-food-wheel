package com.adefruandta.spinningwheelandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SpinningWheelView.OnRotationListener<String> {

    private SpinningWheelView wheelView;

    private Button rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelView = (SpinningWheelView) findViewById(R.id.wheel);

        rotate = (Button) findViewById(R.id.rotate);

        wheelView.setItems(R.array.dummy);
        wheelView.setOnRotationListener(this);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 10 second
                // every 50 ms rander rotation
                executeGetMethod();
                wheelView.rotate(50, 3000, 50);
            }
        });
    }

    @Override
    public void onRotation() {
        Log.d("XXXX", "On Rotation");
    }

    @Override
    public void onStopRotation(String item) {
        Toast.makeText(this, item, Toast.LENGTH_LONG).show();
    }

    private void executeGetMethod() {
        StringRequest jsonForGetRequest = new StringRequest(
                Request.Method.GET,"https://developers.zomato.com/api/v2.1/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response",response.toString());
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    JSONObject jsonObject = null;
                    String errorMessage = null;

                    switch(response.statusCode){
                        case 400:
                            errorMessage = new String(response.data);

                            try {

                                jsonObject = new JSONObject(errorMessage);
                                String serverResponseMessage =  (String)jsonObject.get("hataMesaj");
                                Toast.makeText(getApplicationContext(),""+serverResponseMessage,Toast.LENGTH_LONG).show();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }
            }


        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("user-key", "b96c78b11e953d90b3916a2c6aeb22b2");

                return params;
            }


        };

        jsonForGetRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonForGetRequest);

    }
}
