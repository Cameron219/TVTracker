package uk.ac.abertay.tvtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_open_search_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_open_search_activity = (Button) findViewById(R.id.btn_open_search_activity);
        btn_open_search_activity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_search_activity:
                open_search_activity();
                break;
        }
    }

    private void open_search_activity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
