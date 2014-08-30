package viewmodel;

import models.UserChild;

public class UserChildVM {

	public Long id;
    public String birthYear;
    public String birthMonth;
    public String birthDay;
    public String gender;
    
    public UserChildVM() {
    	
    }
    
    public UserChildVM(UserChild userChild) {
    	this.id = userChild.id;
    	this.birthDay = userChild.birthDay;
    	this.birthMonth = userChild.birthMonth;
    	this.birthYear = userChild.birthYear;
    	this.gender = userChild.gender;
    }
}
