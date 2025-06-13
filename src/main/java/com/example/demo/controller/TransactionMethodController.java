// src/main/java/com/example/demo/controller/TransactionMethodController.java
package com.example.demo.controller;

import com.example.demo.model.dto.TransactionMethodDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.TransactionMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction-methods") // API 路徑使用複數形式
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"}, allowCredentials = "true")
public class TransactionMethodController {

    @Autowired
    private TransactionMethodService transactionMethodService;

    /**
     * 獲取所有可用的交易方式列表。
     * 這個 API 是公開的，因為刊登商品和結帳時都需要這些資訊。
     *
     * @return 包含所有交易方式列表的 ApiResponse。
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionMethodDto>>> getAllMethods() {
        List<TransactionMethodDto> methods = transactionMethodService.getAllTransactionMethods();
        
        // 根據查詢結果返回不同的成功訊息
        String message = methods.isEmpty() ? "查無任何交易方式資料" : "查詢所有交易方式成功";
        
        return ResponseEntity.ok(ApiResponse.success(message, methods));
    }
}