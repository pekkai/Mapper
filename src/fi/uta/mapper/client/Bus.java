package fi.uta.mapper.client;

import com.google.gwt.json.client.JSONObject;

public class Bus {
	private String name;
	private String line;
	private double latitude;
	private double longitude;

	public Bus( JSONObject data ) {
		this.init( data );
	}
		
	private void init( JSONObject data ) {
		this.name = data.get("MonitoredVehicleJourney").isObject().get("VehicleRef").isObject().get("value").toString();
		this.line = data.get("MonitoredVehicleJourney").isObject().get("LineRef" ).isObject().get("value").toString();
		this.latitude = data.get("MonitoredVehicleJourney").isObject().get("VehicleLocation").isObject().get("Latitude").isNumber().doubleValue();
		this.longitude = data.get("MonitoredVehicleJourney").isObject().get("VehicleLocation").isObject().get("Longitude").isNumber().doubleValue();
			
	}

	public String toString() {
		return this.name + "[" + this.line +"]["+this.latitude+", "+this.longitude+"]";
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getLine() {
		return Integer.parseInt( this.line.replaceAll("\"", "") );
	}
	
	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}
}
