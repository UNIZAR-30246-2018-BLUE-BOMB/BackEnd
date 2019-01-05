package bluebomb.urlshortener.model;

import java.util.ArrayList;
import java.util.Date;


/**
 * Stats
 */

public class Stats {
    private Date day;
    private ArrayList<ClickStat> clickStat;

    public Stats(Date day, ArrayList<ClickStat> clickStat) {
        this.day = day;
        this.clickStat = clickStat;
    }

    public Stats() {
        this.day = null;
        this.clickStat = new ArrayList<ClickStat>();
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public ArrayList<ClickStat> getClickStat() {
        return clickStat;
    }

    public void setClickStat(ArrayList<ClickStat> clickStat) {
        this.clickStat = clickStat;
    }

    public boolean addClickStat(ClickStat input){
        return this.clickStat.add(input);
    }

    @Override
    public String toString() {
        return "Stats{" +
                "day=" + day +
                ", clickStat=" + clickStat +
                '}';
    }
}

