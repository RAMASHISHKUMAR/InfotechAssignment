package com.example.infotechassignment.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infotechassignment.Adapter.AssignmentAdapter;
import com.example.infotechassignment.R;
import com.example.infotechassignment.model.AssignmentResp;
import com.example.infotechassignment.retrofit.RestClient;
import com.example.infotechassignment.utils.Utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JsonParsingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String email_id = "interview@maishainfotech.com";
    AssignmentAdapter assignmentAdapter;
    AssignmentResp assignmentResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jason_parsing);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }

        recyclerView = findViewById(R.id.recycDetails);
        assignmentAdapter = new AssignmentAdapter(assignmentResp, getApplicationContext()); // as you want you dec or nor

        onService();
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
                        Toast.makeText(JsonParsingActivity.this, "response failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AssignmentResp> call, Throwable t) {
                    Toast.makeText(JsonParsingActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


}
