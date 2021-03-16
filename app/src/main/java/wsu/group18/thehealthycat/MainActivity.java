package wsu.group18.thehealthycat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    String ConnectionCode;
    private FirebaseAuth mAuth;
    public Cat cat;
    FirebaseDatabase database;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean hasStarted = prefs.getBoolean("hasStarted", true );
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            hasStarted=false;
        }

        cat = new Cat();

        //The following code executes if returning from the settings activity with saved changes.
        String settingsName = getIntent().getStringExtra("CAT_NAME");
        double settingsTargetWeight = getIntent().getDoubleExtra("CAT_TARGET_WEIGHT", 0.0);
        ArrayList<LocalTime> timeList = (ArrayList<LocalTime>) getIntent().getSerializableExtra("TIME_LIST");
        String settingsConnection = getIntent().getStringExtra("CONNECTION");
        boolean shouldShowStartup = true;
        boolean shouldUpdateDB = false;

        if(settingsName != null){
            cat.setName(settingsName);
            shouldShowStartup = false;
            shouldUpdateDB = true;
        }
        if(settingsTargetWeight != 0.0){
            cat.setTargetWeightLBS(settingsTargetWeight);
        }
        if(timeList != null){
            cat.setFeedingTimes(timeList);
            Toast.makeText(MainActivity.this,"times"+ timeList.toString(),Toast.LENGTH_LONG).show();
        }
        if(settingsConnection != null){
            ConnectionCode = settingsConnection;
        }

        //shouldShowStartup is true if we're starting up the first time, if we return from the settingsActivity, we don't want to run this again.
        if (shouldShowStartup) {
            showStartupDialog();
            cat.setUser(user);
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        if(shouldUpdateDB){
            user = mAuth.getCurrentUser();
            cat.setUser(user);
            cat.updateFirebase(ConnectionCode);
        }

        myRef.setValue("test three!");
    }

    public void OpenChartActivity(View v){
        if(cat!=null){
            user = mAuth.getCurrentUser();
            cat.setUser(user);
            cat.updateFirebase(ConnectionCode);
        }
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
        intent.putExtra("CONNECTION", ConnectionCode);
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
        final EditText emailText = (EditText) startupDialogView.findViewById(R.id.editTextEmailAddress);
        final EditText ConnectionCode = (EditText) startupDialogView.findViewById(R.id.connectionPassword);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ConnectionCode.setTooltipText("Find this code attached to your Healthy Cat Feeder");
        }

        builder.setView(startupDialogView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cat = new Cat(catNameInput.getText().toString(), Double.parseDouble(targetWeightInput.getText().toString()), 0.0, new ArrayList());

                cat.setName(catNameInput.getText().toString());
                cat.setTargetWeightLBS(Double.parseDouble(targetWeightInput.getText().toString()));
                String email = emailText.getText().toString();
                String password = ConnectionCode.getText().toString();
                makeNewUser(email,password);
                Toast.makeText(MainActivity.this, "email is" + email + "pass is " + password, Toast.LENGTH_LONG).show();
                // [START create_user_with_email]



            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private void makeNewUser(String email, String password) {
        ConnectionCode=password;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Authentication is ok, "+user.getUid(),
                                    Toast.LENGTH_SHORT).show();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }
}