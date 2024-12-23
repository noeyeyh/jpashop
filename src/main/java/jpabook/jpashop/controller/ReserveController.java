package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Reserve;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ReserveSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/reserve")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "reserve/reserveForm";
    }

    @PostMapping(value = "/reserve")
    public String reserve(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        reserveService.reserve(memberId, itemId, count);
        return "redirect:/reserves";
    }

    @GetMapping(value = "/reserves")
    public String reserveList(@ModelAttribute("reserveSearch") ReserveSearch reserveSearch, Model model) {
        List<Reserve> reserves = reserveService.findReserves(reserveSearch);

        model.addAttribute("reserves", reserves);

        return "reserve/reserveList";
    }

    @PostMapping(value = "/reserves/{reserveId}/cancel")
    public String cancelReserve(@PathVariable("reserveId") Long reserveId) {
        reserveService.cancelReserve(reserveId);

        return "redirect:/reserves";
    }
}