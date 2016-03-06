package dk.allanmc.cuesdk;

import dk.allanmc.cuesdk.enums.DeviceCaps;
import dk.allanmc.cuesdk.enums.DeviceType;
import dk.allanmc.cuesdk.enums.LogicalLayout;
import dk.allanmc.cuesdk.enums.PhysicalLayout;
import dk.allanmc.cuesdk.jna.CorsairDeviceInfo;

public class DeviceInfo {

    // device type
    private DeviceType type;

    // device model
    private String model;

    // physical layout of the keyboard or mouse
    private PhysicalLayout physicalLayout;

    // logical layout of the keyboard as set in CUE settings
    private LogicalLayout logicalLayout;

    // mask that describes device capabilities, formed as logical 'or' of CorsairDeviceCaps enum values
    private int capsMask;

    public DeviceInfo(CorsairDeviceInfo deviceInfo) {
        this.type = DeviceType.byOrdinal(deviceInfo.type);
        this.model = deviceInfo.model.getString(0);
        this.physicalLayout = PhysicalLayout.byOrdinal(deviceInfo.physicalLayout);
        this.logicalLayout = LogicalLayout.byOrdinal(deviceInfo.logicalLayout);
    }

    public DeviceType getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    public PhysicalLayout getPhysicalLayout() {
        return physicalLayout;
    }

    public LogicalLayout getLogicalLayout() {
        return logicalLayout;
    }

    public int getCapsMask() {
        return capsMask;
    }

    public boolean hasCapability(DeviceCaps cap) {
        return (capsMask | cap.ordinal()) == cap.ordinal();
    }
}
