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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTSlideshow implements EntryPoint, ClickHandler {

	private VerticalPanel mainPanel;
	private FlexTable inputTable;
	private DateBox fromDateBox, toDateBox;
	private Button generateButton;
	private ListBox variableListBox;
	private Image baseImage;
	private FileServiceAsync fileSvc = FileService.Util.getInstance();
	private SlideshowManager slideMan;
	private HorizontalPanel horizontalPanel;
	private Button playButton;
	private Button nextButton;
	private Button previousButton;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void onModuleLoad() {

		RootLayoutPanel rp = RootLayoutPanel.get();
		rp.setSize("100%", Window.getClientHeight()+"px");

		Label fromLabel, toLabel, variableNameLabel;


		

		mainPanel = new VerticalPanel();
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		rp.add(mainPanel);
		rp.setWidgetTopBottom(mainPanel, 0.0, Unit.PX, 123.0, Unit.PX);
		mainPanel.setWidth("100%");
		rp.setWidgetLeftRight(mainPanel, 0.0, Unit.PCT, 0.0, Unit.PX);


		//Setup input area
		inputTable = new FlexTable();
		fromLabel = new Label("From:");
		toLabel = new Label("To:");
		variableNameLabel = new Label("Variable:");
		fromDateBox = new DateBox();
		fromDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
		toDateBox = new DateBox();
		toDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
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

		//setup image
		baseImage = new Image();
		mainPanel.add(baseImage);
		baseImage.setVisible(true);
		baseImage.setPixelSize(800,500);
		
		
		//south
		horizontalPanel = new HorizontalPanel();
		mainPanel.add(horizontalPanel);
		mainPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		previousButton = new Button("previous");
		previousButton.addClickHandler(this);
		horizontalPanel.add(previousButton);
		horizontalPanel.setCellHorizontalAlignment(previousButton, HasHorizontalAlignment.ALIGN_CENTER);

		playButton = new Button("play");
		playButton.addClickHandler(this);
		horizontalPanel.add(playButton);
		horizontalPanel.setCellHorizontalAlignment(playButton, HasHorizontalAlignment.ALIGN_CENTER);

		nextButton = new Button("next");
		nextButton.addClickHandler(this);
		horizontalPanel.add(nextButton);
		horizontalPanel.setCellHorizontalAlignment(nextButton, HasHorizontalAlignment.ALIGN_CENTER);




		//Fill the variableListBox
		fileSvc.getVariables(new AsyncCallback<Collection<String>>() {

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
		});

		







	}


	@Override
	public void onClick(ClickEvent event) {
		
		Widget eventSource =(Widget) event.getSource();


		if(eventSource== generateButton){
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
				Window.alert("Error:" +
						"--From is after to\n--Or one of the dates is after today's date" );
				return;
			}
			generateButton.setEnabled(false);
			AsyncCallback<List<ImageDescription>> callback = new AsyncCallback<List<ImageDescription>>() {

				@Override
				public void onSuccess(List<ImageDescription> images) {
					if(images == null || images.size() == 0){
						Window.alert("No Images for that range of dates");
						return;
					}

					if(slideMan != null)
						slideMan.stop();

					slideMan = new SlideshowManager(baseImage, images, 1);
					startSlide();


				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error:Something unexpected happened please\n" +
							"try again: "+ caught);

				}
			};

			String variable = variableListBox.getItemText(variableListBox.getSelectedIndex());

			fileSvc.getImages(from,to,variable,callback);
			generateButton.setEnabled(true);

		}
		else if(slideMan != null)
		{
			if(eventSource == playButton)
			{

				if(slideMan.isRunning())
					stopSlide();
				else
					startSlide();
			}
			else if(eventSource == previousButton)
			{
				stopSlide();
				slideMan.displayPreviousSlide();

			}
			else if(eventSource == nextButton)
			{
				stopSlide();
				slideMan.displayNextSlide();
			}
		}

	}

	private void stopSlide()
	{
		if(slideMan.isRunning())
		{
			slideMan.stop();
			playButton.setText("play");
		}
	}

	private void startSlide()
	{
		if(!slideMan.isRunning())
		{
			slideMan.startRepeated();
			playButton.setText("pause");
		}
	}

}





