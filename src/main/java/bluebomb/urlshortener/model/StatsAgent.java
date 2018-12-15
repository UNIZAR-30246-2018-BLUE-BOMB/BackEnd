package bluebomb.urlshortener.model;

import java.util.Objects;

public class StatsAgent {
    private String agent;

    public StatsAgent(String agent) {
        this.agent = agent;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsAgent)) return false;
        StatsAgent that = (StatsAgent) o;
        return Objects.equals(agent, that.agent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent);
    }
}
