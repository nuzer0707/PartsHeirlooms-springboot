package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {

	/**
     * 查找兩個特定用戶之間的所有消息，按創建時間升序排列。
     *
     * @param userId1 第一個用戶的ID
     * @param userId2 第二個用戶的ID
     * @return 消息列表
     */
	@Query("SELECT m FROM Message m WHERE (m.senderUser.userId = :userId1 AND m.receiverUser.userId = :userId2) OR (m.senderUser.userId = :userId2 AND m.receiverUser.userId = :userId1) ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
	/** 
	 * 查找特定交易相關的所有消息，按創建時間升序排列。
     *
     * @param transactionId 交易ID
     * @return 消息列表
     */
    

    @Query("SELECT m FROM Message m " +
        "JOIN FETCH m.senderUser " +        // 預先抓取發送者
        "JOIN FETCH m.receiverUser " +      // 預先抓取接收者
        "JOIN FETCH m.transaction t " +     // 預先抓取交易
        "WHERE t.transactionId = :transactionId " +
        "ORDER BY m.createdAt ASC")
    List<Message> findByTransaction_TransactionIdOrderByCreatedAtAsc(@Param("transactionId") Integer transactionId);
    /**
     * 查找用戶發送或接收的所有消息（用於構建對話列表的基礎數據），
     * 可以進一步按對話對象分組和排序。
     *
     * @param userId 用戶ID
     * @return 消息列表
     */
    @Query("SELECT m FROM Message m " +
            "JOIN FETCH m.senderUser " +
            "JOIN FETCH m.receiverUser " +
            "WHERE m.senderUser.userId = :userId OR m.receiverUser.userId = :userId " +
            "ORDER BY m.createdAt DESC")
     List<Message> findAllMessagesForUser(@Param("userId") Integer userId);
    
    @Query(value = "SELECT m FROM Message m " +
            "JOIN FETCH m.senderUser " +
            "JOIN FETCH m.receiverUser " +
            "WHERE m.senderUser.userId = :userId OR m.receiverUser.userId = :userId " +
            "ORDER BY m.createdAt DESC",
    countQuery = "SELECT count(m) FROM Message m WHERE m.senderUser.userId = :userId OR m.receiverUser.userId = :userId")
    Page<Message> findAllMessagesForUser(@Param("userId") Integer userId, Pageable pageable);

    @Modifying // 表示這是一個更新操作
    @Query("UPDATE Message m SET m.isRead = true WHERE m.senderUser.userId = :senderId AND m.receiverUser.userId = :receiverId AND m.isRead = false")
    int markAsRead(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);
	
	
	
}
