package com.dietbuddy.tralice.mapsapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import static java.util.Calendar.getInstance;

public class history extends AppCompatActivity {

    private TextView totaldistance_text;
    private TextView totalduration_text;
    private TextView totalcalories_text;
    private TextView weekView;
    private LineChartView chart1;
    private SwipeSelector swipeWeekSelector;
    private android.support.v7.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        totaldistance_text= (TextView) findViewById(R.id.distance_text);
        totalduration_text=(TextView)findViewById(R.id.duration_text);
        totalcalories_text=(TextView)findViewById(R.id.run_calories_total);
        weekView=(TextView)findViewById(R.id.week_view);
        chart1 = (LineChartView) findViewById(R.id.chart);
        swipeWeekSelector = (SwipeSelector) findViewById(R.id.swapWeek);

        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.customappbar, null);

        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0, 0);

        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);

        rightBmb.setButtonEnum(ButtonEnum.Ham);
        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        int images[] = {R.drawable.ic_burn_calories, R.drawable.ic_bmi_calculator, R.drawable.ic_recepe_finder, R.drawable.ic_records};
        String normalText[] = {"Burn Calories", "BMI Calculator", "Find Recepe", "Reports"};
        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
            rightBmb.addBuilder(new HamButton.Builder().normalImageRes(images[i]).normalText(normalText[i]).
                    shadowEffect(true).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    if (index == 1) {
                        Intent i = new Intent(history.this, bmi_activity.class);
                        startActivity(i);

                    } else if (index == 3) {
                        Intent i = new Intent(history.this, history.class);
                        startActivity(i);
                    } else if (index == 2) {
                        Intent i = new Intent(history.this, activity_recycler_view.class);
                        startActivity(i);
                    } else if (index == 0) {
                        Intent i = new Intent(history.this, MapsActivity.class);
                        startActivity(i);
                    }

                }
            }));


        //DataFetching
        DBHelper db=new DBHelper(this);
        final SQLiteDatabase database=db.getReadableDatabase();


        swipeWeekSelector.setItems(
                new SwipeItem(0, "Last Week", "Weekly Report for Last week"),
                new SwipeItem(1, "Berofe 2 Weeks", "Weekly Report for 2nd week"),
                new SwipeItem(2, "Before 3 weeks", "Weekly Report for 3rd week before today."),
                new SwipeItem(3, "Before 4 weeks", "Weekly Report for 4th week before today.")

        );
        SwipeItem selectedItem = swipeWeekSelector.getSelectedItem();

        int value= (int) selectedItem.value;

        //Initial


            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            Calendar cal=Calendar.getInstance();
            String nowDate=sdf.format(cal.getTime());

            cal.add(Calendar.DATE,-7);
            String startDate = sdf.format(cal.getTime());

            weekView.setText("From "+startDate +" to "+nowDate);

            //Last 7 days
            Cursor days7Before = database.rawQuery("SELECT * FROM " + DBHelper.REPORT_TABLE_NAME +
                    " WHERE date <= "  +'\''+ nowDate +'\''+  " AND date  >= "+ '\'' + startDate+'\'',null);
            days7Before.moveToFirst();

            double total_distance=0;
            double total_duration=0;
            double total_calories=0;

            List<PointValue> values = new ArrayList<PointValue>();

            int i=0;

            if(days7Before.getCount()==0){

                values.add(new PointValue(0,(float)0));
                values.add(new PointValue(1,(float)0));
                values.add(new PointValue(2,(float)0));
                values.add(new PointValue(3,(float)0));
                values.add(new PointValue(4,(float)0));
                values.add(new PointValue(5,(float)0));
                values.add(new PointValue(6,(float)0));

                totaldistance_text.setText(total_distance+"");
                totalduration_text.setText(total_duration+"");
                totalcalories_text.setText(total_calories+"");

                //Toast.makeText(this,"No Data",Toast.LENGTH_LONG);
            }else{
                while(days7Before.moveToNext()){

                    values.add(new PointValue(i, (float)days7Before.getDouble(3)));
                    total_duration += (days7Before.getDouble(2))/1000;
                    total_distance += days7Before.getDouble(3);
                    total_calories += days7Before.getDouble(4);
                    i++;
                }
                totaldistance_text.setText(total_distance+"Km");
                totalduration_text.setText(total_duration+" Sec");
                totalcalories_text.setText((int)Math.round(total_calories)+" Calories Burnt " );
            }

            //ComboLineColumnChartView chart=new ComboLineColumnChartView(this);

            //In most cased you can call data model methods in builder-pattern-like manner.
            Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            chart1.setLineChartData(data);



        swipeWeekSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                if((int)item.value==0){
                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal=Calendar.getInstance();
                    String nowDate=sdf.format(cal.getTime());

                    cal.add(Calendar.DATE,-7);
                    String startDate = sdf.format(cal.getTime());

                    weekView.setText("From "+startDate +" to "+nowDate);

                    //Last 7 days
                    Cursor days7Before = database.rawQuery("SELECT * FROM " + DBHelper.REPORT_TABLE_NAME +
                            " WHERE date <= "  +'\''+ nowDate +'\''+  " AND date  >= "+ '\'' + startDate+'\'',null);
                    days7Before.moveToFirst();

                    double total_distance=0;
                    double total_duration=0;
                    double total_calories=0;

                    List<PointValue> values = new ArrayList<PointValue>();

                    int i=0;

                    if(days7Before.getCount()==0){

                        values.add(new PointValue(0,(float)0));
                        values.add(new PointValue(1,(float)0));
                        values.add(new PointValue(2,(float)0));
                        values.add(new PointValue(3,(float)0));
                        values.add(new PointValue(4,(float)0));
                        values.add(new PointValue(5,(float)0));
                        values.add(new PointValue(6,(float)0));

                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");

                        //Toast.makeText(this,"No Data",Toast.LENGTH_LONG);
                    }else{
                        while(days7Before.moveToNext()){

                            values.add(new PointValue(i, (float)days7Before.getDouble(3)));
                            total_duration += days7Before.getDouble(2);
                            total_distance += days7Before.getDouble(3);
                            total_calories += days7Before.getDouble(4);
                            i++;
                        }
                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");
                    }

                    //ComboLineColumnChartView chart=new ComboLineColumnChartView(this);

                    //In most cased you can call data model methods in builder-pattern-like manner.
                    Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    chart1.setLineChartData(data);

                }else if((int)item.value==1){

                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal=Calendar.getInstance();

                    cal.add(Calendar.DATE,-7);
                    String startDate = sdf.format(cal.getTime());

                    cal.add(Calendar.DATE,-7);
                    String endDate=sdf.format(cal.getTime());

                    weekView.setText("From "+endDate+" to "+startDate );

                    //Last 7 days
                    Cursor days7Before = database.rawQuery("SELECT * FROM " + DBHelper.REPORT_TABLE_NAME +
                            " WHERE date <= "  +'\''+ startDate +'\''+  " AND date  >= "+ '\'' + endDate+'\'',null);
                    days7Before.moveToFirst();

                    double total_distance=0;
                    double total_duration=0;
                    double total_calories=0;

                    List<PointValue> values = new ArrayList<PointValue>();

                    int i=0;

                    if(days7Before.getCount()==0){
                        values.add(new PointValue(0,(float)0));
                        values.add(new PointValue(1,(float)0));
                        values.add(new PointValue(2,(float)0));
                        values.add(new PointValue(3,(float)0));
                        values.add(new PointValue(4,(float)0));
                        values.add(new PointValue(5,(float)0));
                        values.add(new PointValue(6,(float)0));

                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");


                    }else{
                        while(days7Before.moveToNext()){

                            values.add(new PointValue(i, (float)days7Before.getDouble(3)));
                            total_duration += days7Before.getDouble(2);
                            total_distance += days7Before.getDouble(3);
                            total_calories += days7Before.getDouble(4);
                            i++;
                        }
                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");
                    }

                    //ComboLineColumnChartView chart=new ComboLineColumnChartView(this);

                    //In most cased you can call data model methods in builder-pattern-like manner.
                    Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    chart1.setLineChartData(data);

                }else if((int)item.value==2){

                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal=Calendar.getInstance();

                    cal.add(Calendar.DATE,-14);
                    String startDate = sdf.format(cal.getTime());

                    cal.add(Calendar.DATE,-7);
                    String endDate=sdf.format(cal.getTime());

                    weekView.setText("From "+endDate+" to "+startDate );

                    //Last 7 days
                    Cursor days7Before = database.rawQuery("SELECT * FROM " + DBHelper.REPORT_TABLE_NAME +
                            " WHERE date <= "  +'\''+ startDate +'\''+  " AND date  >= "+ '\'' + endDate+'\'',null);
                    days7Before.moveToFirst();

                    double total_distance=0;
                    double total_duration=0;
                    double total_calories=0;

                    List<PointValue> values = new ArrayList<PointValue>();

                    int i=0;

                    if(days7Before.getCount()==0){
                        values.add(new PointValue(0,(float)0));
                        values.add(new PointValue(1,(float)0));
                        values.add(new PointValue(2,(float)0));
                        values.add(new PointValue(3,(float)0));
                        values.add(new PointValue(4,(float)0));
                        values.add(new PointValue(5,(float)0));
                        values.add(new PointValue(6,(float)0));

                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");

                    }else{
                        while(days7Before.moveToNext()){

                            values.add(new PointValue(i, (float)days7Before.getDouble(3)));
                            total_duration += days7Before.getDouble(2);
                            total_distance += days7Before.getDouble(3);
                            total_calories += days7Before.getDouble(4);
                            i++;
                        }
                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");
                    }

                    //ComboLineColumnChartView chart=new ComboLineColumnChartView(this);

                    //In most cased you can call data model methods in builder-pattern-like manner.
                    Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    chart1.setLineChartData(data);


                }else if((int)item.value==3){

                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal=Calendar.getInstance();

                    cal.add(Calendar.DATE,-21);
                    String startDate = sdf.format(cal.getTime());

                    cal.add(Calendar.DATE,-7);
                    String endDate=sdf.format(cal.getTime());

                    weekView.setText("From "+endDate+" to "+startDate );

                    //Last 7 days
                    Cursor days7Before = database.rawQuery("SELECT * FROM " + DBHelper.REPORT_TABLE_NAME +
                            " WHERE date <= "  +'\''+ startDate +'\''+  " AND date  >= "+ '\'' + endDate+'\'',null);
                    days7Before.moveToFirst();

                    double total_distance=0;
                    double total_duration=0;
                    double total_calories=0;

                    List<PointValue> values = new ArrayList<PointValue>();

                    int i=0;

                    if(days7Before.getCount()==0){
                        values.add(new PointValue(0,(float)0));
                        values.add(new PointValue(1,(float)0));
                        values.add(new PointValue(2,(float)0));
                        values.add(new PointValue(3,(float)0));
                        values.add(new PointValue(4,(float)0));
                        values.add(new PointValue(5,(float)0));
                        values.add(new PointValue(6,(float)0));

                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");

                    }else{
                        while(days7Before.moveToNext()){

                            values.add(new PointValue(i, (float)days7Before.getDouble(3)));
                            total_duration += days7Before.getDouble(2);
                            total_distance += days7Before.getDouble(3);
                            total_calories += days7Before.getDouble(4);
                            i++;
                        }
                        totaldistance_text.setText(total_distance+"");
                        totalduration_text.setText(total_duration+"");
                        totalcalories_text.setText(total_calories+"");
                    }

                    //ComboLineColumnChartView chart=new ComboLineColumnChartView(this);

                    //In most cased you can call data model methods in builder-pattern-like manner.
                    Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    chart1.setLineChartData(data);

                }
            }
        });

    }
}
