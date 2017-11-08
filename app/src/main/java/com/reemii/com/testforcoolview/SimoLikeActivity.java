package com.reemii.com.testforcoolview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SimoLikeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simo_like);

        View jump1 = findViewById(R.id.tv_jump1);

        jump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimoLikeActivity.this, MeasureActivity.class);
                startActivity(intent);
            }
        });
    }
}
