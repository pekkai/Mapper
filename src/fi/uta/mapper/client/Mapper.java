package fi.uta.mapper.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mapper implements EntryPoint {

	@SuppressWarnings("unused")
	private Model model;
	
	@Override
	public void onModuleLoad() {
		
		
		// Initialize the MVC 
		this.init();
	}
	 
	private void init() {

		this.model = new Model();
		HashMap<String, String> settings = new HashMap<String, String>();
		settings.put("map", "gmap");
		settings.put("zoom", "16");
		settings.put("lat", "61.498294");
		settings.put("lon", "23.757935");
		

		
		Map<String, List<String>> map = Window.Location.getParameterMap();
		
		@SuppressWarnings("rawtypes")
		Iterator iterator = map.entrySet().iterator();	
		while( iterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, List<String>> pairs = (Map.Entry<String, List<String>>)iterator.next();
			String key = pairs.getKey().toString();
					//+ " " + ((List<String>)pairs.getValue()).toString() );
		
			if( key.equals( "map" ) || key.equals( "zoom" ) || key.equals( "lat" ) || key.equals( "lon" ) ) {
				settings.put( key, ((List<String>)pairs.getValue()).get(0).toString() );
			}
			
		}

		@SuppressWarnings("rawtypes")
		Iterator settingsIterator = settings.entrySet().iterator();
		while( settingsIterator.hasNext() ) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> pairs = (Map.Entry<String, String>)settingsIterator.next();
	
			String key = pairs.getKey();
						
			if( key.equals( "zoom" ) ) {
				int zoomLevel = Integer.parseInt( ((String)pairs.getValue()) );
				Model.setZoomLevel( zoomLevel );
			}
			else if( key.equals("lat") ) {
				Model.setLatitude( Double.parseDouble( ((String)pairs.getValue()) ));
			}
			else if( key.equals("lon") ) {
				Model.setLongtitude( Double.parseDouble( ((String)pairs.getValue()) ));
				
			}
			else if( key.equals("map") ) {
				if( ((String)pairs.getValue()).equals( "osm") )
					Model.activateOSM();
				else if( ((String)pairs.getValue()).equals("gmap") )
					Model.activateGM();	
				
			}
		}
		
		
		

		
	}

}
