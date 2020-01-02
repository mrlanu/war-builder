package io.lanu.travian.warbuilder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order
{
    private String customerName;
    private String deliveryAddress;
    private Date date;
    private List<Item> orderItems;
}
