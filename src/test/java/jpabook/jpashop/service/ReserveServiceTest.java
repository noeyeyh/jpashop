package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Reserve;
import jpabook.jpashop.domain.ReserveStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ReserveRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
@RunWith(SpringRunner.class)
@SpringBootTest

@Transactional
public class ReserveServiceTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    ReserveService reserveService;
    @Autowired
    ReserveRepository reserveRepository;
    @Test
    public void 영화예매() throws Exception {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고
        int reserveCount = 2;

        //When
        Long reserveId = reserveService.reserve(member.getId(), item.getId(), reserveCount);

        //Then
        Reserve getReserve = reserveRepository.findOne(reserveId);
        assertEquals("상품 주문시 상태는 RESERVE", ReserveStatus.RESERVE, getReserve.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.",1, getReserve.getReserveItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * 2, getReserve.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",8, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 영화예매_남은좌석수초과() throws Exception {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int reserveCount = 11;

        //When
        reserveService.reserve(member.getId(), item.getId(), reserveCount);

        //Then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    @Test
    public void 예매취소() {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int reserveCount = 2;
        Long reserveId = reserveService.reserve(member.getId(), item.getId(), reserveCount);

        //When
        reserveService.cancelReserve(reserveId);

        //Then
        Reserve getReserve = reserveRepository.findOne(reserveId);
        assertEquals("주문 취소시 상태는 CANCEL이다.", ReserveStatus.CANCEL, getReserve.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}