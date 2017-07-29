package com.example.armen.accesstoaddress.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.google.common.eventbus.Subscribe;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.armen.accesstoaddress.ui.activity.AddUrlActivity.ADD_URL;

public class UrlListFragment extends BaseFragment implements View.OnClickListener,
        UrlAdapter.OnItemClickListener, UrlAsyncQueryHandler.AsyncQueryListener, SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE = 100;

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private MenuItem mMenuSearch;
    private ImageView mIvAccess;
    private Bundle mArgumentData;
    private RecyclerView mRv;
    private FloatingActionButton mFloatingActionButton;

    private UrlAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<UrlModel> mUrlList;
    private UrlAsyncQueryHandler mUrlAsyncQueryHandler;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        mMenuSearch = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mMenuSearch);
        searchView.setOnClickListener(this);
        super.onCreateOptionsMenu(menu, inflater);
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

    public static boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
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
    public void onItemClick(UrlModel urlAddress, int position) {
    }

    @Override
    public void onItemLongClick(final UrlModel urlModel, final int position) {
        mUrlAsyncQueryHandler.deleteUrl(urlModel, position);
        mUrlList.remove(position);
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
                UrlModel uriModel = data.getParcelableExtra(ADD_URL);

                mUrlList.add(uriModel);
                mRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    }
    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ArrayList<UrlModel> UrlModel) {
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

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    private void loadData() {
        if (NetworkUtil.getInstance().isConnected(getActivity())) {
            UrlIntentService.start(
                    getActivity(),
                    Constant.API.URL_LIST,
                    Constant.RequestType.PRODUCT_LIST
            );

        } else {
            mUrlAsyncQueryHandler.getUrls();
        }
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
                ArrayList<UrlModel> products = CursorReader.parseUrls(cursor);
                mUrlList.clear();
                mUrlList.addAll(products);
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

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        newText = newText.toLowerCase();
//        ArrayList<UrlModel> ss = new ArrayList<>();
//        for(UrlModel model : mUrlList){
//            String name = model.getUrlAddress().toLowerCase();
//            if(name.contains(newText)){
//                ss.add(model);
//            }
//        }
//        mRecyclerViewAdapter.setFilter(ss);
        return true;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}