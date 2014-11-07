package viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

import domain.AuditFields;

import models.Location;
import models.Resource;
import models.User;
import models.UserChild;

public class UserVM {
	public String firstName;
	public String lastName;
	public String displayName;
	public String email;
	public boolean active;
	public Long id;
	public Long image;
	
	public boolean deleted;
	public String userName;
	public String lastLogin;
	
	public String aboutMe;
	public String birthYear;
	public String location;
	public int numChildren;
	public String createdOn;
	public String parentType;
	public List<UserChildVM> userChildVms = new ArrayList<>();
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public UserVM(Object[] obj) {
		this.id = ((BigInteger)obj[0]).longValue();
		this.displayName = obj[12].toString();
		this.email = obj[13].toString();
	}
	
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.email = user.email;
		this.active = user.active;
		this.id = user.id;
		if(user.deleted != null) {
			this.deleted = user.deleted;
		}
		this.userName = user.displayName;
		this.lastLogin = formatter.format(user.lastLogin);
		if(user.userInfo != null) {
			this.aboutMe = user.userInfo.aboutMe;
			this.birthYear = user.userInfo.birthYear;
			this.numChildren = user.userInfo.numChildren;
			if(user.userInfo.parentType != null) {
	            this.parentType = user.userInfo.parentType.name();
	        }
	        if(user.userInfo.location != null) {
	            this.location = user.userInfo.location.displayName;
	        }
		}
		
		this.createdOn = formatter.format(user.auditFields.getCreatedDate());
		List<UserChild> userChilds = UserChild.findListById(user.id);
		for (UserChild u:userChilds) {
			UserChildVM vm = new UserChildVM(u);
			this.userChildVms.add(vm);
		}
		if(user.albumPhotoProfile != null) {
			List<Resource> resource = Resource.findAllResourceOfFolder(user.albumPhotoProfile.id);
			this.image = resource.get(0).id;
		}
	}
}
