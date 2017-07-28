package com.example.armen.accesstoaddress.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.armen.accesstoaddress.R;
import com.example.armen.accesstoaddress.pojo.UrlModel;

public class AddUrlActivity extends BaseActivity implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AddUrlActivity.class.getSimpleName();
    public static final String ADD_URL = "ADD_URL";
    private static final String EMPTY = "";
    // ===========================================================
    // Fields
    // ===========================================================

    private EditText mEtAddressUrl;
    private Button mBtnAdd;
    private UrlModel mUri;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        setListeners();
        customizeActionBar();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_url_add:
                addUrlAddress();
                break;
        }
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void addUrlAddress() {
        if (mEtAddressUrl.getText().toString().equals(EMPTY))
            Toast.makeText(this, "Empty data", Toast.LENGTH_SHORT).show();
        else {
            Intent data = new Intent();
            data.putExtra(
                    ADD_URL,
                    mUri = new UrlModel(
                            System.currentTimeMillis(),
                            mEtAddressUrl.getText().toString()
                    ));
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void setListeners() {
        mBtnAdd.setOnClickListener(this);
    }

    private void findViews() {
        mEtAddressUrl = (EditText) findViewById(R.id.et_activity_add);
        mBtnAdd = (Button) findViewById(R.id.btn_url_add);
    }

    private void customizeActionBar() {
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}