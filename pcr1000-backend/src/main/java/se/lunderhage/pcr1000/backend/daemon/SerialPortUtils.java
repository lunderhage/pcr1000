package se.lunderhage.pcr1000.backend.daemon;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.NRSerialPort;
import gnu.io.SerialPort;

/**
 * Some utility methods for serial port handling.
 */
public class SerialPortUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SerialPortUtils.class);

    private SerialPortUtils() {}

    public static NRSerialPort openSerialPort(String portName, int baudrate) {
        NRSerialPort serialPort = new NRSerialPort(portName, baudrate);

        LOG.debug("Opening serial port {} @ {}bps", portName, baudrate);
        serialPort.connect();
        serialPort.getSerialPortInstance().setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

        // To keep radio turned on...
        serialPort.getSerialPortInstance().setRTS(true);
        serialPort.getSerialPortInstance().setDTR(true);

        return serialPort;
    }

    public static List<String> getSerialPorts() {
        List<String> ports = new ArrayList<>();

        for (String port : NRSerialPort.getAvailableSerialPorts()) {
            ports.add(port);
        }

        return ports;
    }

}
