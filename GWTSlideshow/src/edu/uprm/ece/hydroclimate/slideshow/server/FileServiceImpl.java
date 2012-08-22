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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uprm.ece.hydroclimate.slideshow.client.FileService;
import edu.uprm.ece.hydroclimate.slideshow.client.ImageDescription;

public class FileServiceImpl extends RemoteServiceServlet implements FileService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5022633506486789297L;
	private Map<String,String> variables = new TreeMap<String,String>();
	private String IMAGE_DIR;
	private File dir;
	private FileFilter  photoFilter = new PhotoFilter();
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private String HOST_URL;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		IMAGE_DIR = config.getInitParameter("imageDir");
		HOST_URL = config.getInitParameter("hostUrl");
		System.out.println(IMAGE_DIR);
		dir = new File(IMAGE_DIR);
		loadVariablesNames();
		
		
	}
	
	
	private List<ImageDescription> checkPaths(Date from, Date to, File[] photos)
	{
		List<ImageDescription> images= new ArrayList<ImageDescription>();
		for(File photo: photos)
		{
			String name = photo.getPath();
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

				if(photoDate.compareTo(from)>=0 && photoDate.compareTo(to)<=0)
				{
					System.out.println("Adding images");
					String path = photo.getPath();
					System.out.println("Real path:"+getServletContext().getRealPath(path));
					//images.add(new ImageDescription("http://136.145.116.40/"+
					images.add(new ImageDescription(HOST_URL+
					path.substring(path.indexOf("GOES-PRWEB_RESULTS")), photoDate.toString()));
				}
			}
		}
		
		return images;
	}
	
	@Override
	public List<ImageDescription> getImages(Date from, Date to,String variableName) {
		
		if(!variables.containsKey(variableName))
			return null;
		String path = variables.get(variableName);
		System.out.println(path);
		File dir = new File(path);
		File[] photos = dir.listFiles(photoFilter);
		
		
		List<ImageDescription> images =checkPaths(from,to,photos); 
		Collections.sort(images);
		return images;
		
	}
	
	@Override
	public Collection<String> getVariables() {
		
		List<String> result = new ArrayList<String>(variables.keySet());
		return result;
	}
	



	
	static class PhotoFilter implements FileFilter{
	
		private static final String JPEG = ".jpg";
		public PhotoFilter(){}
		@Override
		public boolean accept(File file) {
	
			return file.isFile() && file.getPath().endsWith(JPEG);
		}
		
	}





	private void loadVariablesNames()
	{
		//List directories the directories inside that folder will be the variables
		File[] result = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				//accept directories
				return pathname.isDirectory();
			}
		});
		if(result == null)
		{
			System.out.println("Directory null");
			return;
		}
		for(File file: result)
			variables.put(file.getName(), file.getPath());
		
	}
}
