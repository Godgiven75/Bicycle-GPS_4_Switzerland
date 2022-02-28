package ch.epfl.javelo.projection;

import ch.epfl.test.TestRandomizer;
import epfl.javelo.Bits;
import epfl.javelo.Math2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class BitsTest {

    @Test
    void extractSignedThrowsOnIllegalStart() {
        assertThrows( IllegalArgumentException.class, () ->
                { Bits.extractSigned(27, -3, 2);
                });
    }

    @Test
    void extractSignedThrowsOnIllegalLength() {
        assertThrows( IllegalArgumentException.class, () ->
        { Bits.extractSigned(27, 4, -2);
        });
    }

    @Test
    void extractSignedThrowsOnIllegalRange() {
        assertThrows( IllegalArgumentException.class, () ->
        { Bits.extractSigned(27, 0, 33);
        });
    }
    @Test
    void extractUnsignedThrowsOnIllegalStart() {
        assertThrows(IllegalArgumentException.class, () ->
        {Bits.extractUnsigned(27, -3, 2);
        });
    }

    @Test
    void extractUnsignedThrowsOnIllegalLength() {
        assertThrows(IllegalArgumentException.class, () ->
        {Bits.extractUnsigned(27, 2, -7);
        });
    }

    @Test
    void extractUnsignedThrowsOnIllegalRange() {
        assertThrows(IllegalArgumentException.class, () ->
        {Bits.extractUnsigned(27, 2, 31);
        });
    }


    @Test
    void extractSignedWorksOnNormalValues() {
        int i = 0b11001010111111101011101010111110;
        int expected = 0b11111111111111111111111111111010;
        int actual = Bits.extractSigned(i, 8, 4);
        assertEquals(expected, actual);
    }

    @Test
    void extractSignedWorksOnSpecialValues() {
        int i = Integer.MIN_VALUE;
        int expected = 0;
        int actual = Bits.extractSigned(i, 5, 4);
        assertEquals(expected, actual);

        i = Integer.MAX_VALUE;
        expected = Integer.MAX_VALUE ;
        actual = Bits.extractSigned(i,0, 32);
        assertEquals(expected, actual);
    }

    @Test
    void extractUnsignedWorksOnNormalValues() {
        int i = 0b11001010111111101011101010111110;
        int expected = 0b1010;
        int actual = Bits.extractUnsigned(i, 8, 4);
        assertEquals(expected, actual);
    }

    @Test
    void extractUnsignedWorksOnSpecialValues() {
        int i = Integer.MIN_VALUE;
        int expected = 0;
        int actual = Bits.extractUnsigned(i, 5, 4);
        assertEquals(expected, actual);

        i = Integer.MAX_VALUE;
        expected = 0b0_111_111_1111;
        actual = Bits.extractUnsigned(i,5, 10);
        assertEquals(expected, actual);
    }
}
