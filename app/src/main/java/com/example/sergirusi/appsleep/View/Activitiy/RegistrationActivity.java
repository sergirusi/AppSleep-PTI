package com.example.sergirusi.appsleep.View.Activitiy;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sergirusi.appsleep.Connection.HttpLogInGetConnection;
import com.example.sergirusi.appsleep.Connection.HttpPostConnection;
import com.example.sergirusi.appsleep.Presenter.RegistrationPresenter;
import com.example.sergirusi.appsleep.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    public Button loggin;
    private Button register;
    private Button next;
    private EditText username;
    private EditText password;
    private EditText repeat_password;
    private TextView title;
    private TextView info;
    private LinearLayout layout_repeat_pass;

    private RegistrationPresenter registrationPresenter;

    public String mode = "LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViews();
        setSupportActionBar(toolbar);
        registrationPresenter = new RegistrationPresenter(this);
        init();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        loggin = (Button) findViewById(R.id.loggin_btn);
        register = (Button) findViewById(R.id.register_btn);
        next = (Button) findViewById(R.id.next_btn);
        username = (EditText) findViewById(R.id.username_et);
        password = (EditText) findViewById(R.id.password_et);
        repeat_password = (EditText) findViewById(R.id.pass_repeat_et);
        title = (TextView) findViewById(R.id.title_tv);
        info = (TextView) findViewById(R.id.info_tv);
        layout_repeat_pass = (LinearLayout) findViewById(R.id.repeat_layout);
    }

    private void init() {
        if (mode.equals("LOG")) {
            layout_repeat_pass.setVisibility(View.GONE);
        }
        loggin.setOnClickListener(this);
        register.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loggin_btn:
                registrationPresenter.changeMode(loggin, register, layout_repeat_pass, title, info, next);
                break;
            case R.id.register_btn:
                registrationPresenter.changeMode(loggin, register, layout_repeat_pass, title, info, next);
                break;
            case R.id.next_btn:
                String n = username.getText().toString();
                String p = password.getText().toString();
                String repeat = repeat_password.getText().toString();
                JSONObject data = new JSONObject();
                try {
                    data.put("user", username.getText().toString());
                    data.put("passwd", password.getText().toString());
                    data.put("cam", false);
                    data.put("graf", 0);
                    data.put("temps", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mode.equals("REGISTER") && repeat.equals(p)) {
                    new HttpPostConnection().execute(String.valueOf(data), null);
                    setResult(Activity.RESULT_OK, getIntent().putExtra("name", n));
                    finish();
                } else if (mode.equals("LOG")) {
                    AsyncTask<String, String, JSONObject> asyncTask = new HttpLogInGetConnection().execute(n);
                    JSONObject obj = null;
                    try {
                        obj = asyncTask.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (obj == null) {
                        final Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.error_dialog);
                        dialog.setTitle("ERROR");
                        TextView tv = (TextView) dialog.findViewById(R.id.error_dialog_text);
                        tv.setText("L'usuari introduït no existeix.");
                        dialog.show();
                    } else {
                        try {
                            String match = obj.getString("passwd");
                            if (match.equals(password.getText().toString())) {
                                setResult(Activity.RESULT_OK, getIntent().putExtra("name", n));
                                finish();
                            } else {
                                final Dialog dialog = new Dialog(this);
                                dialog.setContentView(R.layout.error_dialog);
                                dialog.setTitle("ERROR");
                                TextView tv = (TextView) dialog.findViewById(R.id.error_dialog_text);
                                tv.setText("La contrassenya introduïda es incorrecta.");
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

}
