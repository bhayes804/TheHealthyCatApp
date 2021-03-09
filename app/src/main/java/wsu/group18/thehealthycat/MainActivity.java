package wsu.group18.thehealthycat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Cat cat = new Cat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean hasStarted = prefs.getBoolean("hasStarted", true);

        //The following code executes if returning from the settings activity with saved changes.
        String settingsName = getIntent().getStringExtra("CAT_NAME");
        double settingsTargetWeight = getIntent().getDoubleExtra("CAT_TARGET_WEIGHT", 0.0);
        ArrayList<LocalTime> timeList = (ArrayList<LocalTime>) getIntent().getSerializableExtra("TIME_LIST");
        if(settingsName != null){
            cat.setName(settingsName);
        }
        if(settingsTargetWeight != 0.0){
            cat.setTargetWeightLBS(settingsTargetWeight);
        }
        if(timeList != null){
            cat.setFeedingTimes(timeList);
        }

        if (!hasStarted) {
            showStartupDialog();
        }

    }

    public void OpenChartActivity(View v){
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    public void OpenSettingsActivity(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("CAT_NAME", cat.getName());
        intent.putExtra("CAT_TARGET_WEIGHT", cat.getTargetWeightLBS());
        intent.putExtra("CAT_CURRENT_WEIGHT", cat.getCurrentWeightLBS());
        intent.putExtra("CAT_FEEDING_TIMES", (ArrayList) cat.getFeedingTimes());
        intent.putExtra("CAT_FEEDING_FREQ", String.valueOf(cat.getFeedingTimes().size()));
        startActivity(intent);
    }

    public void OpenCatInfoActivity(View v){
        Intent intent = new Intent(this, CatInfo.class);
        intent.putExtra("CAT_NAME", cat.getName());
        intent.putExtra("CAT_TARGET_WEIGHT", cat.getTargetWeightLBS());
        intent.putExtra("CAT_CURRENT_WEIGHT", cat.getCurrentWeightLBS());
        startActivity(intent);
    }

    private void showStartupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View startupDialogView = inflater.inflate(R.layout.startup_dialog, null);
        builder.setTitle("Welcome to the Healthy Cat!");

        final EditText catNameInput = (EditText) startupDialogView.findViewById(R.id.cat_name);
        final EditText targetWeightInput = (EditText) startupDialogView.findViewById(R.id.target_weight);
        builder.setView(startupDialogView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // cat = new Cat(catNameInput.getText().toString(), Double.parseDouble(targetWeightInput.getText().toString()), 0.0, new ArrayList());

                cat.setName(catNameInput.getText().toString());
                cat.setTargetWeightLBS(Double.parseDouble(targetWeightInput.getText().toString()));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("hasStarted", false);
        editor.apply();
    }
}