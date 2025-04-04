package com.ymanch.repository.Page;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ymanch.entity.Posts;

public interface PagePostRepository extends PagingAndSortingRepository<Posts, Long> {

    // Page<Posts> findByUserUserIdOrderByPostCreatedAtDesc(long userId, Pageable pageable);

}
