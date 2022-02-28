package ch.epfl.test;

import epfl.javelo.projection.data.AttributeSet;
import org.junit.jupiter.api.Test;
import static epfl.javelo.projection.data.Attribute.HIGHWAY_TRACK;
import static epfl.javelo.projection.data.Attribute.TRACKTYPE_GRADE1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestAttributeSet {



    @Test
    public void checkStringRepresentation() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}
