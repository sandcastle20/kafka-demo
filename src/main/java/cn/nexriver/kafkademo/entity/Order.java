package cn.nexriver.kafkademo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName Order
 * @Description TODO
 * @Author shi.cq
 * @Date 2022/2/20
 */
@Data
@AllArgsConstructor
public class Order {

    private Long orderId;
    private int count;

}
