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
package edu.uprm.ece.hydroclimate.slideshow.client;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTSlideshow implements EntryPoint, ClickHandler {
	
	private VerticalPanel mainPanel;
	private FlexTable inputTable;
	private DateBox fromDateBox, toDateBox;
	private Button generateButton;
	private ListBox variableListBox;
	private DockLayoutPanel slidePanel;
	private Label slideTitleLabel;
	private Image baseImage;
	private Button play, next, previous;
	private Label imageDescriptionLabel;
	private FileServiceAsync fileSvc = FileService.Util.getInstance();
	private SlideshowManager slideMan;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void onModuleLoad() {
		
		RootLayoutPanel rp = RootLayoutPanel.get();
		rp.setSize("100%", "");
		
		
		
		mainPanel = new VerticalPanel();
		rp.add(mainPanel);
		rp.setWidgetTopHeight(mainPanel, 0.0, Unit.PX, 543.0, Unit.PX);
		mainPanel.setWidth("100%");
		rp.setWidgetLeftRight(mainPanel, 0.0, Unit.PCT, 0.0, Unit.PX);
		
		
		//Setup input area
		inputTable = new FlexTable();
		
		Label fromLabel, toLabel, variableNameLabel;
		fromLabel = new Label("From:");
		toLabel = new Label("To:");
		variableNameLabel = new Label("Variable:");
		fromDateBox = new DateBox();
		fromDateBox.setFormat(new DefaultFormat(DateTimeFormat.getShortDateFormat()));
		toDateBox = new DateBox();
		toDateBox.setFormat(new DefaultFormat(DateTimeFormat.getShortDateFormat()));
		fromDateBox.getDatePicker().setValue(new Date());
		toDateBox.getDatePicker().setValue(new Date());
		
		inputTable.setWidget(0, 0, fromLabel);
		inputTable.setWidget(0, 1, toLabel);
		inputTable.setWidget(1,0,fromDateBox);
		inputTable.setWidget(1,1,toDateBox);
		
		generateButton = new Button("Generate");
		generateButton.setEnabled(false);
		variableListBox = new ListBox();
		generateButton.addClickHandler(this);
		inputTable.setWidget(0, 2, variableNameLabel);
		inputTable.setWidget(0,3,variableListBox);
		inputTable.setWidget(1,2,generateButton);
		generateButton.setWidth("100%");
		mainPanel.add(inputTable);
		inputTable.getFlexCellFormatter().setColSpan(1, 2, 2);
		
		slidePanel = new DockLayoutPanel(Unit.PCT);
		slidePanel.setStyleName("slidePanel");
		mainPanel.add(slidePanel);
		slidePanel.setSize("692px", "443px");
		
		
		//TODO: setup slideshow area
		slideTitleLabel = new Label("Variable Name");
		slideTitleLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		slidePanel.addNorth(slideTitleLabel,5.0);
		slideTitleLabel.setWidth("100%");
		
		
		
		
		imageDescriptionLabel = new Label("Description");
		imageDescriptionLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		slidePanel.addSouth(imageDescriptionLabel, 5.0);
		imageDescriptionLabel.setWidth("100%");
		
		baseImage = new Image();
		baseImage.setVisible(true);
		slidePanel.add(baseImage);
		baseImage.setSize("500px", "500px");
		
	
		
		//Fill the variableListBox
		AsyncCallback<Collection<String>> callback = new AsyncCallback<Collection<String>>() {
			
			@Override
			public void onSuccess(Collection<String> result) {
				for(String variable: result)
					variableListBox.addItem(variable);
				generateButton.setEnabled(true);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cant load variables"+caught.getMessage());
				
			}
		};
		
		fileSvc.getVariables(callback);
		
		
		

		
		
		
	}
	
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO: Get urls for slideshow
		
		Date from, to;
		from = fromDateBox.getValue();
		to = toDateBox.getValue();
		
		if(from == null || to == null)
		{
			Window.alert("Date must be valid");
			return;
		}
		Date today = new Date();
		if(from.after(to) || from.after(today) || to.after(today))
		{
			Window.alert("from is after to\nOr One of the dates is after today's date" );
			return;
		}
		generateButton.setEnabled(false);
		AsyncCallback<List<ImageDescription>> callback = new AsyncCallback<List<ImageDescription>>() {
			
			@Override
			public void onSuccess(List<ImageDescription> images) {
				if(images == null || images.size() == 0){
					Window.alert("result nuulll");
					return;
				}
				
				if(slideMan != null)
					slideMan.stop();
				
				slideMan = new SlideshowManager(baseImage, images,imageDescriptionLabel, 1);
				
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error:"+caught.getMessage());
				
			}
		};
		
		String variable = variableListBox.getItemText(variableListBox.getSelectedIndex());
		
		fileSvc.getImages(from,to,variable,callback);
		
	}
}
