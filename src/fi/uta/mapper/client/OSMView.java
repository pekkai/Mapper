package fi.uta.mapper.client;

import java.util.HashMap;
import java.util.Iterator;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import fi.uta.mapper.client.mvc.AbstractView;

public class OSMView extends AbstractView {

	private HashMap<String, BusMarker> markerMap;
	private static ToggleButton tracingButton;
	private static ToggleButton gmButton;
	private static ToggleButton osmButton;
	private static ToggleButton realTimeButton;
	private static ListBox lineBox;
	
	public static Map map;
	public static Vector markerLayer;
	public static MapWidget mapWidget;
	
	public OSMView( String name ) {
		super( name );
		this.markerMap = new HashMap<String, BusMarker>();
	}
	
	private void initView() {
		System.out.println("init");
		MapOptions op = new MapOptions();

		mapWidget = new MapWidget("100%", "100%", op);
        map = mapWidget.getMap();
        
        OSM layer = OSM.Mapnik("test");
       
        layer.setIsBaseLayer( true );
        layer.setIsVisible( true );
        
        map.addLayer( layer );

        LonLat lonLat = new LonLat(Model.getLongtitude(),Model.getLatitude());
        lonLat.transform("EPSG:4326", map.getProjection() );   
        
        map.setCenter(lonLat, Model.getZoomLevel() );                             

        RootPanel.get("map_canvas").clear();
        RootPanel.get("map_canvas").add( mapWidget );

        markerLayer = new Vector("Marker Layer");
        map.addLayer(markerLayer);
        
        mapWidget.setStylePrimaryName("osmMapPanel" );
        OSMView.initControls();
	}
	
	public void update( int code ) {

		if( !this.isActive() && code != Constants.OP_LOADOSMMAPS )
			return;
					
		switch( code ) {
		case Constants.OP_REALTIMEDATA:
				this.processRealTimeData();
			break;
		case Constants.OP_LOADOSMMAPS:
			this.initView();
			this.activate();			
			break;
		case Constants.OP_LOADGOOGLEMAPS:
			this.deactive();
			break;
		case Constants.OP_TRACING_ENABLED:
			this.startTracing();
			break;
		case Constants.OP_TRACING_DISABLED:
			this.stopTracing();
			OSMView.tracingButton.setDown( false );
			break;
		case Constants.OP_LINEREFCHANGE:
			break;
		}		
		
	}
	
	private void startTracing() {
		@SuppressWarnings("rawtypes")
		Iterator iterator = this.markerMap.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<String, BusMarker> pairs = (java.util.Map.Entry<String, BusMarker>)iterator.next();
			((BusMarker)pairs.getValue()).startTracing();
		}
		
	}
	
	private void stopTracing() {
		@SuppressWarnings("rawtypes")
		Iterator iterator = this.markerMap.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			java.util.Map.Entry<String, BusMarker> pairs = (java.util.Map.Entry<String, BusMarker>)iterator.next();
			((BusMarker)pairs.getValue()).stopTracing();
		}
		
	}


	private void processRealTimeData() {
		OSMView.map.removeLayer( OSMView.markerLayer );
		OSMView.markerLayer = new Vector("Marker Layer");
		
		for( int i = 0; i < Model.busList.size(); i++ ) {
			Bus bus = Model.busList.get( i );
			LonLat coords = new LonLat( bus.getLongitude(),bus.getLatitude() );
			coords.transform("EPSG:4326", map.getProjection() );   
			Point pcoords = new Point( coords.lon(), coords.lat() );
			
			if( this.markerMap.containsKey( bus.getName() ) ) {
				this.markerMap.get( bus.getName() ).OSMMarker = pcoords;
				
			}
			else {	
			    this.markerMap.put(bus.getName(), new BusMarker( pcoords ) ); 			    
			}
			
			OSMView.markerLayer.addFeature( new VectorFeature( this.markerMap.get( bus.getName()).OSMMarker ) );

			if( Model.isTracing() ) {
				this.markerMap.get( bus.getName() ).addTracingPointOSM( pcoords );
				
				
				LineString geometry = new LineString( this.markerMap.get( bus.getName() ).getTracingPoints().toArray(new Point[this.markerMap.get( bus.getName() ).getTracingPoints().size()] ) );
				Style style = new Style();
				style.setStrokeColor( "#0033ff" );
				style.setStrokeWidth( 2 );
				OSMView.markerLayer.addFeature( new VectorFeature( geometry, style ) );
			}
		}
		
		OSMView.map.addLayer( OSMView.markerLayer );
		
			
	}

public static void initControls() {
		
		final VerticalPanel controls = new VerticalPanel();
		controls.setStylePrimaryName( "controlsPanelOSM" );
		
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
		gmButton.setDown( false );
		gmButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {
				if( ((ToggleButton)event.getSource()).isDown() ) {
					RootPanel.get("map_canvas").remove( OSMView.mapWidget );
					Model.stopTracing();
					Model.activateGM();
				}
				else if( !((ToggleButton)event.getSource()).isDown() ) {
					//Model.stopTracing();
				}
			}
		});

		controls.add( gmButton );

		
		// Add ToggleButton for chaingin to open streetmaps layout to controls. 
		osmButton = new ToggleButton( "OS Maps", "OS Maps" );
		osmButton.addClickHandler( new ClickHandler() {
			public void onClick( ClickEvent event ) {}
		});
		
		osmButton.setEnabled( false );
		
		controls.add( osmButton );
		
		OSMView.gmButton.setDown( false );
		OSMView.osmButton.setDown( true );
		
		// Add to GWT side and to google maps 
		OSMView.mainPanel.add( controls );
		RootPanel.get("map_canvas").add( controls );
	}	
	
	
}
