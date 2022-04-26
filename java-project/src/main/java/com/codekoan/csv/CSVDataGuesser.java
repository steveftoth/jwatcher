package com.codekoan.csv;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * CSVLinker class takes in a set of csv files, and is able to guess the structure of the files, hints are also given to this process which 
 * can influence the resulting structure.
 * 
 * The structure is output as a proto structure?  This could also be a json schema
 * 
 */
public class CSVDataGuesser {
    enum CSVType{}

   public static class Binder{
       File source;
       //SchemaHint hint;
       boolean isPrimaryTable;
       String[] primary_keys;
       String[] unique_values;
       Map<String, CSVType> type_hints;
   }


    //public void readSchema(File[] files, SchmeaHints[] hints)
public interface Generator<T> {
    List<String> getRequired();
    //T apply(final ImmutableMap<String,Object> record);
}

    public static class Generators {
       //Map<String, Generator>

    }
}
