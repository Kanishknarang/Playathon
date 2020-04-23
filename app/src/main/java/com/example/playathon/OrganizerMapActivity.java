package com.example.playathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OrganizerMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;

    private DatabaseReference availableplayersdatabaseRef;
    private DatabaseReference organizerdatabaseRef;
    private LatLng organizerLocation;

    private int radius = 5;
    private String participantfoundId;

    private DatabaseReference databaseRef;
    private DatabaseReference organizedSports;
    private FirebaseAuth mAuth;

    private String userId;

    private Button logoutBtn;
    private Button searchPlayersbtn;

    private Boolean findPlayers =true;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();

        intent = getIntent();

        logoutBtn = (Button) findViewById(R.id.organization_map_logout_btn);

        userId = mAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId).child("sport");
        databaseRef.setValue(intent.getStringExtra("sport"));
        databaseRef = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId).child("venue");
        databaseRef.setValue(intent.getStringExtra("venue"));

        organizerdatabaseRef = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId).child("location");
        availableplayersdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Participants Available");

        searchPlayersbtn = (Button) findViewById(R.id.search_btn);

        searchPlayersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoFire geoFire = new GeoFire(organizerdatabaseRef);
                geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(),lastLocation.getLongitude()));
                organizerLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(organizerLocation).title("your location"));
                searchPlayersbtn.setText("finding players");
                getnearbyPlayers();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(OrganizerMapActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findPlayers = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId);
        databaseRef.removeValue();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    public void getnearbyPlayers(){
        GeoFire geoFire = new GeoFire(availableplayersdatabaseRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(organizerLocation.latitude,organizerLocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                participantfoundId = key;

                DatabaseReference participantReference = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId).child("found players").child(participantfoundId);
                participantReference.setValue(true);

                organizedSports = FirebaseDatabase.getInstance().getReference().child("organized sports").push();
                organizedSports.child("organizer").setValue(userId);
                organizedSports.child("sport").setValue(intent.getStringExtra("sport"));
                organizedSports.child("venue").setValue(intent.getStringExtra("venue"));

                FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("sports").child("organized").child(organizedSports.getKey()).setValue(true);




            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Intent intent = new Intent(OrganizerMapActivity.this, foundPlayersActivity.class);
                intent.putExtra("sportId",organizedSports.getKey());
                startActivity(intent);



            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}
