package epfl.javelo;

public final class Preconditions {
    private Preconditions() {}

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

    int max(int[] array) {
        Preconditions.checkArgument(array.length > 0);
        int temp = 0;
        int max = 0;
        for (int i=0; i < array.length; i++) {
            if (array[i] > temp) {
                temp = array[i];
                max = temp;
            }
        }
        return max;
    }
}

