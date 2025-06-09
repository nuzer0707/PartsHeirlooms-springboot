package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	@Query(value = "select user_id, username, password_hash, hash_salt, email, active, primary_role,email_token,last_email_sent_at,created_at from users where username=:username", nativeQuery = true)
	User getUser(String username);
	
		Optional<User> findByUsername(String username);// 根據用戶名查找，返回
	
		// 根據 email 查找，返回 Optional<User>
		Optional<User> findByEmail(String email);
		// 檢查用戶名是否存在
		boolean existsByUsername(String username);
		// 檢查 email 是否存在
		boolean existsByEmail(String email);
		// 查找所有活躍或非活躍的用戶
    List<User> findByActive(Boolean active);
    // 查找特定角色的用戶
    List<User> findByPrimaryRole(UserRole primaryRole);
    // 查找特定角色且活躍的用戶
    List<User> findByPrimaryRoleAndActive(UserRole primaryRole, Boolean active);
    // 查找特定角色的用戶
    List<User> findByPrimaryRoleIn(List<UserRole> roles);
    
    // 根據 emailToken 查找用戶 (用於郵件驗證)
    Optional<User> findByEmailToken(String emailToken);
    
    /**
     * 計算活躍用戶的數量。
     * @param active 狀態
     * @return 活躍用戶數
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    // 如果你的 User 實體中 `active` 欄位有不同的命名，例如 `isActive`，你需要對應修改：
    // long countByIsActive(Boolean isActive);
    long countByActive(Boolean active);
}
