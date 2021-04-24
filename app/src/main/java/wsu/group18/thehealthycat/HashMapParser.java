package wsu.group18.thehealthycat;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HashMapParser {
    private HashMap hashMap;

    public HashMapParser(HashMap h){
        hashMap = h;
    }

    public String getName(){
        return hashMap.get("name").toString();
    }

    public double getCurrentWeight(){
        return Double.valueOf(hashMap.get("currentWeightLBS").toString());
    }

    public double getTargetWeight(){
        return Double.valueOf(hashMap.get("targetWeightLBS").toString());
    }

    public int getFeedingSize(){
        return Integer.valueOf(hashMap.get("feedingSize").toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalTime> getFeedingTimes(){
        Object obj = hashMap.get("feedingTimes");
        if(obj == null){
            return null;
        }

        List<HashMap> hashList = new ArrayList<>();
        ArrayList<LocalTime> toRtn = new ArrayList<>();

        if(obj.getClass().isArray()){
            hashList = Arrays.asList((HashMap[]) obj);
        } else if (obj instanceof Collection) {
            hashList = new ArrayList<>((Collection<HashMap>)obj);
        }

        for(int i = 0; i < hashList.size(); i++){
            HashMap h = hashList.get(i);
            String hour = h.get("hour").toString();
            if(hour.length() < 2){
                hour = "0" + hour;
            }

            String minute = h.get("minute").toString();
            if(minute.length() < 2){
                minute = "0" + minute;
            }
            String time = hour + ":" + minute;
            LocalTime l = LocalTime.parse(time);

            toRtn.add(l);
        }
        return toRtn;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<HistoricalWeightEvent> getHistoricalWeightData(){
        Object obj = hashMap.get("historicalWeightData");
        if(obj == null){
            return null;
        }

        List<HashMap> hashList = new ArrayList<>();
        ArrayList<HistoricalWeightEvent> toRtn = new ArrayList<>();

        if(obj.getClass().isArray()){
            hashList = Arrays.asList((HashMap[]) obj);
        } else if (obj instanceof Collection) {
            hashList = new ArrayList<>((Collection<HashMap>)obj);
        }

        for(int i = 0; i < hashList.size(); i++){
            int year;
            Month month;
            int dayOfMonth;
            int hour;
            int minute;
            double weight;

            HashMap h1 = hashList.get(i);

            weight = Double.valueOf(h1.get("Weight").toString());

            HashMap h2 = (HashMap)h1.get("Time");

            year = Integer.valueOf(h2.get("year").toString());
            month = Month.valueOf(h2.get("month").toString());
            dayOfMonth = Integer.valueOf(h2.get("dayOfMonth").toString());
            hour = Integer.valueOf(h2.get("hour").toString());
            minute = Integer.valueOf(h2.get("minute").toString());

            LocalDateTime l = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
            HistoricalWeightEvent hw = new HistoricalWeightEvent(weight, l);
            toRtn.add(hw);
        }

        return toRtn;
    }
}
