package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CommonService {
	
	public void removeSessionMessage();
	
	public String saveImage(MultipartFile file, String folderName);
}
