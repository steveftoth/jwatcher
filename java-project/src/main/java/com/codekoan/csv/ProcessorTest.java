package com.codekoan.csv;

import java.sql.Connection;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbc.JDBCPool;

public class ProcessorTest {

    static List<CSVColumnDef> othername = Arrays.asList(
            new CSVColumnDef("NPI", JDBCType.INTEGER),
            new CSVColumnDef("Provider Other Organization Name", JDBCType.VARCHAR),
            new CSVColumnDef("Provider Other Organization Name Type Code", JDBCType.VARCHAR));

    static String createTableSql(List<CSVColumnDef> cols, String tableName) {

        StringBuilder createStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        createStatement.append(
                cols.stream().map(new Function<CSVColumnDef, String>() {
                    @Override
                    public String apply(CSVColumnDef col) {
                        StringBuilder colSql = new StringBuilder();
                        colSql.append(col.dbName).append(" ");
                        switch (col.sqlType) {
                            case VARCHAR:
                                colSql.append(" VARCHAR(4000) ");
                                break;
                            case INTEGER:
                                colSql.append(" INTEGER ");
                                break;
                            default:
                                break;
                        }
                        return colSql.toString();
                    }
                }).collect(Collectors.joining(",")));
        createStatement.append(")");
        return createStatement.toString();
    }

    static DataSource getDataSource(String url) {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setUrl(url);
        return ds;
    }

    public static void main(String[] args) throws Exception {

        // Create Database
        //

        String dbpath = "jdbc:hsqldb:file:/Users/steveftoth/Projects/npi/hdb/db";
        String loadFile = "/Users/steveftoth/Projects/npi/data/othername_pfile_20050523-20220213.csv";

        String createTableSql = createTableSql(othername, "OTHERNAME");
        System.out.println(createTableSql);

        DataSource dataSource = getDataSource(dbpath);
        try (Connection dbConnection = dataSource.getConnection()) {
            dbConnection.createStatement().execute(createTableSql);
            Processor p = new Processor(dataSource, othername, "OTHERNAME");
            p.blobImporter(loadFile);
        } finally {
            if (dataSource instanceof JDBCPool) {
                ((JDBCPool) dataSource).close(30);
            }
        }
    }
}
