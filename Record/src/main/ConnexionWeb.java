package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;


public class ConnexionWeb {
	
	public static void Setup(String myurl) throws AWTException, InterruptedException {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
		WebDriver chrome = new ChromeDriver();
		chrome.get(myurl);
		chrome.manage().window().maximize();
		/*WebElement signin = chrome.findElement(By.xpath("//*[@ng-click='signin();']"));
		signin.click();
		WebElement name = chrome.findElement(By.id("loginUsername"));
		name.sendKeys("ilovebounty");
		WebElement pass = chrome.findElement(By.id("loginPassword"));
		pass.sendKeys("pag1494");
		WebElement submit = chrome.findElement(By.xpath("//*[@ng-disabled='loginForm.$invalid || submitting == true']"));
		submit.click();*/
		
		
		
		Date date = new Date();
		double frameRate = 30.0;
		int howManyMinToRun = 5;
		WebElement fullscreen = chrome.findElement(By.xpath("//*[@title='Fullscreen']"));
		fullscreen.click();
		ScreenRecorder recorder = new ScreenRecorder("video_"+date.getTime()+".mp4", frameRate);
		JavaSoundRecorder audio_recorder = new JavaSoundRecorder();
		
		Thread audio = new Thread(new Runnable() {
			
			@Override
			public void run() {
				audio_recorder.start();
				
			}
		});
		recorder.startCapture();
		audio.start();
		
		Boolean stop = true;
		
		while (stop)
		{
			if(chrome.getPageSource().contains("I'm currently offline"))
			{
				System.out.println("je suis deco");
				stop = false;
				recorder.stopCapture();
				audio_recorder.finish();
			}
			else
			{
				
				System.out.println("je suis connecter");
				stop = true;
			}
			
			Thread.sleep(howManyMinToRun * 1000 * 60);
		}
		
		
	    
	}

}
