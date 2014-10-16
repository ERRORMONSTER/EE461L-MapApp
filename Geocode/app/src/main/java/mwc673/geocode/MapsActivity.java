package mwc673.geocode;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.drive.query.Query;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText editText;
    private Button button_traffic;
    private Button button_random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        editText = (EditText) findViewById(R.id.locationRequest);
        editText.setOnEditorActionListener(new OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                List<Address> addresses;
                Geocoder gc = new Geocoder(getApplicationContext());
                double lat = 0.0;
                double lng = 0.0;
                int attempts=0;
                do{
                    try{
                        mMap.clear();
                        addresses = gc.getFromLocationName(v.getText().toString(),1);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        return true;
                    }
                    attempts++;
                }while(addresses == null && attempts < 100);

                Log.d("test","Attempts: "+attempts);
                if(addresses!=null&&addresses.size()>0){
                    Log.d("test","Raw address(0): "+addresses.get(0).getAddressLine(0));
                }
                Address address = addresses.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address.getAddressLine(0)));

                CameraUpdate zoomLevel = CameraUpdateFactory.zoomTo(15);
                mMap.animateCamera(zoomLevel);

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
                mMap.moveCamera(center);
                return false;

            }
        });
        button_traffic = (Button)findViewById(R.id.trafficButton);
        button_traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.isTrafficEnabled()==true){
                    mMap.setTrafficEnabled(false);
                }
                else if(mMap.isTrafficEnabled()==false){
                    mMap.setTrafficEnabled(true);
                }
            }
        });
        button_random = (Button)findViewById(R.id.randomButton);
        button_random.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){
                Geocoder gc = new Geocoder(getApplicationContext());
                double lat;
                double lng;
                boolean land = false;
               List<Address> addresses=null;
               int attempts = 0;
               do {
                   try {
                       mMap.clear();
                       lat=Math.random()*90;
                       lng=(Math.random()*360)-180;
                       addresses = gc.getFromLocation(lat, lng, 1);
                   } catch (Exception e) {
                       e.printStackTrace();
                       return;
                   }
                   attempts++;
               } while (addresses == null && attempts < 100);
               if(addresses==null||addresses.size()==0){
                   return;
               }
               mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(addresses.get(0).getAddressLine(0)));

               CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
               mMap.moveCamera(center);

               CameraUpdate zoomLevel = CameraUpdateFactory.zoomTo(15);
               mMap.animateCamera(zoomLevel);
            }
        });
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * INCLUDED AS BASE SOURCE CODE BY ANDROID STUDIO
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * INCLUDED AS BASE SOURCE CODE BY ANDROID STUDIO
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
