package rbq2012.convarter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import rbq2012.convarter.R;
import rbq2012.convarter.configguide.ActivityJs;
import rbq2012.ldbchunk.DB;

public final class ActivityMain extends AppCompatActivity {

    private String[] commands;
    private Class[] targetActivities;

    private void setupCommands() {
        commands = getResources().getStringArray(R.array.main_list);
        targetActivities = new Class[]{
                ActivityJs.class, ActivitySettings.class, null
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCommands();
        ListView list = (ListView) findViewById(R.id.list);
        MainAdapter ada = new MainAdapter();
        list.setAdapter(ada);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ActivityMain.this, targetActivities[i]));
            }
        });
    }

    private class MainAdapter extends ArrayAdapter<String> {
        public MainAdapter() {
            super(ActivityMain.this, R.layout.entry_mainlist, R.id.entry_text, commands);
        }
    }
}
