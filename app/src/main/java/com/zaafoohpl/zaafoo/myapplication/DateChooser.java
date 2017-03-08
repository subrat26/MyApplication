package com.zaafoohpl.zaafoo.myapplication;


import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zaafoohpl.zaafoo.fragment.RestList;

import java.util.Calendar;

import io.paperdb.Paper;


public class DateChooser extends Fragment {


    private int mYear, mMonth, mDay, mHour, mMinute;
    int x;
    int y;
    TextView dateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V=inflater.inflate(R.layout.fragment_date_chooser, container, false);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        dateTime=(TextView)V.findViewById(R.id.textView26);
        dateTime.setTypeface(face);
        Button date=(Button)V.findViewById(R.id.button2);
        Button time=(Button)V.findViewById(R.id.button8);
        dateTime.setText("No Date Or Time is Selected");
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                                Paper.book().write("date",date);
                                if(Paper.book().read("time")!=null)
                                    dateTime.setText("Time:"+Paper.book().read("time")+" "+"Date:"+date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                x=1;
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String time;
                                if(minute<10)
                                    time=hourOfDay+":0"+minute;
                                else
                                time=hourOfDay+":"+minute;

                                Paper.book().write("time",time);
                                if(Paper.book().read("date")!=null)
                                    dateTime.setText("Time:"+time+" "+"Date:"+Paper.book().read("date"));

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                y=1;
            }
        });

        return V;
    }

}
