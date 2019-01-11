package bluebomb.urlshortener.model;

import java.util.Objects;

public class ClickStat {
    private String agent;
    private Integer clicks;

    public ClickStat() {
    }

    public ClickStat(String agent, Integer clicks) {
        this.agent = agent;
        this.clicks = clicks;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickStat clickStat = (ClickStat) o;
        return Objects.equals(agent, clickStat.agent) &&
                Objects.equals(clicks, clickStat.clicks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent, clicks);
    }

    @Override
    public String toString() {
        return "ClickStat{" +
                "agent='" + agent + '\'' +
                ", clicks=" + clicks +
                '}';
    }
}

