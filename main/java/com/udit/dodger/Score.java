package com.udit.dodger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Score extends Activity {

    private ListView mainListView;
    MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_high_scores);
        mainListView = (ListView) findViewById(R.id.mainListView);

        ArrayList<HighScores> scoreArrayList = new ArrayList<HighScores>();
        scoreArrayList = (new SharedPreference()).getScores(getApplicationContext());

        Log.d("************", "" + scoreArrayList);
        myAdapter = new MyAdapter(getApplicationContext(), scoreArrayList);
        mainListView.setAdapter(myAdapter);
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HighScores> arrayList;

        public MyAdapter(Context context, ArrayList<HighScores> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            if(arrayList == null)
                return 0;
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.content_high_scores, null);
                holder.scoreView = (TextView) convertView.findViewById(R.id.scoreTextView);
                holder.dateView = (TextView) convertView.findViewById(R.id.dateTextView);
                holder.serialNumView = (TextView) convertView.findViewById(R.id.serialNumView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
            String dateString = formatter.format(new Date(arrayList.get(position).getTime()));
            holder.serialNumView.setText(String.valueOf(position + 1) + ".");
            holder.scoreView.setText(String.valueOf(arrayList.get(position).getScore()));
            holder.dateView.setText(dateString);

            return convertView;
        }

        public class ViewHolder {
            TextView scoreView;
            TextView dateView;
            TextView serialNumView;
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, Home.class);
        startActivity(mainIntent);
        finish();
    }
}