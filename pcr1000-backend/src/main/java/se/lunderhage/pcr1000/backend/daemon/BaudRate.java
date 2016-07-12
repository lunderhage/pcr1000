package se.lunderhage.pcr1000.backend.daemon;

/**
 * Baud rates used by the PCR1000.
 */
public enum BaudRate {
    INITIAL(9600), FAST(38400);

    private final int baudRate;

    private BaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getBaudRate() {
        return baudRate;
    }
}
