package com.example.armen.accesstoaddress.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import static com.example.armen.accesstoaddress.util.Constant.API.LOADING;
import static com.example.armen.accesstoaddress.util.Constant.API.NO_ACCESS;

public class UrlListFragment extends BaseFragment implements View.OnClickListener,
        UrlAdapter.OnItemClickListener, UrlAsyncQueryHandler.AsyncQueryListener, SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE = 100;

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlListFragment.class.getSimpleName();
    private static final String TAG = UrlListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Button mBtnRefresh;
    private ImageView mIvAccess;
    private SearchView searchView;
    private FloatingActionButton mFloatingActionButton;
    private MenuItem mMenuSearch;
    private RecyclerView mRv;
    private UrlAdapter mAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<UrlModel> mUrlList;
    private ArrayList<UrlModel> mUrlListSafe;
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
                mAdapter.sortByName(mUrlListSafe);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_sort_by_name_rev:
                mAdapter.sortByNameRev(mUrlListSafe);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_sort_by_access:
                mAdapter.sortByAccess(mUrlListSafe);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_sort_displayAll:
                mAdapter.displayAll(mUrlListSafe);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_sort_by_time:
                mAdapter.sortByTime(mUrlListSafe);
                mAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.search(newText, mUrlListSafe);
        return true;
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================
    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.GET_URLS:
                ArrayList<UrlModel> urlModels = CursorReader.parseUrls(cursor);
                mUrlList.clear();
                mUrlList.addAll(urlModels);
                mUrlListSafe = new ArrayList<>(mUrlList);

                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.UPDATE_URL:
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        switch (token) {
            case UrlAsyncQueryHandler.QueryToken.DELETE_URL:
                int position = (int) cookie;
                mUrlList.remove(position);
                mAdapter.notifyItemRemoved(position);
                break;
        }
    }

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

                long time = System.currentTimeMillis();
                new MyTask(uriModel).execute(uriModel.getUrlAddress());
                int respTime = (int) (System.currentTimeMillis() - time);

/* porcelem senc iran hanem noric dnem vor lists refresh lini...loading@ koruma erp vor norc mtnum em Url List*/
                Fragment frg = null;
                frg = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                /*****************************************************************************/
                uriModel.setResponseTime(respTime);
                mUrlAsyncQueryHandler.addUrl(uriModel);

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
        mAdapter = new UrlAdapter(mUrlList, this);
        mRv.setAdapter(mAdapter);
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

    @Override
    public void onResume() {
        mUrlAsyncQueryHandler.getUrls();
        super.onResume();
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

    public class MyTask extends AsyncTask<String, Void, String> {

        UrlModel model;
        boolean checker;

        public MyTask(UrlModel model) {
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            model.setImage(LOADING);
            mUrlAsyncQueryHandler.updateUrl(model);

            mUrlList.clear();
            mUrlList.add(model);
            mAdapter.notifyDataSetChanged();

        }

        @Override
        protected String doInBackground(String... param) {
            try {
                String address = param[0];
                URL url = new URL(address);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(Constant.RequestMethod.HEAD);
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    checker = true;
                    return "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return "0";
        }


        @Override
        protected void onPostExecute(String image) {
            super.onPostExecute(image);
            if (checker) {
                model.setImage(ACCESS_EXIST);
                mUrlAsyncQueryHandler.updateUrl(model);
                mAdapter.notifyDataSetChanged();


            } else {
                model.setImage(NO_ACCESS);
                mUrlAsyncQueryHandler.updateUrl(model);
                mAdapter.notifyDataSetChanged();

            }
        }
    }

}