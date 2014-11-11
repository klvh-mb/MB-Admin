package viewmodel;

import models.Announcement;
import models.Icon;

import org.codehaus.jackson.annotate.JsonProperty;

import common.utils.DateTimeUtil;

public class AnnouncementVM {
    @JsonProperty("id") public long id;
    @JsonProperty("t") public String title;
    @JsonProperty("d") public String description;
    @JsonProperty("ic") public Icon icon;
    @JsonProperty("ty") public String type;
    @JsonProperty("fd") public String fromDate;
    @JsonProperty("td") public String toDate;

    public AnnouncementVM(Announcement announcement) {
        this.id = announcement.id;
        this.title = announcement.title;
        this.description = announcement.description;
        this.type = announcement.announcementType.name();
        this.icon = announcement.icon;
        this.fromDate = DateTimeUtil.toString(announcement.fromDate);
        this.toDate = DateTimeUtil.toString(announcement.toDate);
    }
    
	public AnnouncementVM(Long id, String title, String description, Icon icon, String type) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.type = type;
	}
}
