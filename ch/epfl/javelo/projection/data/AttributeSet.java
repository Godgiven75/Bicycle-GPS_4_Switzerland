package epfl.javelo.projection.data;

import java.util.EnumSet;
import java.util.StringJoiner;

public record AttributeSet(long bits) {

    public AttributeSet {
        /*
        String stringBits = Long.toString(bits);
        int length = (int) (Math.log10(bits)+1);
        if (length > Attribute.values().length) {
            for (int i=Attribute.values().length; i < length; i++) {
                if (stringBits.charAt(i) == 1) {
                    throw new IllegalArgumentException();
                }
            }
        }*/
    }

    public static AttributeSet of(Attribute... attributes) {
        EnumSet<Attribute> e_set = EnumSet.allOf(Attribute.class);
        return e_set;
    }

    public boolean contains(Attribute attribute) {
        return this.contains(attribute);
    }

    public boolean intersects(AttributeSet that) {

    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int i = 0; i < ) {
            if (AttributeSet == 1)
            Attribute a =
            j.add(a.key() + " = " + a.value());
        }

        return j.toString();
    }
}
