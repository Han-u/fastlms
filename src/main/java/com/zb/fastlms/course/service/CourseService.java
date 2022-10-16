package com.zb.fastlms.course.service;

import com.zb.fastlms.course.dto.CourseDto;
import com.zb.fastlms.course.model.CourseInput;
import com.zb.fastlms.course.model.CourseParam;

import java.util.List;

public interface CourseService {
    /**
     * 강좌 등록
     */
    boolean add(CourseInput parameter);

    /**
     * 강좌 목록
     */
    List<CourseDto> list(CourseParam parameter);

    /**
     * 강좌 상세정보
     */
    CourseDto getById(long id);

    /**
     * 강좌 정보수정
     */
    boolean set(CourseInput parameter);

    /**
     * 강좌 내용 삭제
     */
    boolean del(String idList);
}
