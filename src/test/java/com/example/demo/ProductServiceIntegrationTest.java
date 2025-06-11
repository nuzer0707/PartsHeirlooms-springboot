package com.example.demo; // 或你的測試包路徑

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional; // 確保這個 import 正確

import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductContentDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductImageDto;
import com.example.demo.model.dto.products.ProductTransactionDetailInputDto;
import com.example.demo.model.entity.Category;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductContent;
import com.example.demo.model.entity.ProductImage;
import com.example.demo.model.entity.ProductTransactionDetail;
import com.example.demo.model.entity.TransactionMethod;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionMethodRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;

@SpringBootTest
@Transactional // 確保測試在事務中運行並回滾
public class ProductServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceIntegrationTest.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionMethodRepository transactionMethodRepository;

    private User existingSeller;
    private Category existingCategory;
    private TransactionMethod testTransactionMethod;

    @BeforeEach
    void setUp() {
        logger.info("==================== setUp START ====================");

        // 1. 獲取已存在的測試賣家 (user_id=2)
        final Integer sellerUserId = 2;
        Optional<User> sellerOpt = userRepository.findById(sellerUserId);
        if (sellerOpt.isEmpty()) {
            String errorMsg = "測試前置失敗：數據庫中找不到 user_id=" + sellerUserId + " 的用戶。請確保該用戶已存在於你的 users 表中。";
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        existingSeller = sellerOpt.get();

        // 檢查賣家角色，ProductService.addProduct 可能有權限校驗
        if (existingSeller.getPrimaryRole() != UserRole.SELLER && existingSeller.getPrimaryRole() != UserRole.ADMIN) {
            String errorMsg = "測試前置失敗：用戶 " + existingSeller.getUsername() + " (ID:" + sellerUserId + ") 的角色是 "
                             + existingSeller.getPrimaryRole() + "，而不是預期的 SELLER 或 ADMIN。";
            logger.error(errorMsg);
            // 根據你的業務邏輯，如果嚴格要求 SELLER，就拋出異常。
            // 或者，如果測試環境允許，可以臨時修改角色（但不推薦，最好是數據源正確）
            throw new IllegalStateException(errorMsg);
        }
        logger.info("已獲取測試賣家: ID={}, Username={}, Role={}",
                existingSeller.getUserId(), existingSeller.getUsername(), existingSeller.getPrimaryRole());

        // 2. 獲取已存在的測試分類 (category_id=2)
        final Integer categoryId = 2;
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            String errorMsg = "測試前置失敗：數據庫中找不到 category_id=" + categoryId + " 的分類。請確保該分類已存在於你的 categorys 表中。";
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        existingCategory = categoryOpt.get();
        logger.info("已獲取測試分類: ID={}, Name={}",
                existingCategory.getCategoryId(), existingCategory.getCategoryName());

        // 3. 確保測試交易方式存在 (嘗試獲取 ID=1，如果不存在則創建一個新的)
        final Integer preferredMethodId = 1;
        Optional<TransactionMethod> methodOpt = transactionMethodRepository.findById(preferredMethodId);
        if (methodOpt.isPresent()) {
            testTransactionMethod = methodOpt.get();
            logger.info("已獲取已存在的交易方式: ID={}, Name={}",
                    testTransactionMethod.getMethodId(), testTransactionMethod.getName());
        } else {
            logger.warn("數據庫中找不到 method_id={} 的交易方式，將創建一個新的用於測試。", preferredMethodId);
            TransactionMethod newMethod = new TransactionMethod();
            newMethod.setName("JUnit 自動創建的面交 " + System.currentTimeMillis()); // 名稱唯一，避免可能的唯一約束衝突
            newMethod.setDescription("如果 ID=" + preferredMethodId + " 的交易方式不存在，則自動創建此方式。");
            newMethod.setCreatedAt(LocalDateTime.now()); // 確保設置了這個
            try {
                testTransactionMethod = transactionMethodRepository.saveAndFlush(newMethod); // ID 會自動生成
                logger.info("已創建新的測試交易方式: ID={}, Name={}",
                        testTransactionMethod.getMethodId(), testTransactionMethod.getName());
            } catch (Exception e) {
                logger.error("！！！創建新的 TransactionMethod 失敗。", e);
                throw new RuntimeException("創建測試用 TransactionMethod 失敗", e); // 讓測試明確失敗
            }
        }
        logger.info("==================== setUp END ====================");
    }

    @Test
    void testAddProduct_ViaService_UsingExistingUserAndCategory_Success() throws UserNotFoundException, CategoryNotFoundException {
        logger.info("---------- testAddProduct_ViaService_UsingExistingUserAndCategory_Success START ----------");
        // 1. 準備 ProductAddDto
        ProductAddDto productAddDto = new ProductAddDto();
        productAddDto.setCategoryId(existingCategory.getCategoryId());
        productAddDto.setPrice(new BigDecimal("550.25"));
        productAddDto.setQuantity(12);

        ProductContentDto contentDto = new ProductContentDto(
                "服務測試產品 (依賴已有數據)",
                "通過 ProductService 新增，使用 setUp 準備的 User 和 Category。",
                "詳細描述：驗證 ProductService 層的 addProduct 方法是否能正確處理並保存產品及其關聯。"
        );
        productAddDto.setContent(contentDto);

        List<ProductImageDto> images = new ArrayList<>();
        images.add(new ProductImageDto("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="));
        productAddDto.setImages(images);

        List<ProductTransactionDetailInputDto> transactionDetails = new ArrayList<>();
        ProductTransactionDetailInputDto detailInputDto = new ProductTransactionDetailInputDto();
        detailInputDto.setMethodId(testTransactionMethod.getMethodId());
        transactionDetails.add(detailInputDto);
        productAddDto.setTransactionDetails(transactionDetails);

        // 2. 呼叫 ProductService 的 addProduct 方法
        logger.info("調用 ProductService.addProduct，賣家 ID: {}, 分類 ID: {}, DTO: {}",
                existingSeller.getUserId(), existingCategory.getCategoryId(), productAddDto);
        ProductDto addedProductDto = productService.addProduct(productAddDto, existingSeller.getUserId());

        // 3. 驗證返回的 ProductDto
        assertNotNull(addedProductDto, "返回的 ProductDto 不應為 null");
        assertNotNull(addedProductDto.getProductId(), "新增產品後，ProductId 不應為 null");
        assertEquals(contentDto.getTitle(), addedProductDto.getTitle());
        assertTrue(productAddDto.getPrice().compareTo(addedProductDto.getPrice()) == 0, "價格不匹配");
        assertEquals(productAddDto.getQuantity(), addedProductDto.getQuantity());
        assertEquals(existingSeller.getUserId(), addedProductDto.getSellerUserId());
        assertEquals(existingSeller.getUsername(), addedProductDto.getSellerUsername()); // 額外驗證賣家名稱
        assertEquals(existingCategory.getCategoryId(), addedProductDto.getCategoryId());
        assertEquals(existingCategory.getCategoryName(), addedProductDto.getCategoryName()); // 額外驗證分類名稱
        assertEquals(ProductStatus.For_Sale, addedProductDto.getStatus(), "新產品狀態應為 For_Sale");
        assertNotNull(addedProductDto.getImageBases64());
        assertEquals(images.size(), addedProductDto.getImageBases64().size());
        assertNotNull(addedProductDto.getTransactionDetails());
        assertEquals(transactionDetails.size(), addedProductDto.getTransactionDetails().size());
        assertEquals(testTransactionMethod.getMethodId(), addedProductDto.getTransactionDetails().get(0).getMethodId());
        assertEquals(testTransactionMethod.getName(), addedProductDto.getTransactionDetails().get(0).getMethodName()); // 額外驗證交易方式名稱


        // 4. 從資料庫中直接查詢並驗證 (更深層次的驗證)
        Optional<Product> productFromDbOptional = productRepository.findById(addedProductDto.getProductId());
        assertTrue(productFromDbOptional.isPresent(), "新增的產品應存在于資料庫中");

        Product productFromDb = productFromDbOptional.get();
        assertNotNull(productFromDb.getProductContent(), "從DB獲取的產品的ProductContent不應為null");
        assertEquals(contentDto.getTitle(), productFromDb.getProductContent().getTitle());
        assertTrue(productAddDto.getPrice().compareTo(productFromDb.getPrice()) == 0);
        assertEquals(productAddDto.getQuantity(), productFromDb.getQuantity());
        assertNotNull(productFromDb.getSellerUser(), "從DB獲取的產品的SellerUser不應為null");
        assertEquals(existingSeller.getUserId(), productFromDb.getSellerUser().getUserId());
        assertNotNull(productFromDb.getCategory(), "從DB獲取的產品的Category不應為null");
        assertEquals(existingCategory.getCategoryId(), productFromDb.getCategory().getCategoryId());
        assertEquals(ProductStatus.For_Sale, productFromDb.getStatus());
        assertNotNull(productFromDb.getProductImages());
        assertEquals(images.size(), productFromDb.getProductImages().size());
        assertNotNull(productFromDb.getTransactionDetails());
        assertEquals(transactionDetails.size(), productFromDb.getTransactionDetails().size());
        assertNotNull(productFromDb.getTransactionDetails().get(0).getTransactionMethod(), "從DB獲取的產品的交易明細的TransactionMethod不應為null");
        assertEquals(testTransactionMethod.getMethodId(), productFromDb.getTransactionDetails().get(0).getTransactionMethod().getMethodId());

        logger.info("成功通過服務新增並驗證產品 ID: {} - {}", addedProductDto.getProductId(), addedProductDto.getTitle());
        logger.info("---------- testAddProduct_ViaService_UsingExistingUserAndCategory_Success END ----------");
    }

    @Test
    void testAddProduct_DirectlyToRepository_UsingExistingData_Success() {
        logger.info("---------- testAddProduct_DirectlyToRepository_UsingExistingData_Success START ----------");
        // 1. 準備 Product 實體
        Product product = new Product();
        product.setSellerUser(existingSeller);
        product.setCategory(existingCategory);
        product.setPrice(new BigDecimal("299.00"));
        product.setQuantity(5);
        // product.setStatus(ProductStatus.For_Sale); // Product 實體中已有默認值
        product.setCreatedAt(LocalDateTime.now());

        ProductContent content = new ProductContent();
        content.setProduct(product);
        content.setTitle("倉庫直接新增的SSD");
        content.setShortDescription("用於測試倉庫層保存的SSD，依賴已有的User/Category。");
        content.setFullDescription("這是一個高性能的固態硬碟，讀寫速度快。");
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        product.setProductContent(content);

        List<ProductImage> productImages = new ArrayList<>();
        ProductImage image1 = new ProductImage();
        image1.setProduct(product);
        image1.setImageBase64("data:image/png;base64,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");
        image1.setCreatedAt(LocalDateTime.now());
        productImages.add(image1);
        product.setProductImages(productImages);

        List<ProductTransactionDetail> transactionDetailsList = new ArrayList<>();
        ProductTransactionDetail detail1 = new ProductTransactionDetail();
        detail1.setProduct(product);
        detail1.setTransactionMethod(testTransactionMethod);
        transactionDetailsList.add(detail1);
        product.setTransactionDetails(transactionDetailsList);

        // 2. 直接使用 Repository 保存
        Product savedProduct = productRepository.saveAndFlush(product); // 使用 saveAndFlush 確保立即寫入和獲取 ID

        // 3. 驗證
        assertNotNull(savedProduct, "保存的產品不應為 null");
        assertNotNull(savedProduct.getProductId(), "產品 ID 不應為 null");
        assertEquals(existingSeller.getUserId(), savedProduct.getSellerUser().getUserId());
        assertEquals(existingCategory.getCategoryId(), savedProduct.getCategory().getCategoryId());
        assertEquals(ProductStatus.For_Sale, savedProduct.getStatus()); // 驗證默認狀態
        assertNotNull(savedProduct.getProductContent());
        assertEquals("倉庫直接新增的SSD", savedProduct.getProductContent().getTitle());
        assertNotNull(savedProduct.getProductImages());
        assertFalse(savedProduct.getProductImages().isEmpty());
        assertEquals(image1.getImageBase64(), savedProduct.getProductImages().get(0).getImageBase64());
        assertNotNull(savedProduct.getTransactionDetails());
        assertFalse(savedProduct.getTransactionDetails().isEmpty());
        assertEquals(testTransactionMethod.getMethodId(), savedProduct.getTransactionDetails().get(0).getTransactionMethod().getMethodId());

        logger.info("成功直接向倉庫新增並驗證產品 ID: {} - {}", savedProduct.getProductId(), savedProduct.getProductContent().getTitle());
        logger.info("---------- testAddProduct_DirectlyToRepository_UsingExistingData_Success END ----------");
    }
}