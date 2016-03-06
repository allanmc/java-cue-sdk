package dk.allanmc.cuesdk;

import dk.allanmc.cuesdk.enums.DeviceCaps;
import dk.allanmc.cuesdk.enums.LedId;
import dk.allanmc.cuesdk.enums.PhysicalLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestCueSDK {

    private static CueSDK instance;

    @BeforeClass
    public static void setUp() {
        instance = new CueSDK(true);
    }

    @Test
    public void testGetDeviceCount()  {
        final int numDevices = instance.getDeviceCount();
        System.out.println("numDevices = " + numDevices);
    }

    @Test
    public void testGetDeviceInfo()  {
        final DeviceInfo info = instance.getDeviceInfo(0);
        System.out.println("type = " + info.getType());
        System.out.println("model = " + info.getModel());
        System.out.println("logicalLayout = " + info.getLogicalLayout());
        System.out.println("physicalLayout = " + info.getPhysicalLayout());
        System.out.println("supports lightning = " + info.hasCapability(DeviceCaps.CDC_Lighting));
    }

    @Test
    public void testGetLedPositions() {
        final List<LedPosition> ledPositions = instance.getLedPositions();
        System.out.println("ledPositions.size = " + ledPositions.size());
    }

    @Test
    public void testLedId() throws InterruptedException {
        instance.setLedColor(new LedColor(LedId.CLK_Enter, 255, 255, 255));
    }

    @Test
    public void testRandomGlow() throws InterruptedException {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();

        final List<LedPosition> ledPositions = instance.getLedPositions();

        for (int i = 0; i < 100; i++) {
            final int key1 = rand.nextInt(0, ledPositions.size());
            int key2 = rand.nextInt(0, ledPositions.size() - 1);

            if (key1 == key2) {
                key2++;
            }

            instance.setLedsColors(Arrays.asList(
                    new LedColor(ledPositions.get(key1), rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255)),
                    new LedColor(ledPositions.get(key2), rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255))
            ));
            Thread.sleep(10);
        }
    }

    @Test
    public void testExample() throws InterruptedException {
        // Establish connection with device
        final CueSDK cue = new CueSDK(true);

        // Set LED of the Enter key to red
        cue.setLedColor(new LedColor(LedId.CLK_Enter, 255, 0, 0));
        // Set LED of the left Shift key to green
        cue.setLedColor(new LedColor(LedId.CLK_RightShift, Color.GREEN));

        // Set color of multiple keys at the same time
        cue.setLedsColors(Arrays.asList(
                new LedColor(LedId.CLK_W, Color.YELLOW),
                new LedColor(LedId.CLK_A, Color.YELLOW),
                new LedColor(LedId.CLK_S, Color.YELLOW),
                new LedColor(LedId.CLK_D, Color.YELLOW)
        ));

        // Wait some time before exiting, so we can see the LEDs.
        Thread.sleep(1000);
    }

}
