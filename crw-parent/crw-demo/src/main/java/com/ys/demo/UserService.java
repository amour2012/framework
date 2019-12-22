package com.ys.demo;

import org.springframework.stereotype.Service;

import com.ys.crw.meta.RegionParam;
import com.ys.crw.meta.SupportRemote;

/**
 * @author oscar.wu
 *
 */
@Service
public class UserService {
	public UserService(){
		
	}
	public String getUserName(int userId){
		return "";
	}
	
	@SupportRemote
	public boolean saveUserName(int userId, String userName){
		return true;
	}
	
	@SupportRemote
	public Object login(int userId, @RegionParam String userRegion){
		return "success";
	}

}
