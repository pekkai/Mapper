package fi.uta.mapper.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.ControlPosition;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;

import fi.uta.mapper.client.mvc.AbstractView;

public class GMView extends AbstractView {

	public static GoogleMap map;
	private HashMap<String, BusMarker> markerMap;
	private boolean deleteAllMarkers;
	private static ToggleButton tracingButton;
	private static ToggleButton gmButton;
	private static ToggleButton osmButton;
	private static ToggleButton realTimeButton;
	private static ListBox lineBox;

	
	public GMView( String name ) {
		super( name );
		this.deleteAllMarkers = false;
	}
	
	public void initView() {

		System.out.println("load");

		this.markerMap = new HashMap<String, BusMarker>();

		AjaxLoaderOptions options = AjaxLoaderOptions.newInstance();
		options.setOtherParms("sensor=true&language=en");
		Runnable callback = new Runnable() {
			public void run() {
				renderMap();
			}
		};
		AjaxLoader.loadApi("maps", "3", callback, options);	

	}
	

	public static void renderMap() {
		LatLng myLatLng = LatLng.create(Model.getLatitude(),Model.getLongtitude());
		MapOptions myOptions = MapOptions.create();
		myOptions.setZoom( Model.getZoomLevel() );
		myOptions.setCenter(myLatLng);
		myOptions.setMapTypeId(MapTypeId.ROADMAP);
		GMView.map = GoogleMap.create(Document.get().getElementById("Google Maps"), myOptions);

		//KmlLayer ctaLayer = KmlLayer.create("http://www.sis.uta.fi/~a231769/tampere.kml");
	    //ctaLayer.setMap(map);
		
		GMView.initControls();
	}

	public static void initControls() {
		
		VerticalPanel controls = new VerticalPanel();
		controls.setStylePrimaryName( "controlsPanel" );
		
		Label lineLabel = new Label("Line");
		lineLabel.setStylePrimaryName( "controlsLabel" );
		controls.add( lineLabel );

		// Set up real-time update speed
		lineBox = new ListBox();
		
		lineBox.addItem( "All" );
		lineBox.addItem( "1" );
		lineBox.addItem( "2" );		
		lineBox.addItem( "Y2" );		
		lineBox.addItem( "3" );		
		lineBox.addItem( "5" );		
		lineBox.addItem( "6" );		
		lineBox.addItem( "7" );		
		lineBox.addItem( "10" );		
		lineBox.addItem( "11" );		
		lineBox.addItem( "12" );		
		lineBox.addItem( "13" );		
		lineBox.addItem( "15" );		
		lineBox.addItem( "16" );		
		lineBox.addItem( "17" );		
		lineBox.addItem( "Y17" );		
		lineBox.addItem( "18" );		
		lineBox.addItem( "20" );		
		lineBox.addItem( "21" );		
		lineBox.addItem( "Y21" );		
		lineBox.addItem( "22" );		
		lineBox.addItem( "23" );		
		lineBox.addItem( "Y23" );		
		lineBox.addItem( "24" );		
		lineBox.addItem( "25" );		
		lineBox.addItem( "26" );		
		lineBox.addItem( "27" );		
		lineBox.addItem( "28" );		
		lineBox.addItem( "Y28" );		
		lineBox.addItem( "29" );		
		lineBox.addItem( "30" );		
		lineBox.addItem( "31" );		
		lineBox.addItem( "33" );		
		lineBox.addItem( "Y33" );		
		lineBox.addItem( "Y35" );		
		lineBox.addItem( "36" );		
		lineBox.addItem( "37" );		
		lineBox.addItem( "38" );		
		lineBox.addItem( "39" );		
		lineBox.addItem( "90" );		
		lineBox.addItem( "91" );		
		lineBox.addItem( "92A" );		
		lineBox.addItem( "92B" );		

		// Get current lineref and set it to be shown on default in ListBox
		for( int i = 0; i < lineBox.getItemCount(); i++ )
			if( lineBox.getItemText( i ).equals( Model.getLine() ) )
				lineBox.setItemSelected( i, true );
		
		// Add ClickListener to change update speed in model
		lineBox.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				System.out.println("button painettu"); 
				int idx = ((ListBox)event.getSource()).getSelectedIndex();
				GMView.model.setLine( ((ListBox)event.getSource()).getItemText( idx ) );
				Model.stopTracing();
				
			}} );
		
		controls.add( lineBox );	
		
		Label speedLabel = new Label("Update");
		speedLabel.setStylePrimaryName( "controlsLabel" );
		controls.add( speedLabel );
		
		
		// Set up real-time update speed
		ListBox lb = new ListBox();
		
		lb.addItem( "Slow" );
		lb.addItem( "Normal" );
		lb.addItem( "Fast" );
		
		// Get current update speed and set it to be shown on default in ListBox
		for( int i = 0; i < 3; i++ )
			if( lb.getItemText( i ).equals( Model.getUpdateRate() ) )
				lb.setItemSelected( i, true );
		
		// Add ClickListener to change update speed in model
		lb.addClickHandler( new ClickHandler(){

			public void onClick(ClickEvent event) {
				System.out.println("button painettu"); 
				int idx = ((ListBox)event.getSource()).getSelectedIndex();
				GMView.model.setUpdateRate( ((ListBox)event.getSource()).getItemText( idx ) );
				
			}} );
		
		controls.add( lb );
		
		Label realtimeLabel = new Label("Realtime");
		realtimeLabel.setStylePrimaryName( "controlsLabel" );
		controls.add( realtimeLabel );
		
		// Add ToggleButton for real-time data to controls. 
		realTimeButton = new ToggleButton( "Disabled", "Enabled" );
		realTimeButton.setDown( Model.isRealtimeTrace() );
		realTimeButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {
				if( ((ToggleButton)event.getSource()).isDown() ) {
					Model.startRealtimeTrace();
				}
				else if( !((ToggleButton)event.getSource()).isDown() ) {
					Model.stopRealtimeTrace();
					Model.stopTracing();
				}
			}
		});
		
		controls.add( realTimeButton );

		Label tracingLabel = new Label("Tracing");
		tracingLabel.setStylePrimaryName( "controlsLabel" );
		controls.add( tracingLabel );
				
		// Add ToggleButton for tracing to controls. 
		tracingButton = new ToggleButton( "Disabled", "Enabled" );
		tracingButton.setDown( Model.isTracing() );
		tracingButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {
				if( ((ToggleButton)event.getSource()).isDown() ) {
					Model.startTracing();
				}
				else if( !((ToggleButton)event.getSource()).isDown() ) {
					Model.stopTracing();
				}
			}
		});
		
		controls.add( tracingButton );

		
		HTML spacing = new HTML("<br>");
		controls.add( spacing );

		Label mapLabel = new Label("Map data");
		mapLabel.setStylePrimaryName( "controlsLabel" );
		controls.add( mapLabel );
	
		
		// Add ToggleButton for chaingin to googlemaps layout to controls. 
		gmButton = new ToggleButton( "Google", "Google" );
		gmButton.setDown( Model.isRealtimeTrace() );
		gmButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {}
		});
		
		gmButton.setEnabled( false );

		controls.add( gmButton );

		
		// Add ToggleButton for chaingin to open streetmaps layout to controls. 
		osmButton = new ToggleButton( "OS Maps", "OS Maps" );
		osmButton.setDown( Model.isRealtimeTrace() );
		osmButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {
				if( ((ToggleButton)event.getSource()).isDown() ) {
					Model.stopTracing();
					Model.activateOSM();
					
				}
				else if( !((ToggleButton)event.getSource()).isDown() ) {
					//Model.stopTracing();
				}
			}
		});

		controls.add( osmButton );
		
		GMView.gmButton.setDown( true );
		GMView.osmButton.setDown( false );

		
		// Add to GWT side and to google maps 
		GMView.mainPanel.add( controls );
		
		GMView.map.getControls().get(
				new Double(ControlPosition.RIGHT_TOP.getValue()).intValue()).push(
				GMView.mainPanel.getElement());
	
	}
	
	public void update( int code ) {
		if( !this.isActive() && code != Constants.OP_LOADGOOGLEMAPS )
			return;
					
		switch( code ) {
		case Constants.OP_REALTIMEDATA:
			if( this.deleteAllMarkers ) {

				GoogleMap tempMap = null;
				@SuppressWarnings("rawtypes")
				Iterator iterator = this.markerMap.entrySet().iterator();
				
				while( iterator.hasNext() ) {
					@SuppressWarnings("unchecked")
					Map.Entry<String, BusMarker> pairs = (Map.Entry<String, BusMarker>)iterator.next();
					((BusMarker)pairs.getValue()).marker.setMap(tempMap);
				}

				this.markerMap = new HashMap<String,BusMarker>();
				this.deleteAllMarkers = false;
			}
			else
				this.processRealTimeData();
			break;
		case Constants.OP_LOADOSMMAPS:
			GMView.mainPanel.clear();
			this.deactive();
			break;
		case Constants.OP_LOADGOOGLEMAPS:
			RootPanel.get("map_canvas").clear();
			RootPanel.get("map_canvas").add( this );
			RootPanel.get("map_canvas").add( GMView.mainPanel );

			this.activate();
			this.initView();
			break;
		case Constants.OP_TRACING_ENABLED:
			this.startTracing();
			break;
		case Constants.OP_TRACING_DISABLED:
			this.stopTracing();
			GMView.tracingButton.setDown( false );
			break;
		case Constants.OP_LINEREFCHANGE:
			this.deleteAllMarkers = true;
			break;
		}
	}
	
	private void startTracing() {
		@SuppressWarnings("rawtypes")
		Iterator iterator = this.markerMap.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, BusMarker> pairs = (Map.Entry<String, BusMarker>)iterator.next();
			((BusMarker)pairs.getValue()).startTracing();
		}
		
	}
	
	private void stopTracing() {
		@SuppressWarnings("rawtypes")
		Iterator iterator = this.markerMap.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, BusMarker> pairs = (Map.Entry<String, BusMarker>)iterator.next();
			((BusMarker)pairs.getValue()).stopTracing();
		}
		
	}
		
	private void processRealTimeData() {
		System.out.println("process");
		for( int i = 0; i < Model.busList.size(); i++ ) {
			Bus bus = Model.busList.get( i );		
			
			if( this.markerMap.containsKey( bus.getName() ) ) {
				this.markerMap.get( bus.getName() ).marker.setPosition( LatLng.create( bus.getLatitude(), bus.getLongitude() ) );
				
			}
			else {	
				MarkerOptions newMarkerOpts = MarkerOptions.create();
			    newMarkerOpts.setPosition( LatLng.create( bus.getLatitude(), bus.getLongitude() ) );
			    newMarkerOpts.setMap(map);
			    newMarkerOpts.setTitle( bus.getName() );
			    this.markerMap.put(bus.getName(), new BusMarker( Marker.create(newMarkerOpts) ) ); 			    
			}

			if( Model.isTracing() )
				this.markerMap.get( bus.getName() ).addTracingPoint(LatLng.create( bus.getLatitude(), bus.getLongitude() ));
		}
		
	}
	
}
