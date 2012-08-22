package edu.uprm.ece.hydroclimate.slideshow.client;

import java.io.Serializable;

public class ImageDescription implements Serializable, Comparable<ImageDescription> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3513373212406281754L;
	private String url;
	private String description;
	
	public ImageDescription(){};
	public ImageDescription(String url, String description) {
		super();
		this.url = url;
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "ImageDescription [url=" + url + ", description=" + description
				+ "]";
	}
	@Override
	public int compareTo(ImageDescription o) {
		// TODO Auto-generated method stub
		return this.url.compareTo(o.url);
	}
	
	
	
	
}
