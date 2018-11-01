package com.roger.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单编号生成器
 */
@Component
public class OrderNumGen {

    private static int count = 0;

    private static final String DATA_FORMAT = "yyyy-MM-dd-HH-mm-ss";

    public String getOrderNumber(){
        String prefix = new SimpleDateFormat(DATA_FORMAT).format(new Date());
        return prefix + "-" + (++count);
    }
}
