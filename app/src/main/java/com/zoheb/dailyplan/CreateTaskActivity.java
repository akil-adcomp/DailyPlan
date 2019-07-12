package com.zoheb.dailyplan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.zoheb.dailyplan.Model.DailyTask;
import com.zoheb.dailyplan.RecylerViews.RecylerViewAdapter.chipTextViewAdapter;
import com.zoheb.dailyplan.Retrofit.CallUtils;
import com.zoheb.dailyplan.Retrofit.RetrofitClient;
import com.zoheb.dailyplan.Retrofit.UserService;
import com.zoheb.dailyplan.SqlUtils.AndroidDbDatasource;
import com.zoheb.dailyplan.SqlUtils.DataSyncDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zoheb.dailyplan.CommonUtils.getSimpleDate;
import static com.zoheb.dailyplan.CommonUtils.hideKeyboardFrom;
import static com.zoheb.dailyplan.CommonUtils.notNullOrEmpty;
import static com.zoheb.dailyplan.Retrofit.RetrofitErrorHelper.parseNetworkError;

public class CreateTaskActivity extends AppCompatActivity{

    private UserService userService;
    private Button createTask,cancel;
    private EditText taskName,projectName,assignTo,startDate,endDate,comments;
    private DatePickerDialog.OnDateSetListener dateStart;
    private DatePickerDialog.OnDateSetListener dateEnd;
    private final Calendar myCalendarStart = Calendar.getInstance();
    private final Calendar myCalendarEnd = Calendar.getInstance();
    private String myFormat = "dd-MM-yy"; //In which you need put here
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private RecyclerView recyclerView;
    private chipTextViewAdapter chipTextViewAdapter;
    View.OnFocusChangeListener listener;

    Calendar cal = Calendar.getInstance();
    List<String> chipTextData = new ArrayList<>(),chipTextDataDummy = new ArrayList<>();
    List<String> chipTextProjectName = new ArrayList<>(),chipTextProjectNameDummy = new ArrayList<>();

    AndroidDbDatasource db;
    DataSyncDao sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        createTask = findViewById(R.id.createTask);
        cancel = findViewById(R.id.cancel);
        taskName = findViewById(R.id.taskName);
        projectName = findViewById(R.id.projectName);
        assignTo = findViewById(R.id.assignTo);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        comments = findViewById(R.id.comments);
        recyclerView=findViewById(R.id.recyclerview);
        cancel = findViewById(R.id.cancel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateTaskActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        db= AndroidDbDatasource.getDatabase(getApplicationContext());
        sd=db.dataSyncDao();
        listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("iNSIDE FOCUS CHANGE");
                switch(v.getId()){
                    case R.id.projectName:
                        if(hasFocus) {
                            chipTextViewAdapter = new chipTextViewAdapter(chipTextProjectName, projectName);
                        }else {
                            chipTextViewAdapter = new chipTextViewAdapter(new ArrayList<String>(),null);
                        }
                        recyclerView.setAdapter(chipTextViewAdapter);
                        break;

                    case R.id.assignTo:
                        if (hasFocus) {
                            chipTextViewAdapter = new chipTextViewAdapter(chipTextData, assignTo);
                        }else {
                            chipTextViewAdapter = new chipTextViewAdapter(new ArrayList<String>(),null);
                        }
                        recyclerView.setAdapter(chipTextViewAdapter);
                        break;

                    default:
                        chipTextViewAdapter = new chipTextViewAdapter(new ArrayList<String>(),null);
                        recyclerView.setAdapter(chipTextViewAdapter);
                        break;
                }
            }
        };

        taskName.setOnFocusChangeListener(listener);
        projectName.setOnFocusChangeListener(listener);
        assignTo.setOnFocusChangeListener(listener);
        startDate.setOnFocusChangeListener(listener);
        endDate.setOnFocusChangeListener(listener);
        comments.setOnFocusChangeListener(listener);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        projectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newQuery = s.toString();
                if(!newQuery.isEmpty()) {
                    chipTextViewAdapter.getFilter().filter(newQuery);
                }else {
                    chipTextProjectName=new ArrayList<>(chipTextProjectNameDummy);
                    chipTextViewAdapter = new chipTextViewAdapter(chipTextProjectName,projectName);
                    recyclerView.setAdapter(chipTextViewAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        assignTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newQuery = s.toString();
                if(!newQuery.isEmpty()) {
                    chipTextViewAdapter.getFilter().filter(newQuery);
                }else {
                    chipTextData=new ArrayList<>(chipTextDataDummy);
                    chipTextViewAdapter = new chipTextViewAdapter(chipTextData,assignTo);
                    recyclerView.setAdapter(chipTextViewAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        assignTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                System.out.println("iNSIDE ASSIGN TO");
//                chipTextViewAdapter = new chipTextViewAdapter(chipTextData);
//                recyclerView.setAdapter(chipTextViewAdapter);
//            }
//        });


        new getMatserList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, day);
                startDate.setText(sdf.format(myCalendarStart.getTime()));

            }
        };

        dateEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, day);
                endDate.setText(sdf.format(myCalendarEnd.getTime()));

            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().getDecorView().clearFocus();
                hideKeyboardFrom(CreateTaskActivity.this,view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, dateStart, myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH), myCalendarStart.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().getDecorView().clearFocus();
                hideKeyboardFrom(CreateTaskActivity.this,view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, dateEnd, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        userService = RetrofitClient.getClient(CommonUtils.url2, CreateTaskActivity.this).create(UserService.class);


        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValid()) {
                    DailyTask dt = new DailyTask();
                    dt.setProject(projectName.getText().toString());
                    dt.setTask(taskName.getText().toString());
                    dt.setAssignTo(assignTo.getText().toString());
                    dt.setEndDate(myCalendarEnd.getTime());
                    dt.setStartDate(myCalendarStart.getTime());
                    dt.setComments(comments.getText().toString());
                    dt.setStatus("Pending");
                    createTask(dt);
                }
            }
        });




    }

    private Boolean isValid(){

        if(!notNullOrEmpty(taskName)){
            taskName.setError("Field is required");
            taskName.requestFocus();

         return false;
        } else if(!notNullOrEmpty(projectName)){
            projectName.setError("Field is required");
            projectName.requestFocus();
            return false;
        } else if(!notNullOrEmpty(assignTo)){
            assignTo.setError("Field is required");
            assignTo.requestFocus();
            return false;
        } else if(!notNullOrEmpty(endDate)){
            endDate.setError("Field is required");
            endDate.requestFocus();
            return false;
        } else if(!notNullOrEmpty(startDate)){
            startDate.setError("Field is required");
            startDate.requestFocus();
            return false;
        }else {
            return true;
        }

    }

    private class getMatserList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            chipTextData = sd.getAllEmployeeByScore();
            chipTextDataDummy = new ArrayList<>(chipTextData);

            chipTextProjectName = sd.getAllProjectByScore();
            chipTextProjectNameDummy = new ArrayList<>(chipTextProjectName);
            return null;
        }
    }

    public void createTask(final DailyTask dialyTaskdata){
        final ProgressDialog pd = new ProgressDialog(CreateTaskActivity.this);
        pd.setMessage("Creating task...");
        pd.setCancelable(false);
        pd.show();
        createTask.setEnabled(false);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action","addItem")
               .addFormDataPart("taskName",dialyTaskdata.getTask()+"")
               .addFormDataPart("projectName",dialyTaskdata.getProject()+"")
               .addFormDataPart("startDate",getSimpleDate(dialyTaskdata.getStartDate()))
               .addFormDataPart("endDate",getSimpleDate(dialyTaskdata.getEndDate()))
               .addFormDataPart("assignTo",dialyTaskdata.getAssignTo()+"")
               .addFormDataPart("status",dialyTaskdata.getStatus()+"")
               .addFormDataPart("comments",dialyTaskdata.getComments()+"")
                .build();

        Call<ResponseBody> tCall=userService.createTask(requestBody);
        CallUtils.enqueueWithRetry(tCall,new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                createTask.setEnabled(true);
                if(response.isSuccessful()) {

                    sd.incrementScoreByNameForEmployee(dialyTaskdata.getAssignTo());
                    sd.incrementScoreByNameForProject(dialyTaskdata.getProject());
                    Toast.makeText(CreateTaskActivity.this,"Task Created ",Toast.LENGTH_SHORT).show();
                    finish();
                }else {

                }
                pd.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                createTask.setEnabled(true);
                pd.dismiss();
                parseNetworkError(CreateTaskActivity.this,t);
            }
        });
    }



}
