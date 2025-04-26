package com.newpick4u.comment.comment.infrastructure.jpa;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class QueryDslUtil {

  private QueryDslUtil() {
    throw new RuntimeException("Utility Class");
  }


  public static OrderSpecifier<?>[] getAllOrderSpecifierArr(Pageable pageable, Path<?> path) {
    List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable, path);
    return orders.toArray(OrderSpecifier[]::new);
  }

  /**
   * 정렬 기준을 포함한 페이징 객체를 사용하여 QueryDSL 용 다중 기준 정렬 조건을 생성
   *
   * @param pageable : 페이징을 위한 객체
   * @param path     : 정렬 대상 QueryDSL Q객체
   * @return
   */
  public static List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable, Path<?> path) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();
    if (!isEmpty(pageable.getSort())) {
      for (Sort.Order order : pageable.getSort()) {
        Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

        OrderSpecifier<?> createdDate
            = QueryDslUtil.getSortedColumn(direction, path, order.getProperty());
        orders.add(createdDate);
      }
    }
    return orders;
  }

  /**
   * 특정 컬럼을 기준으로 정렬할 수 있도록 하는 QueryDSL 용 OrderSpecifier<?> 객체를 생성
   *
   * @param order
   * @param parent
   * @param fieldName
   * @return
   */
  public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
    Path<?> fieldPath = Expressions.path(Object.class, parent, fieldName);

    return new OrderSpecifier(order, fieldPath);
  }

  public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
    try {
      return new BooleanBuilder(f.get());
    } catch (Exception e) {
      return new BooleanBuilder();
    }
  }
}
