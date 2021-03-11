package wsu.group18.thehealthycat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Array;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText cName;
    private TextInputEditText cTargetWeight;
    public TextInputEditText FeedingFequencyEditor;
    private int feedingFrequency;
    private Button SaveButton;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private CustomTimeAdapter customAdapter;
    public ArrayList<TimeEditModel> editModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cName = findViewById(R.id.NameEditText);
        cTargetWeight = findViewById(R.id.TargetWeightEditText);

        SaveButton = findViewById(R.id.SettingsSaveButton);
        SaveButton.setFocusableInTouchMode(false);

        recyclerView = findViewById(R.id.TimeEditorList);
        editModelArrayList = populateList(0);
        customAdapter = new CustomTimeAdapter(this, editModelArrayList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        FeedingFequencyEditor = findViewById(R.id.FeedingFrequencyEditText);
        FeedingFequencyEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*Do nothing*/ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { /*Do nothing*/ }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int i = Integer.valueOf(s.toString());
                    setFeedingFrequency(i);
                    editModelArrayList = populateList(i);
                    customAdapter.UpdateList(editModelArrayList);
                } catch (NumberFormatException e) {
                    setFeedingFrequency(0);
                    editModelArrayList = populateList(0);
                    customAdapter.UpdateList(editModelArrayList);
                }
            }
        });

        String name = getIntent().getStringExtra("CAT_NAME");
        String targetWeight = String.valueOf(getIntent().getDoubleExtra("CAT_TARGET_WEIGHT", 0.0));
        String feedingFreq = getIntent().getStringExtra("CAT_FEEDING_FREQ");
        ArrayList<LocalTime> incomingFeedingTimes = (ArrayList<LocalTime>) getIntent().getSerializableExtra("CAT_FEEDING_TIMES");
        FirebaseUser incomingUser = (FirebaseUser) getIntent().getSerializableExtra("USER");
        if(!name.isEmpty()){
            cName.setText(name);
        }
        if(!targetWeight.isEmpty()){
            cTargetWeight.setText(targetWeight);
        }
        if(!feedingFreq.isEmpty()){
            FeedingFequencyEditor.setText(feedingFreq);
        }
        if(incomingFeedingTimes != null){
            ArrayList<TimeEditModel> t = ConvertListToTimeEditModel(incomingFeedingTimes);
            editModelArrayList = t;
            customAdapter.UpdateList(t);
        }
        if(incomingUser != null){
            user = incomingUser;
        }
    }

    /*public void CanSave(){
        String name = getIntent().getStringExtra("CAT_NAME");
        String targetWeight = String.valueOf(getIntent().getDoubleExtra("CAT_TARGET_WEIGHT", 0.0));

        if(cName.toString().isEmpty() || cTargetWeight.toString().isEmpty()){
            SaveButton.setClickable(false);
        }
        else if(cName.toString() != name || cTargetWeight.toString() != targetWeight){
            SaveButton.setClickable(true);
        }
        else{
            SaveButton.setClickable(false);
        }
    }*/

    public void OnCancel(View v){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    public void OnSave(View v){
        ArrayList<LocalTime> timeList = ConvertListToLocalTime(editModelArrayList);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CAT_NAME", cName.getText().toString());
        intent.putExtra("CAT_TARGET_WEIGHT", Double.parseDouble(cTargetWeight.getText().toString()));
        intent.putExtra("TIME_LIST", timeList);
        intent.putExtra("USER", user);
        startActivity(intent);
    }

    private ArrayList<TimeEditModel> populateList(int listSize){

        ArrayList<TimeEditModel> list = new ArrayList<>();

        for(int i = 0; i < listSize; i++){
            TimeEditModel editModel = new TimeEditModel();
            list.add(editModel);
        }

        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalTime> ConvertListToLocalTime(ArrayList<TimeEditModel> editList){
        ArrayList<LocalTime> timeList = new ArrayList<>();
        for(int i = 0; i < editList.size(); i++){
            try {
                String s = editList.get(i).getEditTextValue();
                if(s.length() == 4 && s.contains(":")){
                    s = "0" + s;
                }
                else if(s.length() < 4 || s.length() > 5 || !s.contains(":")){
                    continue;
                }
                LocalTime time = LocalTime.parse(s);
                timeList.add(time);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        return timeList;
    }

    public ArrayList<TimeEditModel> ConvertListToTimeEditModel(ArrayList<LocalTime> timeList){
        ArrayList<TimeEditModel> editModel = new ArrayList<TimeEditModel>();
        for(int i = 0; i < timeList.size(); i++){
            TimeEditModel e = new TimeEditModel();
            String hour = String.valueOf(timeList.get(i).getHour());
            String minutes = String.valueOf(timeList.get(i).getMinute());
            if(hour.length() < 2){
                hour = "0" + hour;
            }
            if(minutes.length() < 2){
                minutes = "0" + minutes;
            }

            String time = hour + ":" + minutes;
            e.setEditTextValue(time);
            editModel.add(e);
        }
        return editModel;
    }

    public int getFeedingFrequency(){return feedingFrequency;};
    public void setFeedingFrequency(int value){
        feedingFrequency = value;
    }

}