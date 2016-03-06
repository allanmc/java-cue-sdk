package dk.allanmc.cuesdk;

public enum CorsairError {
    CE_Success(0, "Success"),
    CE_ServerNotFound(1, "Server not found"),
    CE_NoControl(2, "No control"),
    CE_ProtocolHandshakeMissing(3, "Protocol handshake missing"),
    CE_IncompatibleProtocol(4, "Incompatible protocol"),
    CE_InvalidArguments(5, "Invalid arguments");

    int id;
    String message;

    public static CorsairError byId(int id) {
        for (CorsairError m : CorsairError.values()) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }

    CorsairError(int id, String message) {
        this.id = id;
        this.message = message;
    }
}
