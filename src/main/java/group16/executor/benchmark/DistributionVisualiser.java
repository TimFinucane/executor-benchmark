package group16.executor.benchmark;

import group16.executor.benchmark.customDistributions.ClusterDistribution;
import org.apache.commons.math3.random.RandomGeneratorFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Test class of sorts for when one needs to visualize the dynamic load time generation. This class is useful because
 * it's hard to know what your distribution parameters are generating without looking!
 */
public class DistributionVisualiser {
    public static void showTimesPlot(double[] data) {
        XYSeries graph = new XYSeries("Clustered timing chart");
        XYDataset xyDataset = new XYSeriesCollection(graph);
        for (double time : data) {
            graph.add(new XYDataItem(time, 0));
        }
        JFreeChart chart = ChartFactory.createScatterPlot(
                " Array values", "Time", "values",
                xyDataset, PlotOrientation.VERTICAL, true, true, false);

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        double size = 0.3;
        double delta = size / 0.3;
        Shape shape1 = new Rectangle2D.Double(-delta, -delta, size, size);
        Shape shape2 = new Ellipse2D.Double(-delta, -delta, size, size);
        renderer.setSeriesShape(0, shape1);
        renderer.setSeriesShape(1, shape2);
        ChartFrame graphFrame = new ChartFrame("XYLine Chart", chart);
        graphFrame.setVisible(true);
        graphFrame.setSize(300, 300);
    }

    public static void main(String[] args) {
        double[] samples = new ClusterDistribution(
                RandomGeneratorFactory.createRandomGenerator(new Random()),
                5,
                1.0
        ).sample(1000);

        showTimesPlot(samples);
    }
}
