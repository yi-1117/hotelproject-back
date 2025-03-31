package tw.com.ispan.eeit195_01_back.member.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberStatus;
import tw.com.ispan.eeit195_01_back.member.repository.MemberDetailsRepository;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;

@Service
@Transactional
public class MemberDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberDetailsRepository memberDetailsRepository;

    @Autowired
    private MemberService memberService; // 注入 MemberService 處理 MemberBean

    private static final Logger logger = LoggerFactory.getLogger(MemberDetailsService.class);

    // 根據會員 ID 查詢會員詳細資料
    public Optional<MemberDetailsBean> getMemberDetailsByMemberId(Integer memberId) {
        logger.info("Getting details for member with ID: {}", memberId);
        return memberDetailsRepository.findByMemberId(memberId);
    }

    // 查詢所有會員詳細資料（分頁）
    public Page<MemberDetailsBean> getAllMemberDetails(Pageable pageable) {
        return memberDetailsRepository.findAll(pageable);
    }

    // 新增或更新會員詳細資料
    public MemberDetailsBean saveOrUpdateDetails(MemberDetailsBean details) {
        if (details.getMemberBean() == null || details.getMemberBean().getMemberId() == null) {
            throw new IllegalStateException("Member information is missing or invalid.");
        }

        MemberBean member = memberService.findMemberById(details.getMemberBean().getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with ID: " + details.getMemberBean().getMemberId()));

        details.setMemberBean(member);

        Optional<MemberDetailsBean> existingDetails = memberDetailsRepository.findByMemberId(member.getMemberId());
        if (existingDetails.isPresent()) {
            MemberDetailsBean existing = existingDetails.get();
            existing.setFullName(details.getFullName());
            existing.setGender(details.getGender());
            existing.setPhoneNumber(details.getPhoneNumber());
            existing.setAddress(details.getAddress());
            existing.setDateOfBirth(details.getDateOfBirth());
            existing.setNationality(details.getNationality());
            existing.setPreferredLanguage(details.getPreferredLanguage());
            existing.setNewsletterSubscription(details.getNewsletterSubscription());
            existing.setSocialMediaAccount(details.getSocialMediaAccount());

            // 僅在 profilePicture 不為 null 時更新頭像
            // if (details.getProfilePicture() != null) {
            // existing.setProfilePicture(details.getProfilePicture());
            // }

            existing.setUpdatedAt(LocalDateTime.now());
            return memberDetailsRepository.save(existing);
        }
        details.setJoinDate(LocalDateTime.now());
        details.setUpdatedAt(LocalDateTime.now());

        // 如果資料不存在，直接新增，@PrePersist 自動設定 joinDate
        return memberDetailsRepository.save(details);
    }

    // 更新社交媒體帳號
    public void updateLineProfile(String userId, String displayName, String pictureUrl) {
        System.out.println("更新 LINE Profile：userId=" + userId + ", displayName=" + displayName);

        // 查找會員詳細資料
        MemberDetailsBean memberDetails = memberDetailsRepository.findBySocialMediaAccount(userId).orElse(null);

        // 檢查是否存在 `MemberDetailsBean`，但 `member_id` 是空的
        if (memberDetails != null && memberDetails.getMemberBean() != null) {
            System.out.println("會員已存在，memberId=" + memberDetails.getMemberBean().getMemberId());
            return; // 直接返回，不要錯誤更新
        }

        System.out.println("會員不存在，應該進入註冊頁");

        // 創建新的會員
        MemberBean newMember = new MemberBean();
        newMember.setIsVerified(true);
        newMember.setStatus(MemberStatus.ACTIVE);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        newMember = memberRepository.saveAndFlush(newMember);

        // 創建 `MemberDetailsBean`
        MemberDetailsBean newMemberDetails = new MemberDetailsBean();
        newMemberDetails.setMemberBean(newMember);
        newMemberDetails.setSocialMediaAccount(userId);
        newMemberDetails.setFullName(displayName);
        newMemberDetails.setProfilePicture(pictureUrl);
        newMemberDetails.setUpdatedAt(LocalDateTime.now());

        memberDetailsRepository.saveAndFlush(newMemberDetails);
    }

    public Optional<MemberDetailsBean> findBySocialMediaAccount(String socialMediaAccount) {
        return memberDetailsRepository.findBySocialMediaAccount(socialMediaAccount);
    }

    // 刪除會員詳細資料
    public void deleteMemberDetails(Integer memberId) {
        logger.info("Attempting to delete MemberDetails for member with ID: {}", memberId);
        if (memberDetailsRepository.existsById(memberId)) {
            memberDetailsRepository.deleteById(memberId); // 僅刪除詳細資料
            logger.info("Deleted MemberDetails for member with ID: {}", memberId);
        } else {
            logger.error("Member details not found with ID: {}", memberId);
            throw new IllegalArgumentException("Member details not found with ID: " + memberId);
        }
    }

    public String saveFile(MultipartFile file, Integer memberId, String uploadDir) throws IOException {
        // 確保上傳路徑存在
        File uploadPath = new File(uploadDir + memberId + "/");
        if (!uploadPath.exists()) {
            uploadPath.mkdirs(); // 如果資料夾不存在則創建
        }

        // 生成檔案名稱
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(uploadPath, fileName);

        // 儲存檔案
        file.transferTo(destination);

        // 返回簡單的路徑（只包含會員資料夾，沒有檔案名稱）
        return "/profile_pictures/" + memberId; // 修改為只返回資料夾路徑
    }

    // 新增更新頭像的方法
    public void updateProfilePicture(Integer memberId, String profilePicture) {
        memberDetailsRepository.findByMemberId(memberId).ifPresentOrElse(memberDetails -> {
            // 若 profilePicture 不為 null，才更新頭像
            if (profilePicture != null) {
                memberDetails.setProfilePicture(profilePicture);
                memberDetailsRepository.save(memberDetails);
            }
        }, () -> {
            throw new IllegalArgumentException("會員 ID: " + memberId + " 的資料不存在");
        });
    }

    public Mono<MemberDetailsBean> findMonoBySocialMediaAccount(String socialMediaAccount) {
        return Mono.fromCallable(() -> memberDetailsRepository.findBySocialMediaAccount(socialMediaAccount))
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }

    // 將同步 save() 轉成 Mono
    public Mono<MemberDetailsBean> saveMember(MemberDetailsBean member) {
        return Mono.fromCallable(() -> memberDetailsRepository.save(member));
    }
}
