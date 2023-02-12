package dev.fangscl;

import com.google.common.base.Splitter;

import java.io.CharArrayReader;
import java.util.Spliterator;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
         Splitter.on("")
                 .split("abc a")
                .forEach(System.out::println);

    }
}