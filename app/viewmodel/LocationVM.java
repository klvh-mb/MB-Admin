package viewmodel;

import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class LocationVM {

	@JsonProperty("id") public long id;
	@JsonProperty("type") public String type;
    @JsonProperty("name") public String name;
    @JsonProperty("displayName") public String displayName;
	
    public LocationVM() {
    	
    }
    public LocationVM(Location l) {
		this.id = l.id;
		this.displayName = l.displayName;
	}
    
	public static LocationVM locationVM(Location location) {
	    LocationVM locationVM = new LocationVM();
	    locationVM.id = location.id;
	    if(location.locationType != null) {
	    	locationVM.type = location.locationType.toString();
	    }
	    locationVM.name = location.getName();
	    locationVM.displayName = location.getDisplayName();
		return locationVM;
	}
}
