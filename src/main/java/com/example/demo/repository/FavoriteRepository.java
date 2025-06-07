package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	List<Favorite> findByUserUserId(Integer userId);

	Optional<Favorite> findByUserUserIdAndProductProductId(Integer userId, Integer productId);

	void deleteByUserUserIdAndProductProductId(Integer userId, Integer productId);

	boolean existsByUserUserIdAndProductProductId(Integer userId, Integer productId);
}
