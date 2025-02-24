package tw.com.ispan.eeit195_01_back.room.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;
import tw.com.ispan.eeit195_01_back.room.dao.RoomOrderDAO;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, String>, RoomOrderDAO {
    List<RoomOrder> findByMember(MemberBean member);
}
