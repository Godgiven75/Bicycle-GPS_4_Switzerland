package epfl.javelo;

public final class Bits {
     private Bits() {}

    public  static int extractSigned(int value, int start, int length) {
        int rangeSize = start + length;
        if (start < 0 || !( (rangeSize > 0) && (rangeSize < 32))) throw new IllegalArgumentException();
        int leftShift = value << start;
        return leftShift >>> 32 - start;


    }

    public static int extractUnsigned(int value, int start, int length) {
        int rangeSize = start + length;
        if (start < 0 || !((rangeSize > 0) && (rangeSize <= 32))) throw new IllegalArgumentException();
        int leftShift = value << start;
        return leftShift >> 32 - start;

    }

}
