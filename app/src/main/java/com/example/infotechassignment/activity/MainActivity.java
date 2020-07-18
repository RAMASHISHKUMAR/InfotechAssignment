package com.example.infotechassignment.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.infotechassignment.Adapter.AssignmentAdapter;
import com.example.infotechassignment.R;
import com.example.infotechassignment.model.AssignmentResp;
import com.example.infotechassignment.retrofit.RestClient;
import com.example.infotechassignment.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String email_id = "interview@maishainfotech.com";
    AssignmentAdapter assignmentAdapter;
    List<Response> responses = new ArrayList<>();
    List<AssignmentResp> assignmentRespList = new ArrayList<>();
    Context context;
    AssignmentResp assignmentResp;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;

    public int TIMECOUNT = 10000;
    public boolean ISTRAVERSING = true;
    public double longitude, latitude;
    Handler handler = new Handler();
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycDetails);
        assignmentAdapter = new AssignmentAdapter(assignmentResp, getApplicationContext()); // as you want you dec or nor
        button = findViewById(R.id.location);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NearByRestaurantsActivity.class);
                startActivity(intent);
            }
        });
        onService();

//        gpsTrackingWithOutServiceClass();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.removeCallbacks(sendData);
//                ISTRAVERSING = false;
//
//                gpsTrackingWithOutServiceClass();
//            }
//        }, 5000);                ///  update gps location
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void onService() {

        RequestBody emailId = RequestBody.create(MediaType.parse("text/plain"), email_id);

        if (Utils.isInternetConnected(this)) {
            Utils.showProgressDialog(this);

            RestClient.getDetails(emailId, new Callback<AssignmentResp>() {
                @Override
                public void onResponse(Call<AssignmentResp> call, Response<AssignmentResp> response) {
                    Utils.dismissProgressDialog();

                    if (response != null && response.code() == 200 && response.body() != null) {
                        if (response.body().getResponse() != null) {
                            if (response.body().getResponse().size() > 0) {
                                assignmentResp = response.body();

                                assignmentAdapter = new AssignmentAdapter(assignmentResp, getApplicationContext());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(assignmentAdapter);

                                assignmentAdapter.notifyDataSetChanged();

                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "response failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AssignmentResp> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


/////////////location update//////////////////////

//    private void gpsTrackingWithOutServiceClass() {
//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(ACCESS_COARSE_LOCATION);
//
//        permissionsToRequest = findUnAskedPermissions(permissions);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (permissionsToRequest.size() > 0)
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//        }
//
//        locationTrack = new LocationTrack(getApplicationContext());
//
//        if (locationTrack.canGetLocation()) {
//
//            longitude = locationTrack.getLongitude();
//            latitude = locationTrack.getLatitude();
////            Toast.makeText(getActivity(), "Longitude:" + Double.toString(longitude) + "\n" +
////                    "Latitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
//
//            ISTRAVERSING = true;
//            handler.postDelayed(sendData, TIMECOUNT);
//
//        } else {
//
//            locationTrack.showSettingsAlert();
//        }
//    }
//
//
////    private Runnable sendData = new Runnable() {
////        public void run() {
////            try {
////                //prepare and send the data here..
////                // gpsTrackingWithServiceClass();
////                if (ISTRAVERSING) {
////                    handler.postDelayed(sendData, TIMECOUNT);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////    };
//
//
//    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
//        ArrayList<String> result = new ArrayList<String>();
//
//        for (String perm : wanted) {
//            if (!hasPermission(perm)) {
//                result.add(perm);
//            }
//        }
//
//        return result;
//    }
//
//    private boolean hasPermission(String permission) {
//        if (canMakeSmores()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                return (getApplicationContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
//            }
//        }
//        return true;
//    }
//
//    private boolean canMakeSmores() {
//        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        switch (requestCode) {
//
//            case ALL_PERMISSIONS_RESULT:
//                for (String perms : permissionsToRequest) {
//                    if (!hasPermission(perms)) {
//                        permissionsRejected.add(perms);
//                    }
//                }
//
//                if (permissionsRejected.size() > 0) {
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
//                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    });
//                            return;
//                        }
//                    }
//
//                }
//
//                break;
//        }
//
//    }
//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(getApplicationContext())
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
    /////////////location update//////////////////////


}
