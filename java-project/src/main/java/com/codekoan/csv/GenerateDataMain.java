package com.codekoan.csv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class GenerateDataMain {

    public static final void main(String[] args) throws Exception {
        Options options = new Options();
        Option input;
        options.addOption(Option.builder("o").desc("output file").hasArg().build());
        options.addOption(input = Option.builder("i").longOpt("input").hasArg().desc("input file").build());
        options.addOption(
                Option.builder("r").hasArg().longOpt("records").desc("records to generate").required(true).build());

        CommandLine cLine = new DefaultParser().parse(options, args);

        InputStream in = System.in;
        OutputStream out = System.out;

        if (cLine.hasOption("o")) {
            out = new FileOutputStream(cLine.getOptionValue("o"));
        }
        if (cLine.hasOption(input)) {
            in = new FileInputStream(cLine.getOptionValue(input));
        }
        new StreamDataGuesser().generateData(in, out, Integer.parseInt(cLine.getOptionValue("r")));
        out.close();
        in.close();
    }
}
