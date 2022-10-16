package com.zb.fastlms.admin.service;

import com.zb.fastlms.admin.dto.CategoryDto;
import com.zb.fastlms.admin.model.CategoryInput;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> list();
    boolean add(String categoryName);

    /**
     * 카테고리 수정
     */
    boolean update(CategoryInput parameter);

    /**
     * 카테고리 삭제
     */
    boolean del(long id);
}
