package tw.com.ispan.eeit195_01_back.points.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.repository.PointsRepository;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;

@Service
@Transactional
public class PointsService {
    @Autowired
    private PointsRepository pointsRepository;
    @Autowired
    private MemberRepository memberRepository;
    // @Autowired
    // private  MemberLevelsRespository memberLevelsRespository;
    @PersistenceContext
    private EntityManager entityManager;  

    //addPoints method
    public PointsBean addPoints(PointsBean pointsBean ,int pointsToAdd){
        if(pointsToAdd<=0){ 
            throw new IllegalArgumentException("增加的積分不能為零或負數");
        }
        // 從資料庫查詢 PointsBean，確保它存在
        PointsBean persistPointsBean = pointsRepository.findById(pointsBean.getPointsId())
        .orElseThrow(() -> new IllegalArgumentException("積分記錄不存在"));

        if (persistPointsBean == null) {
            return null;  // 找不到資料時，回傳 false
        };        
        MemberBean member = persistPointsBean.getMember();//獲取會員資料
        
        persistPointsBean.setCurrentPoints(persistPointsBean.getCurrentPoints()+pointsToAdd);
        persistPointsBean.setUpDateTime(LocalDateTime.now());
        //updateMemberLevel(pointsBean);

        pointsRepository.save(persistPointsBean);
        System.out.println("會員"+member+ "已增加積分"+pointsToAdd+"點，目前為"+persistPointsBean.getCurrentPoints()+"點");

        return persistPointsBean;
    };

    //reducePoints method
    public PointsBean reducePoints(PointsBean pointsBean,int pointsToReduce){
        if(pointsToReduce<=0){ 
            throw new IllegalArgumentException("減少的積分不能為零或負數");
        }
        if(pointsBean.getCurrentPoints()<pointsToReduce){
            throw new IllegalArgumentException("積分不足");
        }
        PointsBean persistPointsBean = pointsRepository.findById(pointsBean.getPointsId())
        .orElseThrow(() -> new IllegalArgumentException("積分記錄不存在"));;
        
        MemberBean member = persistPointsBean.getMember(); //獲取會員資料
        pointsBean.setCurrentPoints(persistPointsBean.getCurrentPoints()-pointsToReduce);
        persistPointsBean.setUpDateTime(LocalDateTime.now());
        pointsRepository.save(persistPointsBean);
        System.out.println("會員"+member+ "已減少積分"+pointsToReduce+"點，目前為"+persistPointsBean.getCurrentPoints()+"點");
        return persistPointsBean;
        
    }
    //getCurrentPoints method
    public Integer getCurrentPoints(Integer memberId){
        MemberBean member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

        PointsBean pointsBean = member.getPoints();
        if (pointsBean == null) {
            throw new IllegalArgumentException("該會員沒有對應的積分資料");
        }
        return pointsBean.getCurrentPoints();
    }
    //updateCurrentPoints method
    public PointsBean updatedCurrentPoints(PointsBean pointsBean,Integer updatedPoints){
        if (updatedPoints < 0) {
            throw new IllegalArgumentException("不能更新積分為負數");
        }
        pointsBean.setCurrentPoints(updatedPoints);
        pointsBean.setUpDateTime(LocalDateTime.now());
        MemberBean member = pointsBean.getMember(); //獲取會員資料
        pointsRepository.save(pointsBean);
        System.out.println("會員"+member+ "已更新至"+pointsBean.getCurrentPoints()+"點");
        return pointsBean;
    }

    //resetCurrentPointsToZero method
    public PointsBean resetCurrentPointsToZero (Integer memberId){

        MemberBean member = memberRepository.findById(memberId)
        .orElseThrow(()-> new IllegalArgumentException("會員不存在"));

        PointsBean pointsBean = pointsRepository.findByMember(member);
        pointsBean.setCurrentPoints(0);
        pointsBean.setUpDateTime(LocalDateTime.now());
        pointsRepository.save(pointsBean);
        return pointsBean;
    }

    // allPointReset method 
    public void allPointsReset(){
        List<PointsBean> allPointsBeans = pointsRepository.findAll(); // 獲取所有積分資料
        for(PointsBean pointsBean :allPointsBeans){
            pointsBean.setCurrentPoints(0);
            System.out.println("會員積分"+pointsBean.getPointsId()+"已重置");
            System.out.println(pointsBean);
        }
        pointsRepository.saveAll(allPointsBeans); // 批量更新
        System.out.println("所有會員(共"+allPointsBeans.size()+"筆)已回歸積分零");
    }
    // deletePoints method  MDgit不早講 跟本不用寫
    public boolean deletePoints(Integer memberId){
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if(member == null || memberId == null) throw new IllegalArgumentException("會員不存在");
        PointsBean pointsBean = member.getPoints();
        if(pointsBean == null || pointsBean.getPointsId() == null) throw new IllegalArgumentException("會員積分資料不存在");
        pointsRepository.delete(pointsBean);
        return true;
    }


    //earningPointsByOrder method
    public PointsBean earningPointsByRoomOrder (RoomOrder roomOrder , Double roomPointExchangeRate){
        //設定兌換率:先寫死
        roomPointExchangeRate = 1000.0;
        //檢查訂單是否為null
        if(roomOrder ==null || roomOrder.getTotalPayment() == null){
            throw new IllegalArgumentException("沒有訂單 或 無訂單金額");
        }
        PointsBean pointsBean =pointsRepository.findByMember(roomOrder.getMember());//從訂單找出會員
        if(pointsBean==null){ //如果會員不存在
            throw new IllegalArgumentException("找不到會員");
        }
        Integer increasedPoints = (int)Math.floor(roomOrder.getTotalPayment()/roomPointExchangeRate);
        PointsBean addedPointsBean =addPoints(pointsBean ,increasedPoints);
        return addedPointsBean;
    }

    //pointsRedemption method 積分兌換   exchangeDiscounts
    public Double pointsRedemption (RoomOrder roomOrder , Integer usedPoints ,boolean isUsedAllPoints){
        Double exchangeDiscountByPointsRate = 100.0;
        // 查找對應的 MemberBean
        MemberBean member = roomOrder.getMember();
        if(member == null) throw new IllegalArgumentException("會員不存在");

        PointsBean pointsBean = pointsRepository.findByMember(member);

        if(isUsedAllPoints) usedPoints = pointsBean.getCurrentPoints();

        if(usedPoints>pointsBean.getCurrentPoints()){
            throw new IllegalArgumentException("會員點數不足無法兌換");
        }

        Double discountAmount =usedPoints*exchangeDiscountByPointsRate;
        pointsBean.setCurrentPoints(pointsBean.getCurrentPoints()-usedPoints);
        pointsRepository.save(pointsBean);
        return discountAmount;
    }
}

// //tierPromotion method 升等系統  不能用
    //     // 設定積分升級的閾值
    //     private final int SILVER_LEVEL_THRESHOLD = 20;
    //     private final int GOLD_LEVEL_THRESHOLD = 50;
    //     private final int PLATINUM_LEVEL_THRESHOLD = 100;
    //     public void updateMemberLevel(PointsBean pointsBean) {
    //         Integer currentPoints = pointsBean.getCurrentPoints();
    //         Optional<MemberLevelsBean> memberLevelBeanOptional =memberLevelsRespository.findById(pointsBean.getMemberId());
    //         if(!memberLevelBeanOptional.isPresent()){
    //             throw new IllegalArgumentException("找不到會員");
    //         }
    //         MemberLevelsBean memberLevelBean =memberLevelBeanOptional.get();
    //         LevelBean levelBean = null;
    //         // 根據積分範圍設置會員等級
    //         if (currentPoints >= PLATINUM_LEVEL_THRESHOLD) {
    //         levelBean = new LevelBean("鉑金會員", "最高等級，享有全方位服務與優惠");
    //     } else if (currentPoints >= GOLD_LEVEL_THRESHOLD) {
    //         levelBean = new LevelBean("金卡會員", "高級會員，享有尊貴優惠");
    //     } else if (currentPoints >= SILVER_LEVEL_THRESHOLD) {
    //         levelBean = new LevelBean("銀卡會員", "中級會員，享有部分優惠");
    //     } else {
    //         levelBean = new LevelBean("普通會員", "基礎會員，享有基本服務");
    //     }
    //     // 設定會員等級
    //     memberLevelBean.setLevels(levelBean);
    //     memberLevelBean.setUpDateTime(LocalDateTime.now());
    //     // 保存會員等級信息
    //     memberLevelsRespository.save(memberLevelBean);
    //     }

// private boolean isScheduled = false; // 用來避免多次調用重複排程  //難產的排程功能
    // //periodic allPointReset method  //難產的排程功能
    // public void periodicPointReset(LocalDateTime targetDateTime){
    //     // 取得當前&目標時間
    //     LocalDateTime currentTime = LocalDateTime.now();
    //     //設成指定時間  管理者設定指定時間
    //         // int currentYear =currentTime.getYear();
    //         // LocalDateTime targetDateTime = LocalDateTime.of(currentYear, Month.APRIL, 1, 12, 0, 0); //利用.of來創造物件
    //     // // 如果目標時間已經過了，則設定為下一年的目標時間
    //     if (currentTime.isAfter(targetDateTime)) {
    //         targetDateTime = targetDateTime.plusYears(1);
    //     }
    //     // }
    //     // 計算從當前時間到目標時間還要多久（毫秒）
    //     long delayMillis = Math.max(0, Duration.between(currentTime, targetDateTime).toMillis());
    //     // 創建一個 ScheduledExecutorService 專門用來執行定期任務。
    //     if (isScheduled) {
    //         System.out.println("已經有排程執行中，無需再次排程");
    //         return;
    //     }
    //     ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    //     // 設定任務
    //     Runnable task = () -> {
    //         try {
    //             System.out.println("週期重置積分任務已觸發: " + LocalDateTime.now());
    //             allPointsReset();
    //             isScheduled = false; // 重置排程狀態
    //             System.out.println("重置任務執行完畢");
    //         } catch (Exception e) {
    //             System.err.println("任務執行時出現錯誤：" + e.getMessage());
    //             e.printStackTrace();
    //         }
    //     };
    //     // 計算每年間隔的天數，使用 ChronoUnit.DAYS 避免閏年問題：計算從當年的 4 月 1 日 12:00:00 到下一年同一天 12:00:00 之間的天數。這樣能夠避免閏年導致的問題，確保間隔天數是準確的。
    //     long periodMillis = Duration.between(targetDateTime, targetDateTime.plusYears(1)).toMillis();
    //     // 排程任務，每年執行一次
    //     System.out.println("將於 " + targetDateTime + " 觸發任務");
    //     // delayMillis 是第一次執行任務的延遲時間，會從當前時間計算出第一次執行的時間。
    //     // periodMillis 是任務之間的間隔時間，每次任務完成後，會根據這個間隔再次執行。
    //     scheduler.scheduleAtFixedRate(task, delayMillis, periodMillis, TimeUnit.MILLISECONDS); //無限期執行 除非使用shutdown方法。
    //     isScheduled = true;
    //     System.out.println("任務已排程，將於 " + targetDateTime + " 觸發，週期為每年一次");
    //     // scheduler.shutdown();
    // }




