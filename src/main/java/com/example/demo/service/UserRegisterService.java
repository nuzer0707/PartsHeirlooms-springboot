package com.example.demo.service;

import com.example.demo.exception.TokenInvalidException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.model.dto.users.UserRegisterDto;

public interface UserRegisterService {

  /**
   * 處理使用者註冊請求，包括創建使用者記錄、生成驗證token並發送驗證郵件。
   * @param registrationDto 包含使用者名稱、密碼、Email的註冊資訊
   * @throws UserAlreadyExistsException 如果使用者名稱或Email已被註冊
   */
  void registerNewUser(UserRegisterDto userRegisterDto ) throws UserAlreadyExistsException;

  /**
   * 根據驗證token驗證使用者的Email地址。
   * @param token 從驗證連結中獲取的驗證token
   * @return 如果驗證成功返回 true
   * @throws TokenInvalidException 如果token無效、過期或已被使用
   * @throws UserNotFoundException 如果token對應的使用者不存在 (雖然通常會被TokenInvalidException覆蓋)
   */
  
  boolean EmailToken(String token)throws TokenInvalidException;
	
  // 可選：如果需要重新發送驗證郵件的功能
  // void resendVerificationEmail(String email) throws UserNotFoundException;

  
}
