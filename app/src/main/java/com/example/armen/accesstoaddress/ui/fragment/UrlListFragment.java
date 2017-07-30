package com.example.armen.accesstoaddress.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.armen.accesstoaddress.R;
import com.example.armen.accesstoaddress.db.cursor.CursorReader;
import com.example.armen.accesstoaddress.db.handler.UrlAsyncQueryHandler;
import com.example.armen.accesstoaddress.db.pojo.UrlModel;
import com.example.armen.accesstoaddress.io.bus.BusProvider;
import com.example.armen.accesstoaddress.io.service.UrlIntentService;
import com.example.armen.accesstoaddress.ui.activity.AddUrlActivity;
import com.example.armen.accesstoaddress.ui.adapter.UrlAdapter;
import com.example.armen.accesstoaddress.util.Constant;
import com.example.armen.accesstoaddress.util.NetworkUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.armen.accesstoaddress.ui.activity.AddUrlActivity.ADD_URL;
import static com.example.armen.accesstoaddress.util.Constant.API.ACCESS_EXIST;
import static com.example.armen.accesstoaddress.util.Constant.API.NO_EXIST;

public class UrlListFragment extends BaseFragment implements View.OnClickListener,
        UrlAdapter.OnItemClickListener , UrlAsyncQueryHandler.AsyncQueryListener, SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE = 100;

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private static Boolean mCurrentBoolean;
    private Button mBtnRefresh;
    private ImageView mIvAccess;
    private SearchView searchView;
    private FloatingActionButton mFloatingActionButton;
    private MenuItem mMenuSearch;
    private RecyclerView mRv;
    private UrlAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<UrlModel> mUrlList;
    private UrlAsyncQueryHandler mUrlAsyncQueryHandler;

    public UrlListFragment() {
    }

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
        loadData();
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu);
        mMenuSearch = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(mMenuSearch);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_name:
                mRecyclerViewAdapter.sortByName();
                mRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_sort_by_name_rev:
                mRecyclerViewAdapter.sortByNameRev();
                mRecyclerViewAdapter.notifyDataSetChanged();
                break;
        }
        return true;
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
            case R.id.btn_refresh:
                //TODO refresh
        }
    }


    @Override
    public void onItemClick(UrlModel urlAddress, int position) {
        Toast.makeText(getContext(), urlAddress.getUrlAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(final UrlModel urlModel, final int position) {
        openDeleteProductDialog(urlModel, position);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    private void setListeners() {
        mFloatingActionButton.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);
    }


    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
                UrlModel uriModel = data.getParcelableExtra(ADD_URL);
                exists(uriModel);
                if(mCurrentBoolean != null){
                    if(mCurrentBoolean){
                        uriModel.setImage(ACCESS_EXIST);
                    }else{
                        uriModel.setImage(NO_EXIST);
                    }
                }
                mUrlList.add(uriModel);
                mRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_url_list);
        mBtnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fl_btn_url_add);
        mIvAccess = (ImageView) view.findViewById(R.id.iv_item_access_checker);
    }

    private void init() {
        mUrlAsyncQueryHandler = new UrlAsyncQueryHandler(getActivity().getApplicationContext(), this);

        mRv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getActivity());
        mRv.setLayoutManager(mLlm);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mUrlList = new ArrayList<>();
        mRecyclerViewAdapter = new UrlAdapter(mUrlList, this);
        mRv.setAdapter(mRecyclerViewAdapter);
    }


    private void loadData() {
        if (NetworkUtil.getInstance().isConnected(getActivity())) {
            UrlIntentService.start(
                    getActivity(),
                    Constant.API.URL_LIST,
                    Constant.RequestType.URL_LIST
            );

        } else {
            mUrlAsyncQueryHandler.getUrls();
        }
    }

    public void exists(final UrlModel urlModel) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(urlModel.getUrlAddress());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(Constant.RequestMethod.HEAD);
                    con.connect();
                    Log.i(LOG_TAG, "con.getResponseCode() IS : " + con.getResponseCode());
                    mCurrentBoolean = false;
                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.i(LOG_TAG, "Sucess");
                        mCurrentBoolean = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(LOG_TAG, "fail");
                    mCurrentBoolean = false;
                }
            }
        }.start();
    }

    @Override
    public void onResume() {
        mUrlAsyncQueryHandler.getUrls();
        super.onResume();
    }


    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.GET_URLS:
                ArrayList<UrlModel> urlModels = CursorReader.parseUrls(cursor);
                mUrlList.clear();
                mUrlList.addAll(urlModels);
                mRecyclerViewAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.DELETE_URL:
                int position = (int) cookie;
                mUrlList.remove(position);
                mRecyclerViewAdapter.notifyItemRemoved(position);
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<UrlModel> listForSearch = new ArrayList<>();
        for (UrlModel model : mUrlList) {
            String name = model.getUrlAddress().toLowerCase();
            if (name.contains(newText)) {
                listForSearch.add(model);
            }
        }
        mRecyclerViewAdapter.setFilter(listForSearch);
        return true;
    }


    private void openDeleteProductDialog(final UrlModel urlModel, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_you_sure)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUrlAsyncQueryHandler.deleteUrl(urlModel, position);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}