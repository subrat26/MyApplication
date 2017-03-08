package com.zaafoohpl.zaafoo.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.zaafoohpl.zaafoo.myapplication.DateChooser;
import com.zaafoohpl.zaafoo.myapplication.NoOfPersons;
import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.SessionManagement;
import com.zaafoohpl.zaafoo.myapplication.ZaafooUser;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;


public class BookTable extends Fragment {

    ZaafooUser user;
    String restid;
    String dateTime;
    ArrayList<Table> tableList;
    ArrayList<String> bookedTableNos;
    Paint pinkpaint, bluepaint, blackpaint, whitepaint;
    Bitmap bg;
    Canvas canvas;
    View rootView;
    static int screenWidth, screenHeight;
    int noOfPersons,leftPersons;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_table_book, container, false);
        getActivity().setTitle("Book Table");
        Paper.init(getActivity());
        user = Paper.book().read("user", new ZaafooUser());
        restid = user.getRestaurant();
        dateTime = Paper.book().read("date").toString() + "T" + Paper.book().read("time").toString();
        tableList=Paper.book().read("tableList",new ArrayList<Table>());
        noOfPersons=Integer.parseInt(Paper.book().read("persons").toString());
        bookedTableNos=Paper.book().read("bookedTables",new ArrayList<String>());
        leftPersons=noOfPersons;
        findScreenSize();
        initialiseColors();

        if(!bookedTableNos.isEmpty()){
            for(Table t:tableList){
                for(int i=0;i<bookedTableNos.size();i++)
                    if (t.getId().equalsIgnoreCase(bookedTableNos.get(i))){ ;
                        t.setBooked(true);
                    }
            }

        }



    drawInitialLayout();


        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                bg = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bg);
                //drawFloorPlan(canvas);
                 //canvas.translate(80,80);

                Rect rect;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    int x=(int)(event.getX());
                    //x-=40;
                    int y=(int)(event.getY());
                    y+=20;

                    Log.e("touch",x+"   "+y);

                        if(leftPersons>0&&leftPersons<=noOfPersons) {
                            for (Table t : tableList) {
                                rect = t.getR();
                                if (rect.contains(x, y)) {
                                    Log.e("rectangle",rect.flattenToString());
                                    if (t.isBooked())
                                        canvas.drawRect(rect, blackpaint);
                                    else if (t.isSelected()) {
                                        canvas.drawRect(rect, bluepaint);
                                        t.setSelected(false);
                                        leftPersons = leftPersons + t.getNoOfPerson();
                                        Snacky.builder()
                                                .setActivty(getActivity())
                                                .setText("Left Persons : "+leftPersons)
                                                .centerText()
                                                .setDuration(Snacky.LENGTH_SHORT)
                                                .info()
                                                .show();

                                    } else {
                                        canvas.drawRect(rect, pinkpaint);
                                        t.setSelected(true);
                                        leftPersons = leftPersons - t.getNoOfPerson();
                                        if(leftPersons>0)
                                        Snacky.builder()
                                                .setActivty(getActivity())
                                                .setText("Left Persons : "+leftPersons)
                                                .centerText()
                                                .setDuration(Snacky.LENGTH_SHORT)
                                                .info()
                                                .show();
                                        else
                                            Snacky.builder()
                                                    .setActivty(getActivity())
                                                    .setText("All Persons Got Tables")
                                                    .centerText()
                                                    .setDuration(Snacky.LENGTH_SHORT)
                                                    .info()
                                                    .show();


                                    }
                                } else {

                                    if (t.isBooked())
                                        canvas.drawRect(rect, blackpaint);
                                    else if (t.isSelected())
                                        canvas.drawRect(rect, pinkpaint);
                                    else
                                        canvas.drawRect(rect, bluepaint);
                                }
                                canvas.drawText(String.valueOf(t.getNoOfPerson()), (int)t.getX() * 500.0f, (int)t.getY() * 500.0f, whitepaint);
                                if(leftPersons<0)
                                    leftPersons=0;
                            }
                            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.table_layout);
                            ll.setBackgroundDrawable(new BitmapDrawable(bg));
                        }
                    if(leftPersons<=0)
                    tryAgain();

                }
                return true;
            }
        });
        return  rootView;
    }

    private void tryAgain() {

        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity());
        alert.setTitle("Zaafoo Select Table");
        alert.setCancelable(false);
        alert.setMessage("Do you want to continue with selected tables or reselect tables again?");
        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.content_home_drawar,new MenuFragment()).commit();
                    Paper.book().write("tableList",tableList);
                    new SessionManagement(getActivity()).insertTableList(tableList);
            }
        });
        alert.setNegativeButton("Reselect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        clearTables();
                        dialog.dismiss();
            }
        });
        AlertDialog dialog=alert.create();
        dialog.show();
    }


    private void clearTables() {
        for(Table t:tableList)
            t.setSelected(false);
        leftPersons=noOfPersons;
        drawInitialLayout();
    }


    public void findScreenSize(){
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = (int)size.x;
        screenHeight = (int)size.y;
        Log.e("screen",screenHeight+" "+screenWidth);

    }


    // Draw Initial Layout
    private void drawInitialLayout() {


        double x,y;
        int left,right,top,bottom;
        Rect rectangle;

        if(!tableList.isEmpty()){

            bg = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bg);
            //drawFloorPlan(canvas);
            //canvas.translate(80,80);


            for(Table t:tableList){

                x=t.getX();
                y=t.getY();
                left=(int)(x*convertPixelsToDp()  - 70);
                right = (int)(x*convertPixelsToDp() + 70);
                top = (int)(y*convertPixelsToDp() - 70);
                bottom =(int)(y*convertPixelsToDp()+ 70);

                rectangle=new Rect(left,top,right,bottom);
                t.setR(rectangle);


                if(t.isBooked())
                    canvas.drawRect(rectangle,blackpaint);
                else
                    canvas.drawRect(rectangle,bluepaint);

                canvas.drawText(String.valueOf(t.getNoOfPerson()),(int)(t.getX()*convertPixelsToDp()),(int)(t.getY()*convertPixelsToDp()),whitepaint);

            }
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.table_layout);
            ll.setBackgroundDrawable(new BitmapDrawable(bg));

        }
        else
            Snacky.builder()
                    .setActivty(getActivity())
                    .setText("No Tables Available For Booking")
                    .centerText()
                    .setDuration(Snacky.LENGTH_SHORT)
                    .error()
                    .show();

        bg=null;

    }

    // Initialise Paint Colors
    private void initialiseColors(){

        pinkpaint = new Paint();
        pinkpaint.setColor(Color.parseColor("#e1336f"));

        blackpaint = new Paint();
        blackpaint.setColor(Color.parseColor("#000000"));
        blackpaint.setStrokeWidth(2);

        whitepaint = new Paint();
        whitepaint.setColor(Color.parseColor("#FFFFFF"));
        whitepaint.setTextSize(30);
        whitepaint.setTextAlign(Paint.Align.CENTER);

        bluepaint = new Paint();
        bluepaint.setColor(Color.parseColor("#0099cc"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Paper.book().write("tableList",new ArrayList<Table>());
    }

    // Draw Floor Plan
    public void drawFloorPlan(Canvas c){


        String floorPlan=Paper.book().read("floorPlan");
        StringTokenizer st=new StringTokenizer(floorPlan," ");
        StringTokenizer st1;
        ArrayList<String> coordinates=new ArrayList<>();

        while(st.hasMoreTokens()) {
            st1 = new StringTokenizer(st.nextToken(), ",");
            while (st1.hasMoreTokens()) {
                String val = st1.nextToken();
                coordinates.add(val);
            }
        }

        for(int i=0;i<coordinates.size()-3;i=i+2){

            float startX,startY,endX,endY;
            startX=Float.parseFloat(coordinates.get(i))*1.5f;
            startY=Float.parseFloat(coordinates.get(i+1))*1.5f;
            endX=Float.parseFloat(coordinates.get(i+2))*1.5f;
            endY=Float.parseFloat(coordinates.get(i+3))*1.5f;
            c.drawLine(startX,startY,endX,endY,blackpaint);
        }
        int x=coordinates.size();
        c.drawLine(Float.parseFloat(coordinates.get(x-2))*1.5f,Float.parseFloat(coordinates.get(x-1))*1.5f,Float.parseFloat(coordinates.get(0))*1.5f,Float.parseFloat(coordinates.get(1))*1.5f,blackpaint);

    }


    public double convertPixelsToDp(){
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float dp=metrics.densityDpi;
        Log.e("dp",dp+"");
        return  (dp/2.3);
    }
}



