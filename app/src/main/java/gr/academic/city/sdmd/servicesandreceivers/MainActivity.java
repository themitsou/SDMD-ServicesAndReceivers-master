package gr.academic.city.sdmd.servicesandreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver getAllStudentsResultBroadcastReceiver;

    {
        getAllStudentsResultBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String issuesResults = intent.getStringExtra(StudentService.EXTRA_STUDENTS_RESULT);

                Issues issues = new Gson().fromJson(issuesResults, Issues.class);

                String result = "";

                for (int i = 0; i < issues.getIssues().length; i++) {
                    Issue issue = issues.getIssue(i);
                    result += issue.getSubject() + "\n";
                }

                TextView resultsTextView = (TextView) findViewById(R.id.tv_results);
                resultsTextView.setText(result);
            }
        };
    }

    private BroadcastReceiver createStudentResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String resultMsg = intent.getStringExtra(StudentService.EXTRA_CREATE_STUDENT_RESULT);

            Toast.makeText(MainActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String firstName = ((TextView) findViewById(R.id.txt_first_name)).getText().toString();
            String lastName = ((TextView) findViewById(R.id.txt_last_name)).getText().toString();
            String age = ((TextView) findViewById(R.id.txt_age)).getText().toString();

            insertStudent(firstName, lastName, age);
            }
        });

        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStudents();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter getStudentsResultIntentFilter = new IntentFilter(StudentService.ACTION_GET_STUDENTS_RESULT);
        broadcastManager.registerReceiver(getAllStudentsResultBroadcastReceiver, getStudentsResultIntentFilter);

        IntentFilter createStudentResultIntentFilter = new IntentFilter(StudentService.ACTION_CREATE_STUDENT_RESULT);
        broadcastManager.registerReceiver(createStudentResultBroadcastReceiver, createStudentResultIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(getAllStudentsResultBroadcastReceiver);
        broadcastManager.unregisterReceiver(createStudentResultBroadcastReceiver);
    }

    private void insertStudent(String firstName, String lastName, String age) {
        Intent intent = new Intent(this, StudentService.class);
        intent.setAction(StudentService.ACTION_CREATE_STUDENT);

        intent.putExtra(StudentService.EXTRA_FIRST_NAME, firstName);
        intent.putExtra(StudentService.EXTRA_LAST_NAME, lastName);
        intent.putExtra(StudentService.EXTRA_AGE, age);

        startService(intent);
    }

    private void getAllStudents() {
        Intent intent = new Intent(this, StudentService.class);
        intent.setAction(StudentService.ACTION_GET_STUDENTS);

        startService(intent);
    }
}
