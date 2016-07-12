package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import gnu.io.NRSerialPort;
import gnu.io.SerialPort;
import se.lunderhage.pcr1000.backend.subscribers.PowerStateSubscriber;
import se.lunderhage.pcr1000.backend.tasks.PowerState;

/**
 * Some utility methods for serial port handling.
 */
public class SerialPortUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SerialPortUtils.class);

    private SerialPortUtils() {}

    public static NRSerialPort openSerialPort(String portName, int baudrate) {
        NRSerialPort serialPort = new NRSerialPort(portName, baudrate);

        LOG.debug("Opening serial port.");
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


    public static boolean testPort(String portName, int baudRate) {
        // TODO: Find PCR1000 on specified port.
        NRSerialPort serialPort = null;
        try {
            serialPort = openSerialPort(portName, baudRate);
            EventBus eventBus = new EventBus();
            InputStream input = serialPort.getInputStream();
            EventListener eventListener = new EventListener(eventBus, input);
            serialPort.addEventListener(eventListener);
            PowerStateSubscriber powerState = new PowerStateSubscriber();
            eventBus.register(powerState);
            OutputStream output = serialPort.getOutputStream();

            output.write(new PowerState().getCommand().getBytes());
            output.write("\r\n".getBytes());
            output.flush();
            if (powerState.isTurnedOn() != null) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } finally {
            serialPort.removeEventListener();
            if (serialPort != null) {
                serialPort.disconnect();
            }
        }

        return false;
    }

}
