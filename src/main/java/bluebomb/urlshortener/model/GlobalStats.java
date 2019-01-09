package bluebomb.urlshortener.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Global stats used in realtime stats in WebSockets
 */
public class GlobalStats {
    private String sequence;
    private String parameter;
    private List<ClickStat> stats;

    public GlobalStats() {
    }

    public GlobalStats(String sequence, String parameter, List<ClickStat> stats) {
        this.stats = stats;
        this.sequence = sequence;
        this.parameter = parameter;
    }

    public GlobalStats(String sequence, String parameter, ClickStat stat) {
        this.stats = new ArrayList<>();
        this.stats.add(stat);
        this.sequence = sequence;
        this.parameter = parameter;
    }

    public List<ClickStat> getStats() {
        return stats;
    }

    public void setStats(List<ClickStat> stats) {
        this.stats = stats;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalStats that = (GlobalStats) o;
        return Objects.equals(sequence, that.sequence) &&
                Objects.equals(parameter, that.parameter) &&
                Objects.equals(stats, that.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, parameter, stats);
    }
}
