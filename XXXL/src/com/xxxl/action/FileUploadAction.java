package com.xxxl.action;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.xxxl.service.DisplayNodes;
import com.xxxl.service.UserService;

public class FileUploadAction extends ActionSupport implements
		ServletRequestAware, SessionAware {
	private File file1;
	private String file1FileName;
	private String file1ContentType;
	private HttpServletRequest request;
	private Map<String, Object> session;
	static UserService userService;
	static {
		userService = new UserService();
	}

	public void setFile1(File file1) {
		this.file1 = file1;
	}

	public void setFile1FileName(String file1FileName) {
		this.file1FileName = file1FileName;
	}

	public void setFile1ContentType(String file1ContentType) {
		this.file1ContentType = file1ContentType;
	}

	@Override
	public String execute() throws Exception {
		// Check file name
		if (isFileExisting(file1FileName)) {
			request.setAttribute("hint", "Cannot input the same file!");
			return SUCCESS;
		}
		String userName = (String) session.get("name");
		try {
			String fileExtension = file1FileName.substring(
					file1FileName.lastIndexOf(".") + 1).toLowerCase();
			if (file1ContentType.equals("application/octet-stream")
					|| fileExtension.equals("json")) {
				userService.extractJSON(file1, file1FileName, userName);
			} else if (file1ContentType.equals("text/csv")
					|| fileExtension.equals("csv")) {
				userService.extractCSV(file1, file1FileName, userName);
			} else if (file1ContentType.equals("text/xml")
					|| fileExtension.equals("xml")) {
				userService.extractXML(file1, file1FileName, userName);
			} else {
				userService.extractOtherFiles(file1, file1FileName, userName);
			}
			userService.linking(userName);
			request.setAttribute("hint", "File Uploaded!");
		} catch (Exception e) {
			request.setAttribute("hint", "Cannot parse the file");
		}
		session.put("jsonStr", new DisplayNodes().displayFiles(userName));
		return SUCCESS;
	}

	private boolean isFileExisting(String file1FileName) {
		List<String> fileList = (List<String>) session.get("fileList");
		for (String fileName : fileList) {
			if (fileName.equals(file1FileName))
				return true;
		}
		return false;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
}
