package com.codekoan.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lombok.Data;
import lombok.ToString;

// Memorizes an input set and reproduces it with the same frequency that it was passed in.
public class ProbabilityDataGuesser {

    PChain root = new PChain();

    void buildGuesser(Stream<String> values) {
        root.end=false;
        values.forEach((String value) -> {
            PChain node = root;
            for (char c : value.toCharArray()) {
                node = node.addEdge(c);
            }
            node.addEnd();
        });
        root.normalizeEdges();
    }

    @Data
    static class PChainEntry implements Comparable<PChainEntry> {

        PChainEntry() {
            next = null;
            p = 1;
        }

        double p;
        PChain next;

        @Override
        public int compareTo(PChainEntry o) {
            return Double.compare(this.p, o.p);
        }
    }

    @ToString 
    static class PChain {
        PChain() {
            end = true;
        }

        PChain(char value) {
            end = false;
            this.value = value;
        }

        List<PChainEntry> edges=new ArrayList<>();
        char value;
        boolean end;

        void normalizeEdges() {
            if(this.end) return;
            // first normalize this set of edge values
            final double total = edges.stream().map(x -> x.p).reduce((x,y)-> x+y).get();
            //System.out.println("Total:"+ total);
            edges.stream().forEach(entry -> entry.p= entry.p/total);
            edges.stream().forEach(entry -> entry.next.normalizeEdges());
            // then descend.
        }

        PChain addEdge(char value) {
            PChainEntry r = null;
            for (PChainEntry entry : edges) {
                if (entry.next.value == value) {
                    entry.p += 1;
                    r = entry;
                    break;
                }
            }
            if (r == null) {
                r = new PChainEntry();
                r.next = new PChain(value);
                edges.add(r);
            }
            return r.next;
        }

        void addEnd() {
            PChainEntry r = null;
            for (PChainEntry entry : edges) {
                if (entry.next.end) {
                    entry.p += 1;
                    r = entry;
                    break;
                }
            }
            if (r == null) {
                r = new PChainEntry();
                r.next = new PChain();
                edges.add(r);
            }
        }

        PChain findEdge(char value) {
            return null;
        }
    }

    String generateGuess(Random seed) {
        StringBuilder result = new StringBuilder();

        PChain cnode = root;
        outer: do {
            double nr = seed.nextDouble();
            for (PChainEntry next : cnode.edges) {
                nr -= next.p;
                if (nr <= 0) {
                    cnode = next.next;
                    if (cnode.end) {
                        cnode = next.next;
                        break outer;
                    } else {
                        result.append(cnode.value);
                        break;
                    }
                }
            }
            if( nr > 0 ) throw new RuntimeException("failure, normalization " + result.toString() + " " + nr);
        } while (cnode != null);
        return result.toString();

    }
}
