package epfl.javelo.projection.data;

import epfl.javelo.Preconditions;

import java.util.StringJoiner;

import static epfl.javelo.projection.data.Attribute.ALL;

public record AttributeSet(long bits) {

    public AttributeSet {
        Preconditions.checkArgument((bits << Attribute.values().length) == 0L);
    }

    public static AttributeSet of(Attribute... attributes) {
        long nb = 0L;
        for (Attribute a : attributes) {
            long mask = 1L << a.ordinal();
            nb |= mask;
        }
        return new AttributeSet(nb);
    }

    public boolean contains(Attribute attribute) {
        return (this.bits << attribute.ordinal()) == 1;
    }

    public boolean intersects(AttributeSet that) {
        return (this.bits & that.bits) != 0L;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < 64; i++) {
            if ( ( (bits >> i) % 2 == 1 ) ) {
                j.add(ALL.get(i).key()).add(ALL.get(i).value());
            }
        }
        return j.toString();
    }
}

