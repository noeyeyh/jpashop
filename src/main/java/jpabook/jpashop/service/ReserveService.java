package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Reserve;
import jpabook.jpashop.domain.ReserveItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.ReserveRepository;
import jpabook.jpashop.repository.ReserveSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long reserve(Long memberId, Long itemId, int count) {

        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        ReserveItem reserveItem = ReserveItem.createReserveItem(item, item.getPrice(), count);

        Reserve reserve = Reserve.createReserve(member, reserveItem);

        reserveRepository.save(reserve);

        return reserve.getId();
    }

    @Transactional
    public void cancelReserve(Long reserveId) {
        Reserve reserve = reserveRepository.findOne(reserveId);
        reserve.cancel();
    }

    public List<Reserve> findReserves(ReserveSearch reserveSearch) {
        return reserveRepository.findAllByString(reserveSearch);
    }
}