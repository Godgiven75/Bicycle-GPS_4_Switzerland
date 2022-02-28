package ch.epfl.test;

import epfl.javelo.projection.data.Attribute;
import epfl.javelo.projection.data.AttributeSet;
import org.junit.jupiter.api.Test;

import static epfl.javelo.projection.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TestAttributeSet {

    //Le constructeur
    @Test
    public void throwsExceptionIfGivenAttributeHas1NotCorrespondingToAnyAttribute() {
        long bits = Long.MIN_VALUE;
        System.out.println(Long.toBinaryString(bits));
        //AttributeSet set = new AttributeSet(bits);
        System.out.println(Long.toBinaryString(bits >>> Attribute.values().length));
        System.out.println((bits >>> Attribute.values().length) == 0L);
        assertThrows(IllegalArgumentException.class, () ->
        {AttributeSet set = new AttributeSet(bits);
        });
    }

    //Méthode of
    @Test
    public void givesArgumentAttributesBack() {
        assertEquals(new AttributeSet(7L),
                AttributeSet.of(HIGHWAY_SERVICE, HIGHWAY_TRACK, HIGHWAY_RESIDENTIAL));
    }

    //Méthode contains
    @Test
    public void checkIfContains() {
        assertTrue((new AttributeSet(15L).contains(HIGHWAY_RESIDENTIAL)));
    }

    @Test
    public void checkStringRepresentation() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}
