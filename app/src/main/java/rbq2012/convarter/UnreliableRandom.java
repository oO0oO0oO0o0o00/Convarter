package rbq2012.convarter;

import java.util.Random;

public final class UnreliableRandom {

    final static private Random rand = new Random(System.currentTimeMillis() >> 2);

    static public long nextLong() {
        return rand.nextLong();
    }

}
