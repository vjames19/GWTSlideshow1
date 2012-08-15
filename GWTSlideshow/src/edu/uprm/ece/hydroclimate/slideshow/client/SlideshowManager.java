package edu.uprm.ece.hydroclimate.slideshow.client;

import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class SlideshowManager {
	
	private final Image image;
	private Timer timer;
	private int delay;
	private final static int MILLIS = 1000; 
	private List<ImageDescription> images;
	private final Label description;
	private Integer index = 0;
	private boolean running = false;
	public SlideshowManager(final Image image, final List<ImageDescription> images,final Label description, int delay)
	{
		this.image = image;
		this.images = images;
		this.delay = delay;
		this.description = description;
		
		timer = new Timer() {
			
			@Override
			public void run() {
				
				index = index+1 <images.size() ? index+1: 0;
				image.setUrl(images.get(index).getUrl());
				description.setText(images.get(index).getUrl());
				
			}
		};
		
		
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
		commonSlideInfo(index);
	}
	
	public void displayPreviousSlide()
	{
		index = index-1 <0? images.size() -1: index-1;
		commonSlideInfo(index);
	}
	
	private void commonSlideInfo(int index)
	{
		image.setUrl(images.get(index).getUrl());
		description.setText(images.get(index).getUrl());
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	
	
	
	
	

}
