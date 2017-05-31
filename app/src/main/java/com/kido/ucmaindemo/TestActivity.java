package com.kido.ucmaindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author Kido
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mUcNewsBarButton;
    private Button mOnlyUcNewsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        bindViews();
    }

    private void bindViews() {
        mUcNewsBarButton = (Button) findViewById(R.id.ucNewsBar_button);
        mOnlyUcNewsButton = (Button) findViewById(R.id.onlyUcNews_button);

        mUcNewsBarButton.setOnClickListener(this);
        mOnlyUcNewsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ucNewsBar_button:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.onlyUcNews_button:
                startActivity(new Intent(this, OnlyUcNewsActivity.class));
                break;
        }
    }
}
