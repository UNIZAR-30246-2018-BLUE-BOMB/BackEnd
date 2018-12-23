package bluebomb.urlshortener.model;

import java.util.ArrayList;

/**
 * Global stats used in realtime stats in WebSockets
 */
public class GlobalStats {
    private String sequence;
    private String parameter;
    private ArrayList<ClickStat> stats;

    @SuppressWarnings("unused")
    public GlobalStats() {
    }

    @SuppressWarnings("unused")
    public GlobalStats(String sequence, String parameter, ArrayList<ClickStat> stats) {
        this.stats = stats;
        this.sequence = sequence;
        this.parameter = parameter;
    }

    @SuppressWarnings("unused")
    public GlobalStats(String sequence, String parameter, ClickStat stat) {
        this.stats = new ArrayList<>();
        this.stats.add(stat);
        this.sequence = sequence;
        this.parameter = parameter;
    }

    @SuppressWarnings("unused")
    public ArrayList<ClickStat> getStats() {
        return stats;
    }

    @SuppressWarnings("unused")
    public void setStats(ArrayList<ClickStat> stats) {
        this.stats = stats;
    }

    @SuppressWarnings("unused")
    public String getSequence() {
        return sequence;
    }

    @SuppressWarnings("unused")
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @SuppressWarnings("unused")
    public String getParameter() {
        return parameter;
    }

    @SuppressWarnings("unused")
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
