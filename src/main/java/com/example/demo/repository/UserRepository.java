package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer>{

	@Query(value = "select user_id, username, password_hash, hash_salt, email, active, primary_role,created_at from users where username=:username", nativeQuery = true)
	User getUser(String username);
	
	// Optional<User> findByUsername(String username); 根據用戶名查找，返回
	
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
	
}
