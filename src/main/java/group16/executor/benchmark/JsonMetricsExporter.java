package group16.executor.benchmark;

import com.google.gson.Gson;
import group16.executor.benchmark.metrics.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonMetricsExporter implements MetricsExporter {
    @Override
    public void exportMetrics(Metrics metrics, String serviceType, String profileType) {
        Gson gson = new Gson();
        String fileName = serviceType + "_" + profileType + "_" +
                new SimpleDateFormat("yyy-MM-dd-HHmmss'.json'").format(new Date());
        File directory = new File(Paths.get(System.getProperty("user.dir"), "metrics").toString());
        if (! directory.exists()){
            directory.mkdir();
        }
        try {
            // Can't use gson.toJson(obj, FileWriter) because gson seems to cut it off prematurely? odd bug.
            String asString = gson.toJson(metrics);
            FileWriter fw=new FileWriter(Paths.get(directory.toString(), fileName).toString());
            fw.write(asString);
            fw.close();
        } catch (IOException e) {
            System.out.println("Error while exporting metrics as JSON");
            e.printStackTrace();
        }
    }
}
