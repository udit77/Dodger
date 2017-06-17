package com.udit.dodger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class scores extends Activity {

    private ListView mainListView;
    MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        mainListView = (ListView) findViewById(R.id.mainListView);

        ArrayList<HighScore> scoreArrayList = new ArrayList<HighScore>();
        scoreArrayList = (new SharedPreference()).getScores(getApplicationContext());

        Log.d("************", "" + scoreArrayList);
        myAdapter = new MyAdapter(getApplicationContext(), scoreArrayList);
        mainListView.setAdapter(myAdapter);
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HighScore> arrayList;

        public MyAdapter(Context context, ArrayList<HighScore> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
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
                convertView = mInflater.inflate(R.layout.score_row, null);
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
        Intent mainIntent = new Intent(this, Main2Activity.class);
        startActivity(mainIntent);
        finish();
    }
}