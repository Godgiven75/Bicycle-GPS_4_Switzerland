package ch.epfl.test;

import epfl.javelo.projection.data.Attribute;
import epfl.javelo.projection.data.AttributeSet;
import org.junit.jupiter.api.Test;
import static epfl.javelo.projection.data.Attribute.HIGHWAY_TRACK;
import static epfl.javelo.projection.data.Attribute.TRACKTYPE_GRADE1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TestAttributeSet {

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

    @Test
    public void checkStringRepresentation() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}
