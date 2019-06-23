package dk.allanmc.cuesdk.enums;

public enum DeviceType {
    CDT_Unknown,
    CDT_Mouse,
    CDT_Keyboard,
    CDT_Headset,
    CDT_MouseMat,
    CDT_HeadsetStand,
    CDT_CommanderPro,
    CDT_LightingNodePro,
    CDT_MemoryModule,
    CDT_Cooler;

    private static DeviceType[] values = DeviceType.values();

    public static DeviceType byOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        return null;
    }
}
