package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static epfl.javelo.Q28_4.asDouble;
import static epfl.javelo.Q28_4.ofInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    //Méthode ofInt
    @Test
    public void checkOfInt() {
        assertEquals(80, ofInt(5));
        assertEquals(48, ofInt(3));
        assertEquals(0, ofInt(0));
    }

    //Méthode ofDouble
    @Test
    public void checkAsDouble() {
        //assertEquals(, asDouble(6));
        System.out.println(asDouble(0b10011100));
    }

}
