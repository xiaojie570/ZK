package com.fj.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.cglib.core.Local;
import org.springframework.format.number.CurrencyFormatter;
import sun.util.locale.LocaleUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by lenovo on 2019/1/5.
 */
public class FormatterTest {
    @Test
    public void test1() throws ParseException {
        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        currencyFormatter.setFractionDigits(2);    // 保留小数点后几位
        currencyFormatter.setRoundingMode(RoundingMode.CEILING);    // 舍入模式(celling表示四舍五入)
        Assert.assertEquals(new BigDecimal("123.13"),currencyFormatter.parse("$123.125", Locale.US));    // 将带货币符号的字符串“$123.125”转换为BigDecimal（"123.00")

    }

    @Test
    public void test2() throws ParseException {
        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        currencyFormatter.setFractionDigits(2);
        currencyFormatter.setRoundingMode(RoundingMode.CEILING);
        // 将BigDecimal("123.00")格式转化为字符串“$123.00”展示
        Assert.assertEquals("$123.00",currencyFormatter.print(new BigDecimal("123"), Locale.US));
    }
}
