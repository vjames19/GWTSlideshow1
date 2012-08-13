/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.uprm.ece.hydroclimate.slideshow.server;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uprm.ece.hydroclimate.slideshow.client.FileService;
import edu.uprm.ece.hydroclimate.slideshow.client.ImageDescription;

public class FileServiceImpl extends RemoteServiceServlet implements FileService {

	private Map<String,String> variables = new TreeMap<String,String>();
	private final String IMAGE_DIR = "images";
	private File dir = new File(IMAGE_DIR);
	private FileFilter  photoFilter = new PhotoFilter();
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	public FileServiceImpl()
	{
		loadVariablesNames();
	}
	
	private void loadVariablesNames()
	{
		File[] result = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}
		});
		
		for(File file: result)
			variables.put(file.getName(), file.getPath());
		
		System.out.println("Variables initialized");
			
		
		
		
	}
	
	@Override
	public List<ImageDescription> getImages(Date from, Date to,String variableName) {
		
		if(!variables.containsKey(variableName))
			return null;
		String path = variables.get(variableName);
		System.out.println(path);
		File dir = new File(path);
		File[] photos = dir.listFiles(photoFilter);
		System.out.println(Arrays.toString(photos));
		
		List<ImageDescription> images =checkPaths(from,to,photos); 
		System.out.println(images);
		return images;
		
	}
	
	private List<ImageDescription> checkPaths(Date from, Date to, File[] photos)
	{
		List<ImageDescription> images= new ArrayList<ImageDescription>();
		for(File photo: photos)
		{
			String name = photo.getName();
			System.out.println(name);
			//Split non numeric fields
			String[] fields = name.split("\\D+");
			if(fields.length == 0)
				continue;
			Date photoDate = null;
			try {
				photoDate = dateFormat.parse(fields[fields.length-1].trim());
				System.out.println("photoDate:"+photoDate);
			} catch (ParseException e) {
				// TODO Logger
				e.printStackTrace();
			}
			System.out.println(photoDate);
			if(photoDate != null)
			{
				System.out.println("inside not null");
				System.out.println("From:"+from);
				System.out.println("to:"+to);
				System.out.println("compare from:"+photoDate.compareTo(from));
				System.out.println("compare to:"+photoDate.compareTo(to));
				if(photoDate.compareTo(from)>=0 && photoDate.compareTo(to)<=0)
				{
					System.out.println("Adding images");
					images.add(new ImageDescription(photo.getPath(), photoDate.toString()));
				}
			}
		}
		
		return images;
	}
	


	@Override
	public Collection<String> getVariables() {
		
		List<String> result = new ArrayList<String>(variables.keySet());
		return result;
	}
	
	
	public static class PhotoFilter implements FileFilter{

		public PhotoFilter(){}
		private static final String JPEG = ".jpg";
		@Override
		public boolean accept(File file) {

			return file.isFile() && file.getPath().endsWith(JPEG);
		}
		
	}
}
