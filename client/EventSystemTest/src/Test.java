import event.EventSystem;

public class Test {
    public static void main( String[] args ) {
        Class1 c1 = new Class1();
        Class2 c2 = new Class2();
        Class3 c3 = new Class3();
        Class4 c4 = new Class4();

        EventSystem es = EventSystem.get();

        //connect
        es.connect(c1, "signalNoParams", c3, "slot0Param");
        es.connect(c1, "signal1Param", c3, "slot0Param"); // BAD
        es.connect(c1, "signal1Param", c3, "slot1aParam");
        es.connect(c1, "signal2Param", c3, "slot2Param");

        es.connect(c2, "signalNoParams", c3, "slot0Param");
        es.connect(c2, "signal1Param", c3, "slot1bParam");

        es.connect(c2, "signal2aParam", c1, "slot2Param");

        es.connect(c2, "signal1Param", c4, "slot1bParam");
        es.connect(c2, "signal2aParam", c4, "slot2aParam");
        es.connect(c2, "signal2bParam", c4, "slot2bParam");
        es.connect(c2, "signal2aParam", c4, "slot2bParam"); // BAD

        es.connect(c3, "signalNoParams", c4, "slot0Param");
        es.connect(c3, "signal1Param", c4, "slot0Param"); // BAD
        es.connect(c3, "signal1Param", c4, "slot1aParam");

        //emit
        es.emit(c1, "signalNoParams");
        es.emit(c1, "signal1Param", 10);
        es.emit(c1, "signal2Param", 0.2, new int[]{5, 10});

        es.emit(c2, "signalNoParams");
        es.emit(c2, "signal1Param", (Object) new String[]{"s3", "s4"});
        es.emit(c2, "signal2aParam", 1.3f, "hello");
        es.emit(c2, "signal2bParam", new int[]{20,30}, new float[]{2.5f, 3.7f});

        es.emit(c3, "signalNoParams");
        es.emit(c3, "signal1Param", 13);

        // disconnect & emit
        System.out.println("After disconnect");
        es.disconnect(c2, "signal1Param", c4, "slot1bParam");
        es.emit(c2, "signal1Param", (Object) new String[]{"s1", "s2"});
    }
}
