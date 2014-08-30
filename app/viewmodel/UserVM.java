package viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import models.Location;
import models.User;
import models.UserChild;

public class UserVM {
	public String firstName;
	public String lastName;
	public String displayName;
	public String email;
	public boolean active;
	public Long id;
	
	public boolean deleted;
	public String userName;
	public String lastLogin;
	
	public String aboutMe;
	public String birthYear;
	public String location;
	public int numChildren;
	public String parentType;
	public List<UserChildVM> userChildVms = new ArrayList<>();
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.email = user.email;
		this.active = user.active;
		this.id = user.id;
		this.deleted = user.deleted;
		this.userName = user.displayName;
		this.lastLogin = formatter.format(user.lastLogin);
		this.aboutMe = user.userInfo.aboutMe;
		this.birthYear = user.userInfo.birthYear;
		this.location = user.userInfo.location.displayName;
		this.numChildren = user.userInfo.numChildren;
		this.parentType = user.userInfo.parentType.name();
		List<UserChild> userChilds = UserChild.findListById(user.id);
		for (UserChild u:userChilds) {
			UserChildVM vm = new UserChildVM(u);
			this.userChildVms.add(vm);
		}
		
	}
	
	
}
