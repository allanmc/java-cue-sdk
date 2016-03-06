package dk.allanmc.cuesdk;

import dk.allanmc.cuesdk.enums.CorsairError;
import dk.allanmc.cuesdk.jna.CorsairLedColor;
import dk.allanmc.cuesdk.jna.CorsairLedPosition;
import dk.allanmc.cuesdk.jna.CorsairLedPositions;
import dk.allanmc.cuesdk.jna.CorsairProtocolDetails;
import dk.allanmc.cuesdk.jna.CueSDKLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CueSDK {

    private final CueSDKLibrary instance;

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
        final CorsairProtocolDetails.ByValue protocolDetails = instance.CorsairPerformProtocolHandshake();

        if (protocolDetails.breakingChanges != 0) {
            String sdkVersion = protocolDetails.sdkVersion.getString(0);
            String cueVersion = protocolDetails.serverVersion.getString(0);
            throw new RuntimeException("Incompatible SDK (" + sdkVersion + ") and CUE " + cueVersion + " versions.");
        }

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
    public int getDeviceCount() {
        return instance.CorsairGetDeviceCount();
    }

    /**
     *  Retrieve information about a connected device.
     * @param deviceIndex Index of the connected device to get information about
     * @return device information
     */
    public DeviceInfo getDeviceInfo(int deviceIndex) {
        return new DeviceInfo(instance.CorsairGetDeviceInfo(deviceIndex));
    }

    /**
     * Retrieve a list of available LED positions, including their id and physical properties.
     * @return list of LED details
     */
    public List<LedPosition> getLedPositions() {
        final CorsairLedPositions corsairLedPositions = instance.CorsairGetLedPositions();
        final ArrayList<LedPosition> ledPositions = new ArrayList<>();
        final int count = corsairLedPositions.numberOfLed;

        if (corsairLedPositions != null && count > 0) {
            final CorsairLedPosition.ByReference pLedPosition = corsairLedPositions.pLedPosition;
            final CorsairLedPosition[] nativeLedPositions = (CorsairLedPosition[]) pLedPosition.toArray(new CorsairLedPosition[count]);
            ledPositions.ensureCapacity(count);
            for (CorsairLedPosition nativeLedPosition : nativeLedPositions) {
                ledPositions.add(new LedPosition(nativeLedPosition));
            }

        }
        return ledPositions;
    }

    /**
     * Set the color af several LED at the same time.
     * @param ledColors List of LED identifiers and colors
     */
    public void setLedsColors(Collection<LedColor> ledColors) {
        if (ledColors == null || ledColors.isEmpty()) {
            return;
        }
        final Iterator<LedColor> iterator = ledColors.iterator();
        LedColor ledColor = iterator.next();
        if (ledColors.size() == 1) {
            setLedColor(ledColor);
        } else {
            final CorsairLedColor[] nativeLedColors = (CorsairLedColor[]) new CorsairLedColor().toArray(ledColors.size());
            int index = 0;
            copyCorsairLedColor(ledColor, nativeLedColors[index++]);
            while (iterator.hasNext()) {
                ledColor = iterator.next();
                copyCorsairLedColor(ledColor, nativeLedColors[index++]);
            }
            final byte ret = instance.CorsairSetLedsColors(nativeLedColors.length, nativeLedColors[0]);
            if (ret != 1) {
                handleError();
            }
        }
    }

    /**
     * Set the color of a single LED.
     * @param ledColor LED identifier and color
     */
    public void setLedColor(LedColor ledColor) {
        if (ledColor == null) {
            return;
        }
        final CorsairLedColor nativeLedColor = new CorsairLedColor();
        copyCorsairLedColor(ledColor, nativeLedColor);
        final byte ret = instance.CorsairSetLedsColors(1, nativeLedColor);
        if (ret != 1) {
            handleError();
        }
    }

    private void copyCorsairLedColor(LedColor src, CorsairLedColor dst) {
        dst.ledId = src.ledId;
        dst.r = src.r;
        dst.g = src.g;
        dst.b = src.b;
    }

    private void handleError() {
        final int errorId = instance.CorsairGetLastError();
        final CorsairError error = CorsairError.byOrdinal(errorId);
        throw new RuntimeException(error + " - " + error.getMessage());
    }
}
