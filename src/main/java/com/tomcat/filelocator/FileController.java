/**
 * 
 */
package com.tomcat.filelocator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 
 *
 */
@Controller
public class FileController {

	@Value("${application.userName}")
	String appUserName;
	
	@Value("${application.password}")
	String appPassword;
	
	@Value("${application.folderPath}")
	String appFolderPath;
@Autowired
	public File[] loadResources() {
		File folder;
		File[] fileList = null;
	    try {
	        folder = new File(appFolderPath);
	        fileList = folder.listFiles();
	        for(File file: fileList)
	        	System.out.println("File Name = "+file.getPath());
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    System.out.println("FileList = "+fileList.length);
	    return fileList;
	}
	
	@RequestMapping("/")
	public String home() {
		return "index";
	}
/*	
	@RequestMapping(value = "/download/{filePath}", method = RequestMethod.GET)
	@ResponseBody
	public FileSystemResource getFile(@PathVariable("filePath") String filePath) {
		try {
		System.out.println("File Path = "+filePath);
	    return new FileSystemResource(filePath); 
	}catch(Exception e) {
		e.printStackTrace();
	}
		return null;
		}*/
	@RequestMapping(value="/download")
    public void downloadFile(HttpServletResponse response,
    		@ModelAttribute(value="path") String path1,
    		@RequestParam(value="path" , required = false) String path, Model model) throws Exception {
		
     System.out.println("Download = "+path +" Path :: " + path1);
     model.addAttribute("path", "");
 
     
     File file = null;
    
         file = new File(path);
         // "C:\\Users\\Yashwanth\\Downloads\\SpringSecurityOAuth2Example.zip");
 
      
     if(!file.exists()){
         String errorMessage = "Sorry. The file you are looking for does not exist";
         System.out.println(errorMessage);
         OutputStream outputStream = response.getOutputStream();
         outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
         outputStream.close();
         return;
     }
      
     String mimeType= URLConnection.guessContentTypeFromName(file.getName());
     if(mimeType==null){
         System.out.println("mimetype is not detectable, will take default");
         mimeType = "application/octet-stream";
     }
      
     System.out.println("mimetype : "+mimeType);
      
     response.setContentType(mimeType);
      
     /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser 
         while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
     response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));

      
     /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
     //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
      
     response.setContentLength((int)file.length());

     InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

     //Copy bytes from source to destination(outputstream in this example), closes both streams.
     FileCopyUtils.copy(inputStream, response.getOutputStream());

}
	@RequestMapping("/authenticate")
	public String authenticate(@RequestParam String userName, @RequestParam String password , Model model) {
		String returnValue = (userName.equals(appUserName) &&  password.equals(appPassword)) ?"folderIndex" : "index";
		if(returnValue.equals("folderIndex")){
			model.addAttribute("fileList",loadResources());
		}
		return returnValue;
	}
	
	class Download {
		public Download() {
			// TODO Auto-generated constructor stub
		}
		@Autowired
		String path;
		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}
		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}
	}
}
