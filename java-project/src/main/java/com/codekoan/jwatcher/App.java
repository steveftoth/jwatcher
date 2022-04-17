package com.codekoan.jwatcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App 
{

    public static Set<Path> reportNewFiles(Set<Path> previousFiles, Path rootPath) throws IOException {
        
        Set<Path> newPaths = new HashSet<>();
        try(Stream<Path> files = Files.walk(rootPath)) {
            final Set<Path> initialFiles = newPaths;
            files.collect(Collectors.toCollection(() -> initialFiles));
            }
    
        newPaths.removeAll(previousFiles);
        if(newPaths.isEmpty()) {
 return previousFiles;
        }
for(Path np : newPaths) {
    System.out.println("Found: " + np.toString());
}
previousFiles.addAll(newPaths);
return previousFiles;
    }

    public static void main( String[] args ) throws Exception
    {
        String watchDir="/Users/steveftoth/Projects";
        final Path rootPath =FileSystems.getDefault().getPath(watchDir);
        final Set<Path> initialFiles = new HashSet<>();
        try(Stream<Path> files = Files.walk(rootPath)) {
        files.collect(Collectors.toCollection(() -> initialFiles));
        }
        System.out.println("Found: " +initialFiles.size());
        Set<Path> nFiles= initialFiles;
        while(true) {
            Thread.sleep(10000);
            nFiles = reportNewFiles(nFiles,rootPath);
        }
    }
}
