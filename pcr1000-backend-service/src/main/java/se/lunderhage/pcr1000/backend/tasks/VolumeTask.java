package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;
import se.lunderhage.pcr1000.backend.model.commands.Volume;

/**
 * Command to set the volume on the PCR1000.
 */
public class VolumeTask extends Task {

	private static final String VOLUME_CMD = "J40%s\n";

	private final Volume volume;

	public VolumeTask(Volume volume) {
		Preconditions.checkArgument(volume.getLevel() >= 0 && volume.getLevel() <= 255, "Volume musst be between 0 and 255");
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}

	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {
		try {
			commandOutput.execCommand(getCommand());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
    public String getCommand() {
		return String.format(VOLUME_CMD, Integer.toHexString(volume.getLevel()).toUpperCase());
	}

}
