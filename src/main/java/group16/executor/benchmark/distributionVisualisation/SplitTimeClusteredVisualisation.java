package group16.executor.benchmark.distributionVisualisation;

import group16.executor.benchmark.ProfileBuilder;
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

public class SplitTimeClusteredVisualisation {

    public void showTimesPlot() {
        ProfileBuilder builder = new ProfileBuilder(0);
        double[] times = builder.splitTimeClustered(1000, 1000, 5);

        XYSeries graph = new XYSeries("Clustered timing chart");
        XYDataset xyDataset = new XYSeriesCollection(graph);
        for (double time : times) {
            graph.add(new XYDataItem(time, 0));
        }
        JFreeChart chart = ChartFactory.createScatterPlot(
                " Array values", "Time", "values",
                xyDataset, PlotOrientation.VERTICAL, true, true, false);

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        double size = 1;
        double delta = size / 2.0;
        Shape shape1 = new Rectangle2D.Double(-delta, -delta, size, size);
        Shape shape2 = new Ellipse2D.Double(-delta, -delta, size, size);
        renderer.setSeriesShape(0, shape1);
        renderer.setSeriesShape(1, shape2);
        ChartFrame graphFrame = new ChartFrame("XYLine Chart", chart);
        graphFrame.setVisible(true);
        graphFrame.setSize(300, 300);
    }

}
