package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import main.ScreenRecorder.Quality;

public class ScreenRecorder implements Runnable {
	
	private final double frameRate;
	private final Dimension screenBounds;
	private final Robot robot;
	private final IMediaWriter writer;
	private final ScheduledExecutorService pool;
	private long startTime;
	private int frameCount = 0;
	
	/**
	 * Calls {@link #ScreenRecorder(String, double, Quality)} with a frame rate of 5 and {@link Quality#MEDIUM}
	 * @param outputFile
	 * @throws AWTException
	 */
	public ScreenRecorder(String outputFile) throws AWTException {
		this(outputFile, 5);
	}
	
	/**
	 * Creates a ScreenRecorder for capturing the entire desktop of an agent's computer.
	 * 
	 * @param outputFile Where to write the mp4 video file.
	 * @param frameRate How many captures per second need to be included in the video.
	 * @param quality Determines the video resolution quality ({@link Quality#LOW}, {@link Quality#MEDIUM}, {@link Quality#HIGH}).
	 * @throws AWTException If a screen capture Robot cannot be created.
	 */
	public ScreenRecorder(String outputFile, double frameRate) throws AWTException {
		this.frameRate = frameRate;
		
		robot = new Robot();
		
		// let's make a IMediaWriter to write the file.
		writer = ToolFactory.makeWriter(outputFile);

		screenBounds = calculateScreenBounds();

		// We tell it we're going to add one video stream, with id 0, at position 0, mp4 codec, width, height.
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width / Quality.HIGH.getDivisor(), screenBounds.height / Quality.HIGH.getDivisor());

		pool = Executors.newScheduledThreadPool(1);
	}
	
	public void startCapture() {
		System.out.println(" Starting screen capture.");
		startTime = System.nanoTime();
		pool.scheduleAtFixedRate(this, 0L, (long) (1000.0 / frameRate), TimeUnit.MILLISECONDS);
	}
	
	public void stopCapture() {
		try {
			pool.shutdown();
			pool.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		System.out.println(" Screen capture has ended. " + frameCount + " images recorded in video.");
		
		// tell the writer to close and write the trailer if needed
		writer.close();
	}

	@Override
	public void run() {
		// take the screen shot
		BufferedImage screen = robot.createScreenCapture(new Rectangle(screenBounds));

		// convert to the right image type
		BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
		
		// encode the image to stream #0
		writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
		
		frameCount++;
	}

	public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

		BufferedImage image;

		// if the source image is already the target type, return the source image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new image
		else {
			image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}

		return image;

	}
	
	private static Dimension calculateScreenBounds() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();

		Dimension allScreenBounds = new Dimension();
		for (GraphicsDevice screen : screens) {
			Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();

			allScreenBounds.width += screenBounds.width;
			allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
		}
		return allScreenBounds;
	}
	
	public enum Quality {
		/**
		 * Original captured screen resolution.
		 */
		HIGH(1), 
		
		/**
		 * One-half of the original captured screen resolution.
		 */
		MEDIUM(2), 
		
		/**
		 * One-fourth of the original captured screen resolution.
		 */
		LOW(4);
		
		private final int divisor;
		
		private Quality(int divisor) {
			this.divisor = divisor;
		}
		
		int getDivisor() {
			return divisor;
		}
	}

}