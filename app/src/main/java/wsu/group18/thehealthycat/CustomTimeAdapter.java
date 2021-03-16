package wsu.group18.thehealthycat;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomTimeAdapter extends RecyclerView.Adapter<CustomTimeAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    public static ArrayList<TimeEditModel> editModelArrayList;


    public CustomTimeAdapter(Context ctx, ArrayList<TimeEditModel> editModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.editModelArrayList = editModelArrayList;
    }

    public void UpdateList(ArrayList<TimeEditModel> List){
        editModelArrayList.clear();
        editModelArrayList.addAll(List);
        notifyDataSetChanged();
    }

    @Override
    public CustomTimeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.timelistitems, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final CustomTimeAdapter.MyViewHolder holder, final int position) {
        int hour;
        int minutes;

        String time = editModelArrayList.get(position).getEditTextValue();
        if(time == null){
            hour = 12;
            minutes = 0;
        }
        else{
            hour = Integer.valueOf(time.substring(0, 2));
            minutes = Integer.valueOf(time.substring(3, 5));
        }

        holder.timePicker.setHour(hour);
        holder.timePicker.setMinute(minutes);
        Log.d("print","yes");

    }

    @Override
    public int getItemCount() {
        return editModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        //protected EditText editText;
        protected TimePicker timePicker;

        public MyViewHolder(View itemView) {
            super(itemView);

            timePicker = (TimePicker) itemView.findViewById(R.id.editid);

            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    String hour = String.valueOf(hourOfDay);
                    String minutes = String.valueOf(minute);
                    if(hour.length() < 2){
                        hour = "0" + hour;
                    }
                    if(minutes.length() < 2){
                        minutes = "0" + minutes;
                    }

                    String time = hour + ":" + minutes;
                    editModelArrayList.get(getAdapterPosition()).setEditTextValue(time);
                }
            });

        }

    }
}
