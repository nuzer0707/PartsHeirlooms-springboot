package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.TokenInvalidException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.users.UserRegisterDto;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserRegisterService;
import com.example.demo.util.Hash;


@Service
public class UserRegisterServiceImpl implements UserRegisterService {
	
	@Autowired
	private  UserRepository userRepository ;
	
	@Autowired
	private  EmailService emailService;
	
	@Value("${app.base-url}")
	private  String appBaseUrl;
	
	 private static final int RESEND_COOLDOWN_SECONDS = 10; // 定義冷卻時間為常數
	
	
	@Override
  @Transactional
	public void registerNewUser(UserRegisterDto userRegisterDto) throws UserAlreadyExistsException {
		
		if(userRepository.existsByUsername(userRegisterDto.getUsername())){
			throw new UserAlreadyExistsException("使用者名稱"+userRegisterDto.getUsername()+"已被註冊");
		}
		
		if(userRepository.existsByUsername(userRegisterDto.getEmail())) {		
			throw new UserAlreadyExistsException("電子郵件地址"+userRegisterDto.getEmail()+"已被註冊");
		}
		
		User user =new User();
		user.setUsername(userRegisterDto.getUsername());
		String salt = Hash.getSalt();
		user.setHashSalt(salt);
		user.setPasswordHash(Hash.getHash(userRegisterDto.getPassword(),salt));
		user.setEmail(userRegisterDto.getEmail());
		user.setPrimaryRole(UserRole.BUYER);// 新註冊用戶預設角色
		user.setActive(false);// 新註冊用戶預設未激活
		
		String token = UUID.randomUUID().toString();
		user.setEmailToken(token); // 假設 User 實體中 token 屬性名為 emailToken
		user.setLastEmailSentAt(LocalDateTime.now());// <--- 記錄首次發送時間
		userRepository.save(user);
		
		// 構建驗證連結和郵件內容
		
		String verificationLink = appBaseUrl + "/verify-email?token=" +  token ;
		String emailSubject ="帳戶驗證 - 零件傳承坊";
		String htmlContent = String.format(/* ... 郵件內容 ... */
						 "<html><body>" +
             "<p>親愛的 %s,</p>" +
             "<p>感謝您註冊 零件傳承坊！請點擊以下連結來驗證您的帳戶：</p>" + // 請替換
             "<p><a href=\"%s\">%s</a></p>" +
             "<p>如果您沒有請求此驗證，請忽略此郵件。</p>" +
             "<p>謝謝，<br/>零件傳承坊 團隊</p>" + // 請替換
             "</body></html>",
           user.getUsername(), verificationLink, verificationLink
				);
		
		emailService.sendHtmlEmail(user.getEmail(), emailSubject, htmlContent);
	}

	
	
	@Override
	@Transactional
	public boolean EmailToken(String token) throws TokenInvalidException {
		if(token == null || token.trim().isEmpty()) {
			throw new TokenInvalidException("驗證 Token 不能為空");
		}
			
		User user = userRepository.findByEmailToken(token)
								.orElseThrow(()-> new TokenInvalidException("無效的驗證 Token 或 Token 已被使用"));
		
		if(user.getActive()) {
			throw new TokenInvalidException("此帳戶已經被驗證過了");
		}
		
		user.setActive(true);
		user.setEmailToken(null); // 成功驗證後清除Token
		userRepository.save(user);
		
		return true;
		
	}



	@Override
	public void resendEmail(String email) throws UserNotFoundException, IllegalStateException {
		User user = userRepository.findByEmail(email)
															.orElseThrow(()-> new UserNotFoundException("找不到 Email 為:"+email+"的使用者"));
		if(user.getActive()) {
			throw new IllegalStateException("此帳戶已經是激活狀態，無需重新驗證");
		}
		
		// 檢查上次發送時間，實現60秒冷卻
		if(user.getLastEmailSentAt()!=null&& ChronoUnit.SECONDS.between(user.getLastEmailSentAt(), LocalDateTime.now())<RESEND_COOLDOWN_SECONDS) {
			long secondsRemaining = RESEND_COOLDOWN_SECONDS - ChronoUnit.SECONDS.between(user.getLastEmailSentAt(), LocalDateTime.now());
			throw new IllegalStateException("請求過於頻繁，請在 " + secondsRemaining + " 秒後再試");
		}
		 // 總是生成一個新的 token
		String newToken = UUID.randomUUID().toString();
		user.setEmailToken(newToken);
		
		user.setLastEmailSentAt(LocalDateTime.now());// <--- 更新本次發送時間
		
		String verificationLink = appBaseUrl + "/api/auth/verify-email?token=" + newToken;
		String emailSubject = "重新發送帳戶驗證 - 零件傳承坊";
		
		String htmlContent = String.format(
         "<html><body>"
         + "<p>親愛的 %s,</p>"
         + "<p>您請求了重新發送帳戶驗證郵件。請點擊以下連結進行驗證：</p>"
         + "<p><a href=\"%s\">%s</a></p>"
         + "<p>如果您沒有請求此驗證，請忽略此郵件。</p>"
         + "<p>謝謝，<br/>零件傳承坊 團隊</p>"
         + "</body></html>",
         user.getUsername(), verificationLink, verificationLink
    );
		emailService.sendHtmlEmail(user.getEmail(), emailSubject, htmlContent);
	}

}
