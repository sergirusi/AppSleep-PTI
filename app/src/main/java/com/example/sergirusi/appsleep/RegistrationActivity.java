package com.example.sergirusi.appsleep;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loggin;
    private Button register;
    private Button next;
    private EditText username;
    private EditText password;
    private EditText repeat_password;
    private TextView title;
    private TextView info;
    private LinearLayout layout_repeat_pass;

    private View inflated_layout;

    private String mode = "LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
        init();
    }

    private void findViews() {
//        inflated_layout = (View) getLayoutInflater().inflate(R.layout.loggin_layout, null);
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
                changeMode();
                break;
            case R.id.register_btn:
                changeMode();
                break;
            case R.id.next_btn:
                String p = password.getText().toString();
                String repeat = repeat_password.getText().toString();
                JSONObject data = new JSONObject();
                try {
                    data.put("user", username.getText().toString());
                    data.put("passwd", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mode.equals("REGISTER") && repeat.equals(p)) {
                    new HttpPostConnection().execute(String.valueOf(data));
                    Intent intent = getIntent();
                    intent.putExtra("name", username.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if (mode.equals("LOGGIN")) {
//                    JSONObject obj = new HttpGetConnection().getJSONFromUrl();
                }
                break;
        }

    }

    private void changeMode() {
        if (mode.equals("REGISTER")) {
            loggin.setBackgroundColor(getResources().getColor(R.color.Orange));
            Drawable icon = getResources().getDrawable(R.mipmap.ic_loggin_active);
            loggin.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            loggin.setTextColor(getResources().getColor(R.color.MaterialDark));
            register.setBackgroundColor(getResources().getColor(R.color.MaterialDark));
            icon = getResources().getDrawable(R.mipmap.ic_register);
            register.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            register.setTextColor(getResources().getColor(R.color.Orange));
            layout_repeat_pass.setVisibility(View.GONE);
            title.setText("LOGGIN");
            info.setText("Introdueïxi les seves dades per accedir al seu perfil personal:");
            next.setText("LOGGIN");
            mode = "LOG";
        } else if (mode.equals("LOG")) {
            loggin.setBackgroundColor(getResources().getColor(R.color.MaterialDark));
            Drawable icon = getResources().getDrawable(R.mipmap.ic_loggin);
            loggin.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            loggin.setTextColor(getResources().getColor(R.color.Orange));
            register.setBackgroundColor(getResources().getColor(R.color.Orange));
            icon = getResources().getDrawable(R.mipmap.ic_register_active);
            register.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            register.setTextColor(getResources().getColor(R.color.MaterialDark));
            layout_repeat_pass.setVisibility(View.VISIBLE);
            title.setText("REGISTER");
            info.setText("Introdueïxi les seves dades per registrar el seu perfil personal:");
            next.setText("REGISTER");
            mode = "REGISTER";
        }
    }
}
