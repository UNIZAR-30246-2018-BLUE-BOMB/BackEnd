package bluebomb.urlshortener.database;

import java.util.Date;

public class AuxClickStat {
    private Date date;
    private String agent;
    private int clicks;
    private int sum;

    public AuxClickStat(Date date, String agent, int clicks, int sum) {
        this.date = date;
        this.agent = agent;
        this.clicks = clicks;
        this.sum = sum;
    }

    public AuxClickStat() {
    }

    public Date getDate() {
        return date;
    }

    public String getAgent() {
        return agent;
    }

    public int getClicks() {
        return clicks;
    }

    public int getSum() {
        return sum;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "AuxClickStat{" +
                "date=" + date +
                ", agent='" + agent + '\'' +
                ", clicks=" + clicks +
                ", sum=" + sum +
                '}';
    }
}

