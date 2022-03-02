package ch.epfl.javelo.projection;

import ch.epfl.test.TestRandomizer;
import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;


public class Q28_4Test {


    @Test
    void ofIntThrowsOnALLIntegersThatExceed28Bits() {
        for(int i = Integer.MIN_VALUE; i < - 1; ++i) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> {
                System.out.println(Integer.toBinaryString(finalI));
                Q28_4.ofInt(finalI);
            });
        }
        for(int i = 0B1_0000_0000_0000_0000_0000_0000_0000; i < 0B1111111111111111111111111111111; ++i) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> {
                System.out.println(Integer.toBinaryString(finalI));
                Q28_4.ofInt(finalI);
            });
        }
    }

    @Test
    void ofIntWorksOnPositiveNumbers() {
        var rng = newRandom();
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int v1 = rng.nextInt(0, 0b111111111111111111111111111);
            assertEquals(Q28_4.asFloat(Q28_4.ofInt(v1)),(float)v1);
        }

    }

    @Test
    void ofIntWorksOnNegativeNumbers() {
        var rng = newRandom();
        for(int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int v1 = rng.nextInt(0b11111000000000000000000000000000, -1);
            System.out.println(v1);
            System.out.println(Q28_4.asDouble(Q28_4.ofInt(v1)));
            assertEquals(Q28_4.asDouble(Q28_4.ofInt(v1)),(double)v1 );
        }

    }

    @Test
    void asDoubleWorksOnPositiveNumbers() {
        int q28_4 = 0b1000011100;
        double expected = 33.75;
        double actual = Q28_4.asDouble(q28_4);
        assertEquals(expected, actual);
    }

    @Test
    void asDoubleWorksOnNegativeNumbers() {
        int q28_4 = 0b1111111111111111111010001011_0111;
        double expected  = -372.5625;
        double actual = Q28_4.asDouble(q28_4);
        assertEquals(expected, actual);
    }

    @Test
    void asFloatWorksOnPositiveNumbers() {
        int q28_4 = 0b1000011100;
        float expected = 33.75f;
        float actual = Q28_4.asFloat(q28_4);
        assertEquals(expected, actual);
    }

    @Test
    void asFloatWorksOnNegativeNumbers() {
        int q28_4 = 0b1111111111111111111010001011_0111;
        float expected  = -372.5625f;
        float actual = Q28_4.asFloat(q28_4);
        assertEquals(expected, actual);
    }










}
