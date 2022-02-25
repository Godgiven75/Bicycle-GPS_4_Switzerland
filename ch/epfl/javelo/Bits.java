package epfl.javelo;

public final class Bits {
     private Bits() {}

    public static void main(String[] args) {
         int test = 0b11001010111111101011101010111110;
         String signed = Integer.toBinaryString(extractSigned(test, 8, 11));
         String unsigned = Integer.toBinaryString(extractUnsigned(test, 8, 11));
        System.out.println(signed);
        System.out.println(unsigned);

    }

    public static int extractSigned(int value, int start, int length) {
        int rangeSize = start + length;
        Preconditions.checkArgument(start > 0 && start < length &&  0 <= rangeSize && rangeSize <= 32);
        int leftShift = value << start;
        return leftShift >> 32 - start;


    }

    public static int extractUnsigned(int value, int start, int length) {
        int rangeSize = start + length;
        Preconditions.checkArgument(start > 0 && start < length &&  0 <= rangeSize && rangeSize < 32);
        int leftShift = value << start;
        return leftShift >>> 32 - start;

    }

}
