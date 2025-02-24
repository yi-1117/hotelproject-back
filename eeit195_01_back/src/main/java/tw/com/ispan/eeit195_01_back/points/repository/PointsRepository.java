package tw.com.ispan.eeit195_01_back.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;

public interface PointsRepository extends JpaRepository<PointsBean,Integer>{
    PointsBean findByMember(MemberBean member);

    PointsBean findByMember_MemberId(Integer memberId); // 使用 "Member_MemberId"    
}


