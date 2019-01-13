package rbq2012.convarter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import rbq2012.convarter.R;
import rbq2012.convarter.configguide.ActivityCoexistBlockSetup;
import rbq2012.convarter.configguide.ActivityFlat;
import rbq2012.convarter.configguide.ActivityJs;

import static rbq2012.convarter.Constants.SPREF_NEWCOMER_NOTICE;
import static rbq2012.convarter.Constants.SPREF_PREF;

public final class ActivityMain extends AppCompatActivity {

    private String[] commands;
    private Class[] targetActivities;

    private void setupCommands() {
        commands = getResources().getStringArray(R.array.main_list);
        targetActivities = new Class[]{
                ActivityJs.class,
                ActivityFlat.class,
                ActivityCoexistBlockSetup.class,
                ActivitySettings.class,
                null, ActivityTest.class
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCommands();
        ListView list = findViewById(R.id.list);
        MainAdapter ada = new MainAdapter();
        list.setAdapter(ada);
        list.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) ->
                startActivity(new Intent(ActivityMain.this, targetActivities[i])));
        SharedPreferences pref = getSharedPreferences(SPREF_PREF, MODE_PRIVATE);
        if (pref.getBoolean(SPREF_NEWCOMER_NOTICE, true)) new AlertDialog.Builder(this)
                .setMessage(R.string.main_welcome)
                .setPositiveButton(R.string.global_confirm, (DialogInterface dialog, int which) ->
                        pref.edit()
                                .putBoolean(SPREF_NEWCOMER_NOTICE, false).apply())
                .create().show();
    }

    private class MainAdapter extends ArrayAdapter<String> {
        MainAdapter() {
            super(ActivityMain.this, R.layout.entry_mainlist, R.id.entry_text, commands);
        }
    }
}
