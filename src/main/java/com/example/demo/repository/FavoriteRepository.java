package com.example.demo.repository;

import com.example.demo.model.entity.Favorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	@EntityGraph(attributePaths = {"product", "product.productContent", "product.productImages","product.category"})
	List<Favorite> findByUserUserId(Integer userId);

	Optional<Favorite> findByUserUserIdAndProductProductId(Integer userId, Integer productId);

	void deleteByUserUserIdAndProductProductId(Integer userId, Integer productId);

	boolean existsByUserUserIdAndProductProductId(Integer userId, Integer productId);
}
