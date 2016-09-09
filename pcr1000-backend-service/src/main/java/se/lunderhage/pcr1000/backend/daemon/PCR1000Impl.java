package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lunderhage.pcr1000.backend.model.PCR1000;
import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.Filter;
import se.lunderhage.pcr1000.backend.model.types.Mode;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;
import se.lunderhage.pcr1000.backend.subscribers.PowerStateSubscriber;
import se.lunderhage.pcr1000.backend.subscribers.PrintSubscriber;
import se.lunderhage.pcr1000.backend.tasks.CheckPowerStateTask;
import se.lunderhage.pcr1000.backend.tasks.SquelchTask;
import se.lunderhage.pcr1000.backend.tasks.Task;
import se.lunderhage.pcr1000.backend.tasks.TuneTask;
import se.lunderhage.pcr1000.backend.tasks.VolumeTask;

/**
 * This class is the daemon that communicates with the PCR1000
 */
@Component(immediate = false, service = PCR1000.class, property = { "service.ranking:Integer=2" })
public class PCR1000Impl implements PCR1000 {

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000Impl.class);

	private String portName;
	private PCR1000CommandQueue commandQueue = null;

	public PCR1000Impl() {
	    this.portName = null;
	}

	public PCR1000Impl(String portName) {
		this.portName = portName;
	}

	/**
	 * Turn on and init the PCR1000.
	 */
	@Activate
	@Override
    public synchronized void start() {
	    if (commandQueue != null && !commandQueue.isShutdown()) {
	        return;
	    }

	    LOG.debug("Starting PCR1000...");
	    if (portName == null) {
	        commandQueue = PCR1000CommandQueue.find();
	    } else {
	        commandQueue = PCR1000CommandQueue.create(portName);
	    }
	    LOG.debug("PCR1000 is started.");
	}

	/**
	 * Turn off PCR1000.
	 */
	@Deactivate
	@Override
    public synchronized void stop() {
		if (commandQueue != null && !commandQueue.isShutdown()) {
		    LOG.debug("Stopping PCR1000...");
			commandQueue.shutdown();
			LOG.debug("PCR1000 is stopped.");
		}
	}

	/**
	 * Register subscriber on the event bus.
	 */
	@Override
    public void register(Object subscriber) {
		commandQueue.register(subscriber);
	}

    public Future<?> submitCommand(Task command) {
        if (commandQueue == null) {
            throw new IllegalArgumentException("Radio is turned off.");
        }
		return commandQueue.submitCommand(command);
	}

    @Override
    public boolean setVolume(Volume volume) {
        submitCommand(new VolumeTask(volume));
        return true;
    }

    @Override
    public boolean setSquelch(Squelch squelch) {
        submitCommand(new SquelchTask(squelch));
        return true;
    }

    @Override
    public boolean tune(RadioChannel channel) {
        submitCommand(new TuneTask(channel));
        return true;
    }

    @Override
    public PowerState getPowerState() {
        if (commandQueue == null) {
            LOG.debug("We don't have a command queue. That is bad!");
            return new PowerState(false);
        }
        LOG.debug("We have a command queue. That is good.");
        PowerStateSubscriber subscriber = new PowerStateSubscriber();
        commandQueue.register(subscriber);
        LOG.debug("Submitting task...");
        submitCommand(new CheckPowerStateTask());
        LOG.debug("Got response: {}", subscriber.isTurnedOn());
        return new PowerState(subscriber.isTurnedOn());
    }

	public static void main(String[] args) throws InterruptedException, IOException, CommandException, TimeoutException {
		PCR1000Impl pcr1000 = new PCR1000Impl();
		pcr1000.start();

		pcr1000.register(new PrintSubscriber());

		pcr1000.setVolume(new Volume(80));
		pcr1000.setSquelch(new Squelch(147));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.WFM, Filter._230K, 107100000)));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.AM, Filter._15K,   122450000)));
		pcr1000.tune(new RadioChannel(Mode.AM, Filter._15K,   118100000));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.AM, Filter._15K,   126650000)));

		LOG.debug("Listening to radio... (press enter to quit)");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
		LOG.debug("Shutting down...");
		pcr1000.stop();

	}

}
