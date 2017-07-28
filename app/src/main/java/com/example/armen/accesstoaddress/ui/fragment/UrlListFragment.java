package com.example.armen.accesstoaddress.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.armen.accesstoaddress.R;
import com.example.armen.accesstoaddress.io.bus.BusProvider;
import com.example.armen.accesstoaddress.io.rest.HttpRequestManager;
import com.example.armen.accesstoaddress.io.service.UrlListIntentService;
import com.example.armen.accesstoaddress.pojo.UrlModel;
import com.example.armen.accesstoaddress.ui.activity.AddUrlActivity;
import com.example.armen.accesstoaddress.ui.adapter.UrlAdapter;
import com.example.armen.accesstoaddress.util.Constant;
import com.example.armen.accesstoaddress.util.NetworkUtil;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.armen.accesstoaddress.ui.activity.AddUrlActivity.ADD_URL;

public class UrlListFragment extends BaseFragment implements View.OnClickListener,
        UrlAdapter.OnItemClickListener {

    private static final int REQUEST_CODE = 100;

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ImageView mIvAccess;
    private Bundle mArgumentData;
    private RecyclerView mRv;
    private FloatingActionButton mFloatingActionButton;

    private UrlAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<UrlModel> mUriList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static UrlListFragment newInstance() {
        return new UrlListFragment();
    }

    public static UrlListFragment newInstance(Bundle args) {
        UrlListFragment fragment = new UrlListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_url_list, container, false);
        BusProvider.register(this);
        findViews(view);
        init();
        setListeners();
        getData();
        customizeActionBar();
        loadContacts();

        try {
            Log.d(LOG_TAG," mtela log");
            Log.d(LOG_TAG, String.valueOf(exists("https://stackoverflow.com/questions/26418486/check-if-url-exists-or-not-on-server")));
            Log.d(LOG_TAG, String.valueOf(exists("google.com")));
            Log.d(LOG_TAG, String.valueOf(exists("faceboasdnasdaok.com")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_sort_by_name:
//                Intent intent = new Intent(getContext(), AddUrlActivity.class);
//                break;
//        }
//        return true;
//    }

    public boolean exists(String url) throws IOException {
        try {
            HttpURLConnection.setFollowRedirects(false);
            URL ulr = new URL(url);
            HttpURLConnection con =  (HttpURLConnection) ulr.openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_btn_url_add:
                Intent intent = new Intent(getActivity(), AddUrlActivity.class);
                this.startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }


    @Override
    public void onItemClick(UrlModel contact, int position) {
    }

    @Override
    public void onItemLongClick(UrlModel contact, int position) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
                UrlModel uriModel = data.getParcelableExtra(ADD_URL);
                mUriList.add(uriModel);
//                mTlAsyncQueryHandler.getProduct(id);
                mRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    }
    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ArrayList<UrlModel> UrlModel) {
        mUriList.clear();
        mUriList.addAll(UrlModel);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mFloatingActionButton.setOnClickListener(this);
    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_url_list);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fl_btn_url_add);
        mIvAccess = (ImageView) view.findViewById(R.id.iv_item_access_checker);
    }

    private void init() {
        mRv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getActivity());
        mRv.setLayoutManager(mLlm);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mUriList = new ArrayList<>();
        mRecyclerViewAdapter = new UrlAdapter(mUriList, this);
        mRv.setAdapter(mRecyclerViewAdapter);
    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    private void loadContacts() {
        if (NetworkUtil.getInstance().isConnected(getActivity())) {
            UrlListIntentService.start(
                    getActivity(),
                    Constant.API.CONTACT_LIST,
                    HttpRequestManager.RequestType.URL_LIST
            );
        } else {
            Toast.makeText(getContext(), "internet chka", Toast.LENGTH_SHORT).show();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}