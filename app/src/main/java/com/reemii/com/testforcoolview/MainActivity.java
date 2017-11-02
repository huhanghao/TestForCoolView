package com.reemii.com.testforcoolview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.reemii.com.testforcoolview.View.ProgressButton;
import com.reemii.com.testforcoolview.View.WriteView;

public class MainActivity extends AppCompatActivity {

    private WriteView mWriteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressButton mProgressButton = (ProgressButton) findViewById(R.id.imitateKeepButton);
        mWriteView = (WriteView) findViewById(R.id.write_view);
        TextView tvSave = (TextView) findViewById(R.id.tv_save);
        TextView tvClear = (TextView) findViewById(R.id.tv_clear);
        TextView tvJump = (TextView) findViewById(R.id.tv_jump);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWriteView.savePic();
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWriteView.clear();
            }
        });

        tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(MainActivity.this,NodeTreeActivity.class);
                startActivity(mIntent);
            }
        });


    }
}
