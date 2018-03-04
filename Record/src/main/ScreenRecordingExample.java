package main;

import java.awt.AWTException;


public class ScreenRecordingExample {

	public static void main(String args[]) throws AWTException, InterruptedException {
		
		double frameRate = 30.0;
		int howManySecondsToRun = 60;
		String outputFilename = "output.mp4";

		
		ScreenRecorder recorder = new ScreenRecorder(outputFilename, frameRate);
		recorder.startCapture();

		Thread.sleep(howManySecondsToRun * 1000);

		recorder.stopCapture();

	}

}
