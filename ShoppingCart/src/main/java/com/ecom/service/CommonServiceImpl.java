package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class CommonServiceImpl  implements CommonService{

	@Override
	public void removeSystemMessage() {

		HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
		HttpSession httpSession = request.getSession();
		httpSession.removeAttribute("successMsg");
		httpSession.removeAttribute("errorMsg");
	}

}
