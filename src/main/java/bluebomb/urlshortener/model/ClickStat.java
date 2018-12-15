package bluebomb.urlshortener.model;

public class ClickStat {
    private String agent;
    private Integer clicks ;

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
}

