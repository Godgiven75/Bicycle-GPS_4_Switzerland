package epfl.javelo;

public final class Bits {
    private Bits() {}

    public int extractSigned(int value, int start, int length) {
        int range = start + length;
        if ( !( (range > 0) && (range < 32) ) ) throw new IllegalArgumentException();
    }

    public int extractUnsigned(int value, int start, int length) {

    }

}
