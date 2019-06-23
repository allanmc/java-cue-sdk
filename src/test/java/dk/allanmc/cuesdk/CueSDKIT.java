package dk.allanmc.cuesdk;

import static dk.allanmc.cuesdk.jna.CueSDKLibrary.CorsairLedId;

import dk.allanmc.cuesdk.enums.DeviceCaps;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CueSDKIT {

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
        int numDevices = instance.getDeviceCount();
        while (numDevices > 0) {
            final DeviceInfo info = instance.getDeviceInfo(--numDevices);
            System.out.println("type = " + info.getType());
            System.out.println("model = " + info.getModel());
            System.out.println("logicalLayout = " + info.getLogicalLayout());
            System.out.println("physicalLayout = " + info.getPhysicalLayout());
            System.out.println("supports lightning = " + info.hasCapability(DeviceCaps.CDC_Lighting));
            System.out.println();
        }
    }

    @Test
    public void testGetLedPositions() {
        final List<LedPosition> ledPositions = instance.getLedPositions();
        System.out.println("ledPositions.size = " + ledPositions.size());
    }

    @Test
    public void testLedId() {
        instance.setLedColor(new LedColor(CorsairLedId.CLK_Enter, 255, 255, 255));
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
        cue.setLedColor(new LedColor(CorsairLedId.CLK_Enter, 255, 0, 0));
        // Set LED of the left Shift key to green
        cue.setLedColor(new LedColor(CorsairLedId.CLK_RightShift, Color.GREEN));

        // Set color of multiple keys at the same time
        cue.setLedsColors(Arrays.asList(
                new LedColor(CorsairLedId.CLK_W, Color.YELLOW),
                new LedColor(CorsairLedId.CLK_A, Color.YELLOW),
                new LedColor(CorsairLedId.CLK_S, Color.YELLOW),
                new LedColor(CorsairLedId.CLK_D, Color.YELLOW)
        ));

        // Wait some time before exiting, so we can see the LEDs.
        Thread.sleep(1000);
    }

}
