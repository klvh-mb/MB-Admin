package viewmodel;

import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class LocationVM {

	@JsonProperty("id") public long id;
	@JsonProperty("district") public String district;
	
	public static LocationVM locationVM(Location location) {
	    LocationVM locationVM = new LocationVM();
	    locationVM.id = location.id;
	    locationVM.district = location.district;
		return locationVM;
	}
}
