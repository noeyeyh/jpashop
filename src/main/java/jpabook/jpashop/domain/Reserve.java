package jpabook.jpashop.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reserves")
@Getter @Setter
public class Reserve {

    @Id
    @GeneratedValue
    @Column(name="reserve_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reserve",cascade = CascadeType.ALL)
    private List<ReserveItem> reserveItems= new ArrayList<>();

    private LocalDateTime reserveDate;

    @Enumerated(EnumType.STRING)
    private ReserveStatus status;

    public void setMember(Member member) {
        this.member = member;
        member.getReserves().add(this);
    }

    public void addReserveItem(ReserveItem reserveItem) {
        reserveItems.add(reserveItem);
        reserveItem.setReserve(this);
    }

    public static Reserve createReserve(Member member, ReserveItem... reserveItems) {
        Reserve reserve = new Reserve();
        reserve.setMember(member);
        for (ReserveItem reserveItem : reserveItems) {
            reserve.addReserveItem(reserveItem);
        }
        reserve.setStatus(ReserveStatus.RESERVE);
        reserve.setReserveDate(LocalDateTime.now());
        return reserve;
    }

    public void cancel(){
        this.setStatus(ReserveStatus.CANCEL);
        for (ReserveItem reserveItem : reserveItems) {
            reserveItem.cancel();
        }
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for (ReserveItem reserveItem : reserveItems) {
            totalPrice += reserveItem.getTotalPrice();
        }
        return totalPrice;
    }
}