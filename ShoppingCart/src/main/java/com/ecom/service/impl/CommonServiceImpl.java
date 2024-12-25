package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class CommonServiceImpl  implements CommonService{

	@Override
	public void removeSessionMessage() {

		HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
		HttpSession httpSession = request.getSession();
		httpSession.removeAttribute("succMsg");
		httpSession.removeAttribute("errorMsg");
	}

	@Override
	public String saveImage(MultipartFile file, String folderName) {

		if(!file.isEmpty()) {
			try {
				
				String filename = file.getOriginalFilename();
				
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + folderName + File.separator
						+ filename);

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				return filename;
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
