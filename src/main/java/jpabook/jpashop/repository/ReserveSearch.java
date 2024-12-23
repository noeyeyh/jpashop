package jpabook.jpashop.repository;

import jpabook.jpashop.domain.ReserveStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveSearch {
        private String memberName;

        private ReserveStatus reserveStatus;
    }

