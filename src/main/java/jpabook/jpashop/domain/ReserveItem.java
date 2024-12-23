package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ReserveItem {

    @Id @GeneratedValue
    @Column(name = "reserve_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_id")
    private Reserve reserve;

    private int reservePrice;
    private int count;

    public static ReserveItem createReserveItem(Item item, int reservePrice, int count) {
        ReserveItem reserveItem = new ReserveItem();
        reserveItem.setItem(item);
        reserveItem.setReservePrice(reservePrice);
        reserveItem.setCount(count);

        item.removeStock(count);
        return reserveItem;
    }

    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getReservePrice() * getCount();
    }
}