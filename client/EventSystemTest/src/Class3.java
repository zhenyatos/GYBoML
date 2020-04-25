public class Class3 {
    public void signalNoParams() {}
    public void signal1Param( int i ) {}

    public void slot0Param() {
        System.out.println("Class3::slot0Param called.");
    }

    public void slot1aParam( int i ) {
        System.out.printf("Class3::slot1aParam called. Int: %d\n", i);
    }

    public void slot1bParam( String[] sa ) {
        System.out.printf("Class3::slot1bParam called. String[]: %s, %s\n", sa[0], sa[1]);
    }

    public void slot2Param( double d, int[] ia ) {
        System.out.printf("Class3::slot2Param called. Double: %f, int[]: %d, %d\n",
                d, ia[0], ia[1]);
    }
}
