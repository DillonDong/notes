package com.itheima.exception;

import com.itheima.constant.ShopCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常抛出类
 */
@Slf4j
public class CastException {
    public static void cast(ShopCode shopCode) {
        log.error(shopCode.toString());
        throw new CustomerException(shopCode);
    }
}
