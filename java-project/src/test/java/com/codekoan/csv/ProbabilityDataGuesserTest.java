package com.codekoan.csv;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class ProbabilityDataGuesserTest extends TestCase {

    public void testGuesser() {
        ProbabilityDataGuesser guesser = new ProbabilityDataGuesser();
        guesser.buildGuesser(List.of("test","test", "a", "t").stream());

        
        System.out.println(guesser.root.toString());
        System.out.println(guesser.generateGuess(new Random(0)));
        System.out.println(guesser.generateGuess(new Random(1)));
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            //System.out.println(guesser.generateGuess(r));
        }
    }

}
