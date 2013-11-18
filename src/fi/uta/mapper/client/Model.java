package fi.uta.mapper.client;

import java.util.ArrayList;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;

import fi.uta.mapper.client.mvc.AbstractModel;

public class Model extends AbstractModel {
	
	public static boolean retrieveData;
	public static boolean tracing;
	public static int updateRate;
	public static Timer timer;
	public static ArrayList<Bus> busList;
	public static String lineRef;
	public static String urlPostfix;
	public static int zoomLevel;
	public static double longtitude;
	public static double latitude;
	
	
	public Model() {
		super();
		
		Model.retrieveData = false;
		Model.updateRate = 5000;
		Model.busList = new ArrayList<Bus>();
		Model.tracing = false;
		Model.lineRef = "All";
		Model.urlPostfix = "";
		Model.zoomLevel = 16;
		
		Model.timer = new Timer() {
			public void run() {
				Model.getData();
			}
		};

		
		GMView gmview = new GMView("Google Maps");
		gmview.setModel( this );
		this.addListener( gmview );
		
		OSMView osmview = new OSMView("OpenStreetMaps");
		osmview.setModel( this );
		

		this.addListener( osmview );		
		
	}
	
	public static void setZoomLevel( int i ) {
		Model.zoomLevel = i;
	}
	
	public static int getZoomLevel() {
		return Model.zoomLevel;
	}
	
	public static boolean isTracing() {
		return Model.tracing;
	}
	
	public static boolean isRealtimeTrace() {
		return Model.retrieveData;
	}
	
	public static void startTracing() {
		System.out.println("Starting tracing");
		Model.tracing = true;
		Model.notifyListeners( Constants.OP_TRACING_ENABLED );
	}
	
	public static void stopTracing() {
		System.out.println("Stopping tracing");
		Model.tracing = false;
		Model.notifyListeners( Constants.OP_TRACING_DISABLED );
	}
	
	public static void stopRealtimeTrace() {
		System.out.println("Stopping realtime trace.");
		
		Model.retrieveData = false;
		Model.timer.cancel();
	}
	
	public static void startRealtimeTrace() {
		System.out.println("Starting realtime trace.");
		
		Model.retrieveData = true;
		
		Model.timer.cancel();
		Model.timer.scheduleRepeating( Model.updateRate );
	}
	
	public static String getLine() {
		return Model.lineRef;
	}
	
	public void setLine( String line ) {

		if( line.equals( Model.lineRef ) )
			return;
		
		Model.lineRef = line;
		
		if( line.equals( "All") )
			Model.urlPostfix = "";
		else
			Model.urlPostfix = "?lineRef="+line;

		System.out.println("Setting line reference to " + Model.urlPostfix );
		
		Model.notifyListeners( Constants.OP_LINEREFCHANGE );
	}
	
	public static void activateOSM() {
		Model.notifyListeners( Constants.OP_LOADOSMMAPS );
		//Model.stopRealtimeTrace();
		//Model.stopTracing();
	}
	
	public static void activateGM() {
		Model.notifyListeners( Constants.OP_LOADGOOGLEMAPS );

	}
	
	public void setUpdateRate( String updateRate ) {
		System.out.println("update: " + updateRate );
		
		if( updateRate.equals( "Slow" ) )
			 Model.updateRate= 5000;
		else if( updateRate.equals( "Normal" ) )
			Model.updateRate = 3000;
		else if( updateRate.equals( "Fast" ) )
			Model.updateRate = 1000;
		
		if( Model.retrieveData ) {
			Model.timer.cancel();
			Model.timer.scheduleRepeating( Model.updateRate );
		}
	}	
	
	public static String getUpdateRate() {
		if( Model.updateRate == 5000 )
			 return "Slow";
		else if( updateRate == 3000 )
			return "Normal";
		else if( updateRate == 1000 )
			return "Fast";		
		
		return "Slow";
	}
		
	public static void processData( Response data ) {
		
		Model.busList.clear();
		
		JSONValue value = JSONParser.parseStrict( data.getText() );
		JSONObject obj = value.isObject();
		
		JSONArray array = obj.get("Siri").isObject().get("ServiceDelivery").isObject().get("VehicleMonitoringDelivery").isArray();
		JSONArray array2 = array.get( 0 ).isObject().get( "VehicleActivity").isArray();
		
		ArrayList< Bus > busses = new ArrayList<Bus>();
		
		for (int i=0; i<=array2.size()-1; i++)
			busses.add( new Bus( array2.get( i ).isObject() ) );
		
		Model.busList.addAll( busses );
		Model.notifyListeners( Constants.OP_REALTIMEDATA );
		
		System.out.println( Model.busList.size() );
	}
	public static void setLongtitude( double d ) {
		Model.longtitude = d;
	}
	
	public static void setLatitude( double d ) {
		Model.latitude = d;
	}
	
	public static double getLongtitude() {
		return Model.longtitude;
	}
	
	public static double getLatitude() {
		return Model.latitude;
	}
	
	public static void getData() {
		
		String json_url = "http://localhost/projects/mapper/war/server.php" + Model.urlPostfix;
		//String json_url = "server.php" + Model.urlPostfix;

		//String json_url = "tkl.json";

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				json_url);


		try {
			@SuppressWarnings("unused")
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					System.out.println("Error");
				}

				@Override
				public void onResponseReceived(Request request,
						Response response) {

					if (response.getStatusCode() == 200) {
						Model.processData( response );
					} 
					else
						System.out.println("Error code jotain"
								+ response.getStatusCode());
				}

			});
		} 
		catch (RequestException e) {
			System.out.println(" Exception error");
		}
	}
}
