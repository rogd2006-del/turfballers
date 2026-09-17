package com.turfballers.backend.service;

import com.turfballers.backend.dto.MemberRequestDto;
import com.turfballers.backend.dto.MemberResponseDto;
import com.turfballers.backend.exception.ResourceNotFoundException;
import com.turfballers.backend.model.Member;
import com.turfballers.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Member management service - CRUD and search operations.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /** Paginated member list with optional keyword search and status filter */
    public Page<MemberResponseDto> getMembers(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Member.MemberStatus memberStatus = null;
        if (status != null && !status.isBlank()) {
            try { memberStatus = Member.MemberStatus.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }
        return memberRepository.searchMembers(keyword, memberStatus, pageable)
            .map(MemberResponseDto::from);
    }

    /** Get single member by ID */
    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        return MemberResponseDto.from(member);
    }

    /** Create a new member */
    public MemberResponseDto createMember(MemberRequestDto dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }
        Member member = Member.builder()
            .fullName(dto.getFullName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .membershipPlan(dto.getMembershipPlan())
            .joinDate(dto.getJoinDate())
            .status(dto.getStatus() != null ? dto.getStatus() : Member.MemberStatus.ACTIVE)
            .build();
        return MemberResponseDto.from(memberRepository.save(member));
    }

    /** Update an existing member */
    public MemberResponseDto updateMember(Long id, MemberRequestDto dto) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        member.setFullName(dto.getFullName());
        member.setPhone(dto.getPhone());
        member.setMembershipPlan(dto.getMembershipPlan());
        if (dto.getJoinDate() != null) member.setJoinDate(dto.getJoinDate());
        if (dto.getStatus() != null) member.setStatus(dto.getStatus());
        // Only update email if changed and not taken
        if (!member.getEmail().equals(dto.getEmail())) {
            if (memberRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
            }
            member.setEmail(dto.getEmail());
        }
        return MemberResponseDto.from(memberRepository.save(member));
    }

    /** Delete a member */
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", id);
        }
        memberRepository.deleteById(id);
    }
}
