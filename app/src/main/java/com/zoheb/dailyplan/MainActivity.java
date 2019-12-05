package com.zoheb.dailyplan;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.Model.taskList;
import com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter.UserNameRecyclerViewAdapter;
import com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter.dailyTasksViewAdapter;
import com.zoheb.dailyplan.Retrofit.CallUtils;
import com.zoheb.dailyplan.Retrofit.RetrofitClient;
import com.zoheb.dailyplan.Retrofit.UserService;
import com.zoheb.dailyplan.SqlUtils.AndroidDbDatasource;
import com.zoheb.dailyplan.SqlUtils.DataSyncDao;
import com.zoheb.dailyplan.SqlUtils.TaskDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zoheb.dailyplan.CommonUtils.getRandomColor;
import static com.zoheb.dailyplan.CommonUtils.getUserColorCode;
import static com.zoheb.dailyplan.CommonUtils.getUserName;
import static com.zoheb.dailyplan.Retrofit.RetrofitErrorHelper.parseNetworkError;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private UserService userService;
    private RecyclerView recyclerView;
    private dailyTasksViewAdapter rcAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<DailyTask> daDialyTaskList = new ArrayList<>(),dailyTaskListDummy = new ArrayList<>();
    private AndroidDbDatasource db;
    private DataSyncDao dataSyncDao;
    private TaskDetailsViewModel detailsViewModel;
    private Boolean isFirst= true;
    private RelativeLayout initialsBackgroundColor,userSelect,initialsBackgroundColorDrawer;
    private FloatingSearchView floatingSearch;
    private DrawerLayout drawer;
    private RecyclerView userDataRecyclerView;
    private UserNameRecyclerViewAdapter userNameRecyclerViewAdapter;
    private List<String> employeeList = new ArrayList<>();
    private TextView userNameTextDrawer,initialsDataDrawer,initialsDataText;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        db= AndroidDbDatasource.getDatabase(this);
        dataSyncDao = db.dataSyncDao();
        recyclerView=findViewById(R.id.recyclerview);
        initialsBackgroundColor = findViewById(R.id.initialsBackgroundColor);
        drawer = findViewById(R.id.drawer_layout);
        userNameTextDrawer = findViewById(R.id.userNameTextDrawer);
        initialsDataDrawer = findViewById(R.id.initialsDataDrawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        userSelect = findViewById(R.id.userSelect);
        userDataRecyclerView = findViewById(R.id.recyclerviewUserList);
        userDataRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        userDataRecyclerView.setFocusable(false);
        initialsDataText = findViewById(R.id.initialsDataText);
        initialsBackgroundColorDrawer = findViewById(R.id.initialsBackgroundColorDrawer);
        new getEmployeeList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        floatingSearch = findViewById(R.id.floating_search_view);
        floatingSearch.setDimBackground(false);
        floatingSearch.attachNavigationDrawerToMenuButton(drawer);
        floatingSearch.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if(!newQuery.isEmpty()) {
                    rcAdapter.getFilter().filter(newQuery);
                }else {
                    daDialyTaskList=new ArrayList<>(dailyTaskListDummy);
                    rcAdapter = new dailyTasksViewAdapter(daDialyTaskList,MainActivity.this);
                    recyclerView.setAdapter(rcAdapter);
                }
            }
        });
        userSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.END)) {
                    drawer.closeDrawer(Gravity.END);
                } else {
                    floatingSearch.detachNavigationDrawerFromMenuButton(drawer);
                    drawer.openDrawer(Gravity.END);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setFocusable(false);
        userService = RetrofitClient.getClient(CommonUtils.url, MainActivity.this).create(UserService.class);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              getAllTaskList();
            }
        });
        detailsViewModel = ViewModelProviders.of(MainActivity.this).get(TaskDetailsViewModel.class);

        setData();

        mSwipeRefreshLayout.setRefreshing(true);
        getAllTaskList();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,CreateTaskActivity.class);
                startActivity(intent);
            }
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                floatingSearch.attachNavigationDrawerToMenuButton(drawer);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, SyncData.class));
        } else {
            startService(new Intent(MainActivity.this, SyncData.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getAllTaskList(){

        Call<taskList> tCall=userService.getAllTaskList();
        CallUtils.enqueueWithRetry(tCall,new Callback<taskList>() {
            @Override
            public void onResponse(Call<taskList> call, Response<taskList> response) {


                if(response.isSuccessful()) {
                    taskList daDialyTask = response.body();
                    daDialyTaskList = daDialyTask.getDialyTasks();
                    dataSyncDao.insertAllTasks(daDialyTaskList);

                    mSwipeRefreshLayout.setRefreshing(false);
                }else {

                }
            }

            @Override
            public void onFailure(Call<taskList> call, Throwable t) {
                parseNetworkError(MainActivity.this,t);
            }
        });
    }

    public void updateTaskStatus(final int srNo, final String status, final int position){
        mSwipeRefreshLayout.setRefreshing(true);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action","updateStatus")
                .addFormDataPart("srNo",srNo+"")
                .addFormDataPart("status",status+"")
                .build();

        Call<ResponseBody> tCall=userService.updateTask(requestBody);
        CallUtils.enqueueWithRetry(tCall,new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mSwipeRefreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Task Updated ",Toast.LENGTH_SHORT).show();
                    dataSyncDao.updateTask(status,srNo);

                }else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                parseNetworkError(MainActivity.this,t);
            }
        });
    }

    private class getEmployeeList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if(employeeList!=null) employeeList.add("Anonymous");
            employeeList.addAll(dataSyncDao.getAllEmployeeByScore());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userNameRecyclerViewAdapter = new UserNameRecyclerViewAdapter(employeeList,MainActivity.this);
            userDataRecyclerView.setAdapter(userNameRecyclerViewAdapter);
        }
    }

    public void setData(){
        detailsViewModel.getAllPendingTasks(getUserName(MainActivity.this)).observe(this, new Observer<List<DailyTask>>() {
            @Override
            public void onChanged(@Nullable List<DailyTask> dailyTasks) {
                dailyTaskListDummy = new ArrayList<>(dailyTasks);
                daDialyTaskList = new ArrayList<>(dailyTasks);
                if (isFirst) {
                    rcAdapter = new dailyTasksViewAdapter(daDialyTaskList, MainActivity.this);
                    recyclerView.setAdapter(rcAdapter);
                    isFirst = false;
                } else {
                    rcAdapter.updateData(daDialyTaskList);
                }
            }
        });


        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }
        String initialText = getUserName(MainActivity.this).substring(0,1);
        initialsDataDrawer.setText(initialText);
        userNameTextDrawer.setText(getUserName(MainActivity.this));
        ((GradientDrawable)initialsBackgroundColor.getBackground()).setColor(getUserColorCode(MainActivity.this));
        ((GradientDrawable)initialsBackgroundColorDrawer.getBackground()).setColor(getUserColorCode(MainActivity.this));
        initialsDataText.setText(initialText);
    }
}
