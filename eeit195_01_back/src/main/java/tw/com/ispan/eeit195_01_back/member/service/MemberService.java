package tw.com.ispan.eeit195_01_back.member.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberStatus;
import tw.com.ispan.eeit195_01_back.member.bean.VerificationEmailBean;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotFoundException;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotVerifiedException;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.member.repository.VerificationEmailRepository;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.repository.PointsRepository;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingCartService;

@Service
@Transactional
public class MemberService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private VerificationEmailRepository verificationEmailRepository;

    @Autowired
    private PointsRepository pointsRepository;
    @Autowired
    private ShoppingCartService shoppingCartService;

    public Page<MemberBean> searchMembers(String search, String status, Boolean isVerified, Pageable pageable) {
        Specification<MemberBean> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 會員狀態篩選
            if (status != null && !status.isEmpty()) {
                try {
                    MemberStatus memberStatus = MemberStatus.valueOf(status.toUpperCase()); // 保險起見，轉大寫避免錯誤
                    predicates.add(cb.equal(root.get("status"), memberStatus));
                } catch (IllegalArgumentException e) {
                    // 如果 status 不是合法的 enum 值，就不加入條件
                    System.err.println("無效的會員狀態：" + status);
                }
            }

            // 是否驗證篩選
            if (isVerified != null) {
                predicates.add(cb.equal(root.get("isVerified"), isVerified));
            }

            // 搜尋條件 (memberId 或 email)
            if (search != null && !search.isEmpty()) {
                Predicate searchPredicate;
                try {
                    Integer memberId = Integer.valueOf(search);
                    searchPredicate = cb.equal(root.get("memberId"), memberId);
                } catch (NumberFormatException e) {
                    searchPredicate = cb.like(root.get("email"), "%" + search + "%");
                }
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return memberRepository.findAll(spec, pageable);
    }

    // 註冊會員
    public MemberBean registerMember(MemberBean member) {
        // 檢查 email 是否已經註冊
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered: " + member.getEmail());
        }

        // 設定創建與更新時間
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        // 預設為未驗證
        member.setIsVerified(false);

        // 保存會員資料（尚未填寫 password）
        MemberBean savedMember = memberRepository.save(member);

        // 呼叫購物車生成方法
        int memberId = savedMember.getMemberId();
        shoppingCartService.createShoppingCart(memberId);

        // 創建一個bean 勿刪
        PointsBean pointsBean = new PointsBean();
        pointsBean.setMember(member);
        pointsBean.setCurrentPoints(100);
        pointsBean.setUpDateTime(LocalDateTime.now());
        pointsRepository.save(pointsBean);
        return savedMember;

    }

    // 完成會員註冊，填寫 password
    public MemberBean completeRegistration(Integer memberId, String password) {
        // 根據 memberId 查找會員資料
        MemberBean member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 設置 password
        member.setPassword(password); // 假設你有一個 setPassword 方法來處理密碼加密

        // 設置為已驗證
        member.setIsVerified(true);

        // 更新會員資料
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return member;
    }

    // 根據 email 查找會員
    public Optional<MemberBean> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // 新增儲存會員的方法
    public MemberBean saveMember(MemberBean memberBean) {
        return memberRepository.save(memberBean);
    }

    // 查詢已驗證過會員
    public MemberBean findOrCreateMember(MemberBean member) {
        Optional<MemberBean> existingMemberOpt = memberRepository.findById(member.getMemberId());
        if (existingMemberOpt.isPresent()) {
            MemberBean existingMember = existingMemberOpt.get();
            if (!existingMember.getIsVerified()) {
                throw new MemberNotVerifiedException("Member email not verified");
            }
            return existingMember; // 返回已存在且已驗證的會員
        } else {
            // 創建新會員時預設未驗證
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
            member.setIsVerified(false);
            return memberRepository.save(member); // 創建新會員
        }
    }

    // 查詢所有會員
    public Page<MemberBean> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable); // 使用 JPA 分頁功能
    }

    // 刪除會員
    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }

    // 刪除會員（軟刪除）
    public void softDeleteMember(Integer memberId) {
        Optional<MemberBean> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            MemberBean member = memberOpt.get();
            member.setStatus(MemberStatus.INACTIVE); // 設定狀態為 INACTIVE
            member.setUpdatedAt(LocalDateTime.now()); // 更新修改時間
            memberRepository.save(member);
        } else {
            throw new MemberNotFoundException("Member not found with id: " + memberId);
        }
    }

    // 恢復會員
    public void reactivateMember(Integer memberId) {
        Optional<MemberBean> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            MemberBean member = memberOpt.get();
            member.setStatus(MemberStatus.ACTIVE);
            member.setUpdatedAt(LocalDateTime.now());
            memberRepository.save(member);
        } else {
            throw new MemberNotFoundException("Member not found with id: " + memberId);
        }
    }

    // 更新會員資料 (這裡更新的是密碼)
    public MemberBean updateMember(Integer memberId, MemberBean updatedMember) {
        Optional<MemberBean> existingMemberOpt = memberRepository.findById(memberId);
        if (existingMemberOpt.isPresent()) {
            MemberBean existingMember = existingMemberOpt.get();

            // 僅更新密碼
            existingMember.setPassword(updatedMember.getPassword());
            existingMember.setUpdatedAt(updatedMember.getUpdatedAt()); // 更新時間

            // 儲存更新後的會員資料
            return memberRepository.save(existingMember);
        } else {
            throw new MemberNotFoundException("Member not found with id: " + memberId);
        }
    }

    public Optional<MemberBean> findMemberById(Integer memberId) {
        return memberRepository.findById(memberId);
    }

    public void verifyEmail(String token) {
        // 查找驗證碼資料
        Optional<VerificationEmailBean> verificationOpt = verificationEmailRepository.findByVerificationCode(token);

        if (verificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }

        VerificationEmailBean verification = verificationOpt.get();
        MemberBean member = verification.getMember();

        // 檢查驗證碼是否過期
        if (verification.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        // 標記會員為已驗證
        member.setIsVerified(true);
        member.setUpdatedAt(LocalDateTime.now());

        // 刪除已使用的驗證碼
        verificationEmailRepository.delete(verification);

        memberRepository.save(member);
    }

}
