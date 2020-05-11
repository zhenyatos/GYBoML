public class Class4 {

    public void slot0Param() {
        System.out.printf("Class4::slot0Param called.\n");
    }

    public void slot1aParam( int i ) {
        System.out.printf("Class4::slot1Param called. Int: %d\n", i);
    }

    public void slot1bParam( String[] sa ) {
        System.out.printf("Class4::slot1bParam called. String[]: %s, %s\n", sa[0], sa[1]);
    }

    public void slot2aParam( float f, String s ) {
        System.out.printf("Class4::slot2Param called. Float: %f, string: %s\n", f, s);
    }

    public void slot2bParam( int[] ia, float[] fa ) {
        System.out.printf("Class4::slot2Param called. Ia: %d, %d, Fa: %f, %f\n",
                ia[0], ia[1], fa[0], fa[1]);
    }
}
