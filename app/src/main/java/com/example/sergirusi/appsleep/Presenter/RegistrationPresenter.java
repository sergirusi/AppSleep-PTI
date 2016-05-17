package com.example.sergirusi.appsleep.Presenter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sergirusi.appsleep.R;
import com.example.sergirusi.appsleep.View.Activitiy.RegistrationActivity;

/**
 * Created by SergiRusi on 05/05/2016.
 */
public class RegistrationPresenter implements Presenter {

    private RegistrationActivity context;

    public RegistrationPresenter(RegistrationActivity context) {
        this.context = context;
    }

    public void changeMode(Button loggin, Button register, LinearLayout layout_repeat_pass, TextView title, TextView info, Button next) {
        if (context.mode.equals("REGISTER")) {
            loggin.setBackgroundColor(context.getResources().getColor(R.color.Orange));
            Drawable icon = context.getResources().getDrawable(R.mipmap.ic_loggin_active);
            loggin.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            loggin.setTextColor(context.getResources().getColor(R.color.MaterialDark));
            register.setBackgroundColor(context.getResources().getColor(R.color.MaterialDark));
            icon = context.getResources().getDrawable(R.mipmap.ic_register);
            register.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            register.setTextColor(context.getResources().getColor(R.color.Orange));
            layout_repeat_pass.setVisibility(View.GONE);
            title.setText("LOG IN");
            info.setText("Introdueïxi les seves dades per accedir al seu perfil personal:");
            next.setText("LOG IN");
            context.mode = "LOG";
        } else if (context.mode.equals("LOG")) {
            loggin.setBackgroundColor(context.getResources().getColor(R.color.MaterialDark));
            Drawable icon = context.getResources().getDrawable(R.mipmap.ic_loggin);
            loggin.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            loggin.setTextColor(context.getResources().getColor(R.color.Orange));
            register.setBackgroundColor(context.getResources().getColor(R.color.Orange));
            icon = context.getResources().getDrawable(R.mipmap.ic_register_active);
            register.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            register.setTextColor(context.getResources().getColor(R.color.MaterialDark));
            layout_repeat_pass.setVisibility(View.VISIBLE);
            title.setText("REGISTER");
            info.setText("Introdueïxi les seves dades per registrar el seu perfil personal:");
            next.setText("REGISTER");
            context.mode = "REGISTER";
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
}
