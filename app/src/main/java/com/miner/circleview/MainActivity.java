package com.miner.circleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.miner.circleview.view.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView imgView01;
    private CircleImageView imgView02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView01 = (CircleImageView) findViewById(R.id.circleImgView01);
        imgView02 = (CircleImageView) findViewById(R.id.circleImgView02);
    }
}
