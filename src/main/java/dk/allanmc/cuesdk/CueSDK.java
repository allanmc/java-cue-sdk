package dk.allanmc.cuesdk;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CueSDK {

    private CueSDKLibrary instance;

    /**
     * Instantiates a new CueSDK, and establishes a connection to the Corsair SDK with shared control.
     */
    public CueSDK() {
        this(false);
    }

    /**
     * Instantiates a new CueSDK, and establishes a connection to the Corsair SDK with shared or exclusive control.
     * @param exclusiveLightingControl Whether exclusive light control is needed or not.
     */
    public CueSDK(boolean exclusiveLightingControl) {
        instance = CueSDKLibrary.INSTANCE;
        final CorsairProtocolDetails.ByValue byValue = instance.CorsairPerformProtocolHandshake();

        if (exclusiveLightingControl) {
            final byte ret = instance.CorsairRequestControl(CueSDKLibrary.CorsairAccessMode.CAM_ExclusiveLightingControl);
            if (ret != 1) {
                handleError();
            }
        }
    }

    /**
     * Get the number of connected devices compatible with the Corsair CUE SDK.
     * @return number of devices
     */
    public int corsairGetDeviceCount() {
        return instance.CorsairGetDeviceCount();
    }

    /**
     * Retrieve a list of available LED positions, including their id and physical properties.
     * @return list of LED details
     */
    public List<CorsairLedPosition> corsairGetLedPositions() {
        final CorsairLedPositions corsairLedPositions = instance.CorsairGetLedPositions();
        final CorsairLedPosition.ByReference pLedPosition = corsairLedPositions.pLedPosition;
        final CorsairLedPosition[] ledPositions = (CorsairLedPosition[]) pLedPosition.toArray(new CorsairLedPosition[corsairLedPositions.numberOfLed]);
        return Arrays.asList(ledPositions);
    }

    /**
     * Set the color af several LED at the same time.
     * @param ledColors List of LED identifiers and colors
     */
    public void corsairSetLedsColors(Collection<CorsairLedColor> ledColors) {
        if (ledColors == null || ledColors.isEmpty()) {
            return;
        }
        final Iterator<CorsairLedColor> iterator = ledColors.iterator();
        CorsairLedColor ledColor = iterator.next();
        if (ledColors.size() == 1) {
            corsairSetLedColor(ledColor);
        } else {
            final CorsairLedColor[] fixedLedColors = (CorsairLedColor[]) new CorsairLedColor().toArray(ledColors.size());
            int index = 0;
            copyCorsairLedColor(fixedLedColors[index++], ledColor);
            while (iterator.hasNext()) {
                ledColor = iterator.next();
                copyCorsairLedColor(fixedLedColors[index++], ledColor);
            }
            final byte ret = instance.CorsairSetLedsColors(fixedLedColors.length, fixedLedColors[0]);
            if (ret != 1) {
                handleError();
            }
        }
    }

    /**
     * Set the color of a single LED.
     * @param ledColor LED identifier and color
     */
    public void corsairSetLedColor(CorsairLedColor ledColor) {
        if (ledColor == null) {
            return;
        }
        final byte ret = instance.CorsairSetLedsColors(1, ledColor);
        if (ret != 1) {
            handleError();
        }
    }

    private void copyCorsairLedColor(CorsairLedColor dst, CorsairLedColor src) {
        dst.ledId = src.ledId;
        dst.r = src.r;
        dst.g = src.g;
        dst.b = src.b;
    }

    private void handleError() {
        final int errorId = instance.CorsairGetLastError();
        final CorsairError error = CorsairError.byId(errorId);
        throw new RuntimeException(error + " - " + error.message);
    }
}
