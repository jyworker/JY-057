package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.FamilyMember;
import com.hugmom.back.service.FamilyMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 家庭成员控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private FamilyMemberService familyMemberService;

    /**
     * 获取家庭成员列表
     */
    @GetMapping("/members")
    public Result<List<FamilyMember>> getFamilyMembers(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<FamilyMember> members = familyMemberService.getFamilyMembers(userId);
        return Result.success("获取成功", members);
    }

    /**
     * 获取单个家庭成员详情
     */
    @GetMapping("/member/{memberId}")
    public Result<FamilyMember> getFamilyMember(@PathVariable String memberId) {
        FamilyMember member = familyMemberService.getFamilyMember(memberId);
        return Result.success("获取成功", member);
    }

    /**
     * 添加家庭成员
     */
    @PostMapping("/member")
    public Result<FamilyMember> addFamilyMember(
            HttpServletRequest request,
            @RequestBody FamilyMember member) {
        
        String userId = (String) request.getAttribute("userId");
        FamilyMember newMember = familyMemberService.addFamilyMember(userId, member);
        return Result.success("添加成功", newMember);
    }

    /**
     * 更新家庭成员信息
     */
    @PutMapping("/member/{memberId}")
    public Result<FamilyMember> updateFamilyMember(
            @PathVariable String memberId,
            @RequestBody FamilyMember member) {
        
        FamilyMember updatedMember = familyMemberService.updateFamilyMember(memberId, member);
        return Result.success("更新成功", updatedMember);
    }

    /**
     * 删除家庭成员
     */
    @DeleteMapping("/member/{memberId}")
    public Result<Void> deleteFamilyMember(@PathVariable String memberId) {
        familyMemberService.deleteFamilyMember(memberId);
        return Result.success("删除成功", null);
    }
}
