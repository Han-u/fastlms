package com.zb.fastlms.admin.repository;

import com.zb.fastlms.admin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<Category>> findAllOrderBySortValueDesc();
}
