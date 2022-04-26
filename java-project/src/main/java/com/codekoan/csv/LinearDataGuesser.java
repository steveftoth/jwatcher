package com.codekoan.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;

public class LinearDataGuesser {

    List<List<Prob>> p_guess = new ArrayList<>();

    // histogram of lengths storing normalized probibilty to pick.
    // .1 , .2 , .3 , .4
    // 10% 0 char length
    // 20% 1 char length
    // 30% 2 char length
    // 40% 3 char length
    ArrayList<Double> histogram_lengths = new ArrayList<>();

    @Data
    static private class Prob implements Comparable<Prob> {
        double p;
        char value;

        public Prob(double p, char value) {
            this.p = p;
            this.value = value;
        }

        @Override
        public int compareTo(Prob o) {
            return Double.compare(this.p, o.p);
        }
    }

    void buildGuesser(String value) {
        addToHistogram(value.length());
        addToGuesses(value);
    }

    void buildGuesser(Stream<String> values) {

        values.forEach((String value) -> {
            addToHistogram(value.length());
            addToGuesses(value);
        });
        normalize();
    }

    private void addToGuesses(String value) {
        while (p_guess.size() < value.length()) {
            p_guess.add(new ArrayList<>());
        }
        for (int i = 0; i < value.length(); ++i) {
            List<Prob> probs = p_guess.get(i);
            final char toFind = value.charAt(i);
            Optional<Prob> foundProb = probs.stream().filter(p -> p.value == toFind).findFirst();
            if (foundProb.isEmpty()) {
                probs.add(new Prob(1.0, toFind));
            } else {
                foundProb.get().p++;
            }
        }
    }

    private void addToHistogram(int length) {
        histogram_lengths.ensureCapacity(length);
        while (histogram_lengths.size() < length + 1) {
            histogram_lengths.add(0.0d);
        }
        histogram_lengths.set(length, histogram_lengths.get(length) + 1);
    }

    public void normalize() {
        {
            final double sum = histogram_lengths.stream().reduce((a, b) -> a + b).orElse(Double.valueOf(0d));
            histogram_lengths = new ArrayList<>(
                    histogram_lengths.stream().map(x -> x / sum).collect(Collectors.toList()));
        }
        p_guess = new ArrayList<>(p_guess.stream().map(probList -> {
            final double sum = probList.stream().map(a -> a.p).reduce((a, b) -> a + b).orElse(0d);
            return new ArrayList<>(
                    probList.stream().map(
                            prob -> new Prob(prob.p / sum, prob.value)).collect(Collectors.toList()));
        }).collect(Collectors.toList()));

    }

    String generateGuess(Random r) {
        StringBuilder result = new StringBuilder();
        double g = r.nextDouble();
        int length = 0;
        while (g > 0 && histogram_lengths.size() - 1 > length) {
            g -= histogram_lengths.get(length);
            ++length;
        }

        for (int i = 0; i < length; ++i) {
            double cg = r.nextDouble();
            Iterator<Prob> guessValues = p_guess.get(i).iterator();
            Prob p = null;
            while (cg > 0 && guessValues.hasNext()) {
                p = guessValues.next();
                cg -= p.p;
            }
            if (cg > 0 || p == null) {
                throw new RuntimeException("ERROR");
            }
            result.append(p.value);
        }
        return result.toString();
    }
}
