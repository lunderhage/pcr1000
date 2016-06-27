package se.lunderhage.pcr1000.backend.tasks;

import java.io.OutputStream;

import com.google.common.eventbus.EventBus;

public class FastTransferMode extends Command {

	private final static String CMD_FAST_TRANSFER_MODE_ENABLE = "G301";
	private final static String CMD_FAST_TRANSFER_MODE_DISABLE = "G300";
	
	private boolean enable = false;
	
	public FastTransferMode(boolean enable) {
		this.enable = enable;
	}

	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public String getCommand() {
		if (enable) {
			return CMD_FAST_TRANSFER_MODE_ENABLE;
		}
		return CMD_FAST_TRANSFER_MODE_DISABLE;
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		// TODO Auto-generated method stub

	}

}
