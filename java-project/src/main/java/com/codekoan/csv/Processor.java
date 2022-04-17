package com.codekoan.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Processor {
    public static final int BATCH_SIZE = 1000;

    private DataSource dataSink;
    private List<CSVColumnDef> columns;
    private String tablename;

    private static Logger logger = Logger.getLogger(Processor.class.getName());

    public Processor(DataSource dataSink, List<CSVColumnDef> columns, String tablename) {
        this.dataSink = dataSink;
        this.columns = columns;
        this.tablename = tablename;
    }

    @FunctionName("blobImporter")
    public void blobImporter(@BindingName("name") String filename) {
        try( Connection connection = dataSink.getConnection()){
            try (FileInputStream source = new FileInputStream(filename)) {
                writeInto(connection, source, this.columns);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Cannot insert data for "+ filename, e );
        }
        
    }

    private String insertStatement(List<CSVColumnDef> columns, String table) {
        String columnsSql = columns.stream().map(CSVColumnDef::getDbName).collect(Collectors.joining(","));
        String questionsSql = Collections.nCopies(columns.size(), "?").stream().collect(Collectors.joining(","));
        return "INSERT INTO " + table + " ( " + columnsSql + ") VALUES (" +
                questionsSql + ");";

    }

    private void writeInto(final Connection sink, InputStream source, List<CSVColumnDef> columns)
            throws SQLException, IOException {
        String insertRecordSql = insertStatement(this.columns, this.tablename);
        Iterable<CSVRecord> data = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(false).build()
                .parse(new InputStreamReader(source));
        Map<String, CSVColumnDef> colMap = columns.stream()
                .collect(Collectors.toMap(CSVColumnDef::getCsvName, Function.identity()));
        try (PreparedStatement statement = sink.prepareStatement(insertRecordSql)) {
            int recordsInBatch = 0;
            for (CSVRecord record : data) {
                final List<String> headerNames = record.getParser().getHeaderNames();
                for (int i = 0; i < record.size(); ++i) {
                    CSVColumnDef inserter = colMap.get(headerNames.get(i));
                    statement.setObject(i+1, inserter.map(record.get(inserter.getCsvName())), inserter.sqlType);
                }
                statement.addBatch();
                ++recordsInBatch;
                if (recordsInBatch > BATCH_SIZE) {
                    statement.executeBatch();
                    recordsInBatch = 0;
                }
            }
            statement.executeBatch();
        }
    }

}