package viewmodel;

import java.text.SimpleDateFormat;

import models.Announcement;
import models.Icon;

import org.codehaus.jackson.annotate.JsonProperty;

public class AnnouncementVM {
    @JsonProperty("id") public long id;
    @JsonProperty("t") public String title;
    @JsonProperty("d") public String description;
    @JsonProperty("ic") public Icon icon;
    @JsonProperty("ty") public String type;
    @JsonProperty("fd") public String fromDate;
    @JsonProperty("td") public String toDate;
    @JsonProperty("location") public Long location;

    SimpleDateFormat formatter = new SimpleDateFormat("MMMM-dd-yyyy");
    public AnnouncementVM(Announcement announcement) {
        this.id = announcement.id;
        this.title = announcement.title;
        this.description = announcement.description;
        this.type = announcement.announcementType.name();
        this.icon = announcement.icon;
        this.fromDate = formatter.format(announcement.fromDate);
        this.toDate = formatter.format(announcement.toDate);
        this.location = announcement.location.id;
    }
    
	public AnnouncementVM(Long id, String title, String description, Icon icon, String type) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.type = type;
	}
}
