package com.team8.project2.domain.member.repository;


import com.team8.project2.domain.member.entity.Member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByMemberId(String MemberId);
  Optional<Member> findByUsername(String username);
  Page<Member> findAll(Pageable pageable);
}
