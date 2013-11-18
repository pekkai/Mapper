package fi.uta.mapper.client.mvc;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fi.uta.mapper.client.Model;

public class AbstractView extends SimplePanel {

	private String name;
	private boolean viewActivate;
	public static VerticalPanel mainPanel;
	public static Model model;
	
	public AbstractView( String name ) {
		this.name = name;
		this.viewActivate = false;
		AbstractView.mainPanel = new VerticalPanel();
		this.getElement().setId(name);
		
		RootPanel.get("map_canvas").add( this );

		this.setStylePrimaryName( "mapPanel" );
		this.add( AbstractView.mainPanel );
		this.setVisible( false );
	}
	
	public void activate() {
		//RootPanel.get("map_canvas").add( this );
		this.viewActivate = true;
		this.setVisible( true );
	}
	
	public void deactive() {
		this.viewActivate = false;
		this.setVisible( false );
	}
	
	public boolean isActive() {
		return this.viewActivate;
	}
	
	public String toString() {
		return "["+this.name+"][View]";
	}
	
	public void setModel( Model model ) {
		AbstractView.model = model;
	}
	
	protected void update( int code ) {}
}
