package bluebomb.urlshortener.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Stats {
    private Date day;
    private List<ClickStat> clickStat;

    public Stats(Date day, List<ClickStat> clickStat) {
        this.day = day;
        this.clickStat = clickStat;
    }

    public Stats() {
        this.day = null;
        this.clickStat = new ArrayList<>();
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public List<ClickStat> getClickStat() {
        return clickStat;
    }

    public void setClickStat(List<ClickStat> clickStat) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return Objects.equals(day, stats.day) &&
                Objects.equals(clickStat, stats.clickStat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, clickStat);
    }
}

