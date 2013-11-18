package fi.uta.mapper.client.mvc;

import java.util.ArrayList;

public class AbstractModel {

	private static ArrayList<AbstractView> listeners;
	
	public AbstractModel() {
		AbstractModel.listeners = new ArrayList<AbstractView>();
		System.out.println( "Model created." );
	}
	
	public void addListener( AbstractView view ) {
		AbstractModel.listeners.add( view );

		System.out.println( "Added " + view.toString() + " to model." );
	}
	
	public static void notifyListeners( int code ) {		
		for( int i = 0; i < AbstractModel.listeners.size(); i++ )
			AbstractModel.listeners.get( i ).update( code );
	}
}
