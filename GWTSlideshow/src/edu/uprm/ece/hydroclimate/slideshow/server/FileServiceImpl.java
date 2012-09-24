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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uprm.ece.hydroclimate.slideshow.client.FileService;
import edu.uprm.ece.hydroclimate.slideshow.client.ImageDescription;

public class FileServiceImpl extends RemoteServiceServlet implements
		FileService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5022633506486789297L;
	private static final String GET_IMAGES = "SELECT name, url FROM files WHERE variableName = ? and dateCreated between ? and ?";
	private static final String GET_VARIABLES = "SELECT variableName from variables";
	private final Properties props = new Properties();
	private static final List<String> variables = new ArrayList<String>(25);

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		props.put("host", config.getInitParameter("host"));
		props.put("user", config.getInitParameter("user"));
		props.put("password", config.getInitParameter("password"));
		loadVariableNames();

	}

	private Connection getConnection() throws SQLException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}
		return DriverManager.getConnection(props.getProperty("host"), props);
	}

	@Override
	public List<ImageDescription> getImages(Date from, Date to,
			String variableName) {
		if( !variables.contains(variableName))
			return null;

		List<ImageDescription> images = new ArrayList<ImageDescription>();
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(GET_IMAGES);
			int i =0;

			ps.setString(++i, variableName);
			ps.setDate(++i, new java.sql.Date(from.getTime()));
			ps.setDate(++i, new java.sql.Date(to.getTime()));
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				images.add(new ImageDescription(rs.getString("url"), rs.getString("name")));
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}
//		System.out.println(images);
		return images;

	}

	@Override
	public Collection<String> getVariables() {
		return variables;
	}

	private void loadVariableNames() {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(GET_VARIABLES);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				variables.add(rs.getString("variableName"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

}
