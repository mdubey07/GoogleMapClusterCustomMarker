package il.co.wwo.mapapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private EditText vMaxResult, vSearchRadius;
    private Button btnSave;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (NullPointerException e){
            Toast.makeText(this, "Action bar title error",Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setTitle(R.string.settings);
        prefManager = new PrefManager(this);
        vMaxResult = findViewById(R.id.max_result);
        vSearchRadius = findViewById(R.id.search_radius);
        btnSave = findViewById(R.id.btn_save);
        vMaxResult.setText(String.valueOf(prefManager.getMaxResult()));
        vSearchRadius.setText(String.valueOf(prefManager.getSearchRadius()));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setMaxResult(Integer.valueOf(vMaxResult.getText().toString()));
                prefManager.setSearchRadius(Integer.valueOf(vSearchRadius.getText().toString()));
                finish();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
