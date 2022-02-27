package epfl.javelo;


public final class Q28_4 {
    private Q28_4() {}

    public static void main(String[] args) {
        System.out.println(ofInt(0b10011100 ));
    }
    // scalb et l'opérateur de décalage revoient une interprétation non-signée, ce qui doit être compensée en soustreyant
    // 16. Y a-t-il une meilleure façon de faire cela ?
    public static int ofInt(int i){
        return (i >>> 4) - 16;
    }

    public static double asDouble(int q28_4) {
        return Math.scalb(q28_4, -4) - 16 ;
    }

    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -4) - 16;
    }
}
