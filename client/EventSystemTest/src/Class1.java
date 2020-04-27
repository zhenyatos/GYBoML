public class Class1 {
    public void signalNoParams() {}
    public void signal1Param( int i ) {}
    public void signal2Param( double d, int[] ia )  {}

    public void slot2Param( float f, String s ) {
        System.out.printf("Class1::slot2Param called. Float: %f, String: %s\n", f, s);
    }
}
