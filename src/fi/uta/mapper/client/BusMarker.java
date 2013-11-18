package fi.uta.mapper.client;

import java.util.ArrayList;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.Polyline;
import com.google.maps.gwt.client.PolylineOptions;

public class BusMarker {

	public Marker marker;
	public Point OSMMarker;
	private Polyline tracingLine;
	private ArrayList<Point> tracinglineOSM;
	
	public BusMarker() {
		super();
	}
	
	public BusMarker( Marker mark ) {
		this.marker = mark;
	}
	
	public BusMarker( Point p  ) {
		this.OSMMarker = p;
	}

	public void startTracing() {
		PolylineOptions tracingOptions = PolylineOptions.create();
	    tracingOptions.setStrokeColor("#FF0000");
	    tracingOptions.setStrokeOpacity(1.0);
	    tracingOptions.setStrokeWeight(2);

	    this.tracingLine = Polyline.create(tracingOptions);	 
	    this.tracingLine.setMap( GMView.map );
	
	    this.tracinglineOSM = new ArrayList<Point>();
	}
	
	public void addTracingPoint( LatLng coords ) {
		this.tracingLine.getPath().push( coords );
	}
	
	public void addTracingPointOSM( Point p ) {
		this.tracinglineOSM.add( p );
	}
	
	public ArrayList<Point> getTracingPoints() {
		return this.tracinglineOSM;
	}
	
	public void stopTracing() {
		if( this.tracingLine != null ) {
			GoogleMap tempMap = null;
			this.tracingLine.setMap( null );
		}
	}

}
