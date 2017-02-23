package com.yulin.customarcview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private CustomArcView mCustomArcView;
    private EditText mEdit;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdit = (EditText) findViewById(R.id.id_edit);
        mCustomArcView = (CustomArcView) findViewById(R.id.id_arc);
    }

    /**
     * Set ratio.
     * @param view
     */
    public void setRatio(View view) {
        double ratio = Double.valueOf(mEdit.getText().toString()) / 100;
        if(ratio < 0)
            ratio = 0;
        else if(ratio > 100)
            ratio = 1;
        mCustomArcView.setRatio(ratio);
    }
}
