package com.codekoan.csv;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class LinearDataGuesserTest extends TestCase {

    public void testGuesser() {
        LinearDataGuesser guesser = new LinearDataGuesser();
        guesser.buildGuesser(List.of("test", "test", "a", "t").stream());

        System.out.println(guesser.p_guess);
        System.out.println(guesser.generateGuess(new Random(0)));
        System.out.println(guesser.generateGuess(new Random(1)));
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            System.out.println(guesser.generateGuess(r));
        }
    }

    public void testGuesserSimple() {
        LinearDataGuesser guesser = new LinearDataGuesser();
        guesser.buildGuesser(List.of("test", "test", "test", "test").stream());
        Random r = new Random();
        assertEquals("test", guesser.generateGuess(r));
    }

    public void testGuesserSimpleRandom() {
        LinearDataGuesser guesser = new LinearDataGuesser();
        guesser.buildGuesser(List.of("test", "t", "t", "t").stream());
        Random r = new Random();
        for (int i = 0; i < 10; ++i) {
            if (guesser.generateGuess(r).equals("test")) {
                return;
            }
        }
        fail();
    }

}
