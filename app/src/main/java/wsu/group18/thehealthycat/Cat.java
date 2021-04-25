package wsu.group18.thehealthycat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class Cat implements Serializable {

    public int UID;
    private String Name;
    private double TargetWeightLBS;
    private double CurrentWeightLBS;
    private ArrayList<HistoricalWeightEvent> HistoricalWeightData;
    private ArrayList<LocalTime> FeedingTimes;
    private static final String TAG = "ReadAndWriteSnippets";
    private int FeedingSize;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    FirebaseUser User;
    public Cat(){
        Name = "";
        FeedingSize = 0;
        TargetWeightLBS = 0.0;
        CurrentWeightLBS = 0.0;
        HistoricalWeightData = new ArrayList<HistoricalWeightEvent>();
        FeedingSize = 0;

        HistoricalWeightEvent first= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            first = new HistoricalWeightEvent(13, LocalDateTime.of(2019,01,1,3,14));
        }
        HistoricalWeightData.add(first);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HistoricalWeightData.add(new HistoricalWeightEvent(16,LocalDateTime.of(2019,01,11,3,14)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HistoricalWeightData.add(new HistoricalWeightEvent(19,LocalDateTime.of(2019,01,22,3,14)));
        }
        FeedingTimes = new ArrayList<LocalTime>();

    }

    public void setUser(FirebaseUser user){
        User= user;
}


    public Cat(String name, double targetWeight, double currentWeight, ArrayList<HistoricalWeightEvent> historicalWeightData, ArrayList<LocalTime> feedingTimes, FirebaseUser user, int feedingSize) {
        Name = name;
        TargetWeightLBS = targetWeight;
        CurrentWeightLBS = currentWeight;
        HistoricalWeightData = historicalWeightData;
        FeedingTimes = feedingTimes;
        User = user;
        FeedingSize = feedingSize;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getFeedingSize() {
        return FeedingSize;
    }

    public void setFeedingSize(int feedingSize) {
        FeedingSize = feedingSize;
    }

    public double getTargetWeightLBS() {
        return TargetWeightLBS;
    }

    public void setTargetWeightLBS(double targetWeightLBS) {
        TargetWeightLBS = targetWeightLBS;
    }

    public double getCurrentWeightLBS() {
        return CurrentWeightLBS;
    }

    public void setCurrentWeightLBS(double currentWeightLBS) { CurrentWeightLBS = currentWeightLBS; }

    public ArrayList<HistoricalWeightEvent> getHistoricalWeightData() {
        return HistoricalWeightData;
    }

    public void setHistoricalWeightData(ArrayList historicalWeightData) {
        HistoricalWeightData = historicalWeightData;
    }

    public List getFeedingTimes() {
        return FeedingTimes;
    }

    public void setFeedingTimes(ArrayList feedingTimes) {
        FeedingTimes = feedingTimes;
    }

    public void updateFirebase(String connection){
        mDatabase = FirebaseDatabase.getInstance().getReference("usersData");
        mDatabase.child(connection).setValue(this);
    }

}

