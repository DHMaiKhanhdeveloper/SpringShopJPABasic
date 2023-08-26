package com.example.shop.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
