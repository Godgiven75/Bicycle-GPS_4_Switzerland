package epfl.javelo.projection.data;

public record AttributeSet(long bits) {
    public AttributeSet {
        String stringBits = Long.toString(bits);
        int length = (int) (Math.log10(bits)+1);
        if (length > Attribute.values().length) {
            for (int i=Attribute.values().length; i < length; i++) {
                if (stringBits.charAt(i) == 1) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
}
