package com.ymanch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ymanch.entity.Hashtag;
import com.ymanch.entity.PostHashtag;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

}
