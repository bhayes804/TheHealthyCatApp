package wsu.group18.thehealthycat;
import java.time.LocalDateTime;
import java.io.Serializable;

public class HistoricalWeightEvent implements Serializable{
    public double Weight;
    public LocalDateTime Time;

    public HistoricalWeightEvent(double weight, LocalDateTime time){
        Weight = weight;
        Time = time;
    }
}
