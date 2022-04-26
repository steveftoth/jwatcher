package com.codekoan.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

public class StreamDataGuesser {


    public void generateData(InputStream in, OutputStream out, int recordsToGenerate) throws IOException {

        CSVParser records = CSVFormat.RFC4180.builder().setSkipHeaderRecord(true).build()
                .parse(new InputStreamReader(in));

        List<String> headers = records.getHeaderNames();

        final ArrayList<LinearDataGuesser> models = new ArrayList<>();
        
        // build models.
        records.forEach(record -> {
            while( record.size() > models.size()) {
                models.add(new LinearDataGuesser());
            }
            for (int i = 0; i < record.size() && i < models.size(); i++) {
                models.get(i).buildGuesser(record.get(i));
            }
        });
        models.forEach(LinearDataGuesser::normalize);

        // output data!
        final Random r = new Random();
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out),
                CSVFormat.RFC4180.builder().setHeader(headers.toArray(String[]::new)).build());
                    
        while (recordsToGenerate-- > 0) {
            printer.printRecord(models.stream().map(m -> m.generateGuess(r)).collect(Collectors.toList()));
        }
        printer.close();

    }
}
