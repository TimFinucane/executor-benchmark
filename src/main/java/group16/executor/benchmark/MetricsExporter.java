package group16.executor.benchmark;

import group16.executor.benchmark.metrics.Metrics;

public interface MetricsExporter {

    void exportMetrics(Metrics metrics);

}
