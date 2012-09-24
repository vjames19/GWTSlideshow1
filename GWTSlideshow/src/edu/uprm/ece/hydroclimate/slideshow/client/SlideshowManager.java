package edu.uprm.ece.hydroclimate.slideshow.client;

import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

public class SlideshowManager {
	
	private final Image image;
	private Timer timer;
	private int delay;
	private final static int MILLIS = 1000; 
	private List<ImageDescription> images;
	
	private Integer index = 0;
	private boolean running = false;
	public SlideshowManager(final Image image, final List<ImageDescription> images, int delay)
	{
//		System.out.println(images);
		this.image = image;
		this.images = images;
		this.delay = delay;
		image.setUrl(images.get(0).getUrl());
		
		timer = new Timer() {
			
			@Override
			public void run() {
				displayNextSlide();
				
			}
		};
		
		
	}
	
	public void setDelay(int delayInSeconds)
	{
		delay = delayInSeconds;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public void startRepeated()
	{
		if(isRunning())
			return;
		timer.scheduleRepeating(delay * MILLIS);
		running = true;
	}
	
	public void stop()
	{
		if(!isRunning())
			return;
		timer.cancel();
		running = false;
	}
	
	public void displayNextSlide()
	{
		index = index+1 <images.size() ? index+1: 0;
		setSlideInfo(index);
	}
	
	public void displayPreviousSlide()
	{
		index = index-1 <0? images.size() -1: index-1;
		setSlideInfo(index);
	}
	
	private void setSlideInfo(int index)
	{
		image.setUrl(images.get(index).getUrl());
		
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	
	
	
	
	

}
