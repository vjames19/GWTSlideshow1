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
	public SlideshowManager(final Image image, final List<ImageDescription> images,final Label description, int delay)
	{
		this.image = image;
		this.images = images;
		this.delay = delay;
		this.description = description;
		
		timer = new Timer() {
			int index = 0;
			@Override
			public void run() {
				
				index = index+1 <images.size() ? index+1: 0;
				image.setUrl(images.get(index).getUrl());
				description.setText(images.get(index).getUrl());
				
			}
		};
		
		startRepeated();
	}
	
	public void startRepeated()
	{
		timer.scheduleRepeating(delay * MILLIS);
		
	}
	
	public void stop()
	{
		timer.cancel();
	}
	
	
	
	
	

}
