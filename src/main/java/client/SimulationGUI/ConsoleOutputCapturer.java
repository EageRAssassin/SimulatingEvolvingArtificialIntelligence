package client.SimulationGUI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * A class created to collect the error messages sent through the console
 */
public class ConsoleOutputCapturer {
	private ByteArrayOutputStream baos;
	private PrintStream previous;
	private boolean capturing;

	/**
	 * Start to capture the console messages
	 */
	public void start() {
		if (capturing) {
			return;
		}

		capturing = true;
		previous = System.out;
		baos = new ByteArrayOutputStream();

		OutputStream outputStreamCombiner = new OutputStreamCombiner(Arrays.asList(previous, baos));
		PrintStream custom = new PrintStream(outputStreamCombiner);

		System.setOut(custom);
	}

	/**
	 * Stop to capture the console messages
	 * 
	 * @return the messages sent in console since the last start() function
	 */
	public String stop() {
		if (!capturing) {
			return "";
		}

		System.setOut(previous);

		String capturedValue = baos.toString();

		baos = null;
		previous = null;
		capturing = false;

		return capturedValue;
	}

	private static class OutputStreamCombiner extends OutputStream {
		private List<OutputStream> outputStreams;

		public OutputStreamCombiner(List<OutputStream> outputStreams) {
			this.outputStreams = outputStreams;
		}

		public void write(int b) throws IOException {
			for (OutputStream os : outputStreams) {
				os.write(b);
			}
		}

		public void flush() throws IOException {
			for (OutputStream os : outputStreams) {
				os.flush();
			}
		}

		public void close() throws IOException {
			for (OutputStream os : outputStreams) {
				os.close();
			}
		}
	}
}
