package org.exitsoft.showcase.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.exitsoft.common.utils.ValidateCodeUtils;
import org.exitsoft.showcase.common.SystemVariableUtils;
import org.exitsoft.showcase.service.account.AccountManager;
import org.exitsoft.showcase.service.account.ValidateCodeAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 系统安全控制器
 * 
 * @author vincent
 *
 */
@Controller
public class SystemCommonController {
	
	@Autowired
	private AccountManager accountManager;
	
	/**
	 * 登录C，返回登录页面。当C发现当前用户已经登录名且认真后，会自动跳转到index页面
	 * 
	 * @return String
	 */
	@RequestMapping("/login")
	public String login() {
		if (!SystemVariableUtils.isAuthenticated()) {
			return "login";
		}
		return "redirect:/index";
	}
	
	/**
	 * 首页C,在request用翻入当前用户的菜单集合给页面循环
	 * 
	 * @param model String mvc model 接口
	 * 
	 * @return String
	 */
	@RequestMapping("/index")
	public String index(Model model) {
		
		model.addAttribute("menusList", SystemVariableUtils.getCommonVariableModel().getMenusList());
		
		return "index";
	}
	
	/**
	 * 当前用户修改密码C.修改成功返回"true"否则返回"false"
	 * 
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * 
	 * @return String
	 */
	@ResponseBody
	@RequestMapping("/changePassword")
	public String changePassword(String oldPassword,String newPassword) {
		
		if (accountManager.updateUserPassword(oldPassword,newPassword)) {
			return "true";
		}
		
		return "false";
		
	}
	
	/**
	 * 生成验证码
	 * 
	 * @return byte[]
	 * @throws IOException 
	 */
	@RequestMapping("/validateCode")
	public ResponseEntity<byte[]> validateCode(HttpSession session) throws IOException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String validateCode = ValidateCodeUtils.getCode(70, 28, 4, outputStream).toLowerCase();
		
		session.setAttribute(ValidateCodeAuthenticationFilter.DEFAULT_VALIDATE_CODE_PARAM,validateCode);
		byte[] bs = outputStream.toByteArray();
		outputStream.close();
		
		return new ResponseEntity<byte[]>(bs,headers, HttpStatus.OK);
	}
}
