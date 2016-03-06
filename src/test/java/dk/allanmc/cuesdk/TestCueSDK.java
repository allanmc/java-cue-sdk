package dk.allanmc.cuesdk;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestCueSDK {

    private static CueSDK instance;

    @BeforeClass
    public static void setUp() {
        instance = new CueSDK(false);
    }

    @Test
    public void testGetDeviceCount()  {
        final int numDevices = instance.corsairGetDeviceCount();
        System.out.println("numDevices = " + numDevices);
    }

    @Test
    public void testGetDeviceInfo()  {
        final CorsairDeviceInfo info = instance.corsairGetDeviceInfo(0);
        System.out.println("model = " + info.model.getString(0));
    }

    @Test
    public void testGetLedPositions() {
        final List<CorsairLedPosition> ledPositions = instance.corsairGetLedPositions();
        System.out.println("ledPositions.size = " + ledPositions.size());
    }

    @Test
    public void testRandomGlow() throws InterruptedException {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();

        final List<CorsairLedPosition> ledPositions = instance.corsairGetLedPositions();

        for (int i = 0; i < 100; i++) {
            final int key1 = rand.nextInt(0, ledPositions.size());
            int key2 = rand.nextInt(0, ledPositions.size() - 1);

            if (key1 == key2) {
                key2++;
            }

            instance.corsairSetLedsColors(Arrays.asList(
                    new CorsairLedColor(ledPositions.get(key1).ledId, rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255)),
                    new CorsairLedColor(ledPositions.get(key2).ledId, rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255))
            ));

            Thread.sleep(10);
        }
    }

}
