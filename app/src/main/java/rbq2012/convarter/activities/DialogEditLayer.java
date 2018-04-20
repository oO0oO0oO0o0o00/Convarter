package rbq2012.convarter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import rbq2012.convarter.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class DialogEditLayer extends AppCompatActivity {

    static final public String INTENT_KEY_MODE_ADD = "add";
    static final public String INTENT_KEY_INDEX = "index";
    static final public String INTENT_KEY_ID = "id";
    static final public String INTENT_KEY_DATA = "data";
    static final public String INTENT_KEY_COUNT = "count";
    static final private int REQ_CODE_PICK = 2012;

    private EditText etid, etdata, etcnt;
    private TextView tverr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editlayer);
        Intent intent = getIntent();
        if (intent.getBooleanExtra(INTENT_KEY_MODE_ADD, false))
            setTitle(R.string.editlayers_title_add);
        else setTitle(R.string.editlayers_title_edit);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getWindow().setLayout(metrics.widthPixels * 6 / 7, WRAP_CONTENT);

        etid = findViewById(R.id.edittext);
        etdata = findViewById(R.id.edittext1);
        etcnt = findViewById(R.id.edittext2);
        etid.setText("" + intent.getIntExtra(INTENT_KEY_ID, 0));
        etdata.setText("" + intent.getIntExtra(INTENT_KEY_DATA, 0));
        etcnt.setText("" + intent.getIntExtra(INTENT_KEY_COUNT, 1));
        tverr = findViewById(R.id.text);
        setResult(RESULT_CANCELED);
    }

    public void cancel(View v) {
        finish();
    }

    public void confirm(View v) {
        int id, data, amount;
        try {
            id = Integer.parseInt(etid.getText().toString());
        } catch (Exception e) {
            id = -1;
        }
        if (id < 0 || id > 255) {
            tverr.setText(R.string.editlayer_err_id);
            return;
        }
        try {
            data = Integer.parseInt(etdata.getText().toString());
        } catch (Exception e) {
            data = -1;
        }
        if (data < 0 || data > 15) {
            tverr.setText(R.string.editlayer_err_data);
            return;
        }
        try {
            amount = Integer.parseInt(etcnt.getText().toString());
        } catch (Exception e) {
            amount = -1;
        }
        if (amount < 0) {
            tverr.setText(R.string.editlayer_err_amount);
            return;
        }
        Intent i = getIntent();
        i.putExtra(INTENT_KEY_ID, id);
        i.putExtra(INTENT_KEY_DATA, data);
        i.putExtra(INTENT_KEY_COUNT, amount);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK:
                if (resultCode != RESULT_OK) break;
                etid.setText(Integer.toString(data.getIntExtra(INTENT_KEY_ID, 0)));
                etdata.setText("0");
                break;
        }
    }

    public void pick(View view) {
        startActivityForResult(new Intent(this, DialogPickBlock.class), REQ_CODE_PICK);
    }
}
