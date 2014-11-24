package viewmodel;

import models.EdmTemplate;

import org.codehaus.jackson.annotate.JsonProperty;

public class EdmTemplateVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("et") public String edmType;
	
	public EdmTemplateVM() {}	
	
	public EdmTemplateVM(EdmTemplate edmTemplate) {
		this.id = edmTemplate.id;
		this.name = edmTemplate.name;
		this.edmType = edmTemplate.edmType.name();
	}
}
