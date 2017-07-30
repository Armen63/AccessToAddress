package com.example.armen.accesstoaddress.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.armen.accesstoaddress.R;
import com.example.armen.accesstoaddress.db.handler.UrlAsyncQueryHandler;
import com.example.armen.accesstoaddress.db.pojo.UrlModel;
import com.example.armen.accesstoaddress.ui.fragment.UrlListFragment;

import static com.example.armen.accesstoaddress.util.Constant.API.LOADING;

public class AddUrlActivity extends BaseActivity implements View.OnClickListener, UrlAsyncQueryHandler.AsyncQueryListener {

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
    private UrlModel mUrl;
    private UrlAsyncQueryHandler mUrlAsyncQueryHandler;

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
        init();
        setListeners();
        customizeActionBar();
    }

    private void init() {
        mUrlAsyncQueryHandler = new UrlAsyncQueryHandler(this, this);
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

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.ADD_URL:
                Intent result = new Intent(this, UrlListFragment.class);
                result.putExtra(ADD_URL, mUrl.getId());
                setResult(RESULT_OK, result);
                finish();
                break;
        }
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }


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
                    mUrl = new UrlModel(
                            System.currentTimeMillis(),
                            mEtAddressUrl.getText().toString(),
                            LOADING
                    ));
            mUrlAsyncQueryHandler.addUrl(mUrl);
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