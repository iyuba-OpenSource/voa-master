package com.iyuba.voa.utils; /**
 * Copyright 2014 Zhenguo Jin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static me.goldze.mvvmhabit.utils.StringUtils.isEmpty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 数据校验
 *
 * @author jingle1267@163.com
 */
public final class CheckUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private CheckUtils() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 邮箱检测
     *
     * @param email 可能是Email的字符串
     * @return 是否是Email
     */
    public static boolean isEmail(String email) {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 邮箱验证
     *
     * @param data 可能是Email的字符串
     * @return 是否是Email
     */
    public static boolean isEmail2(String data) {
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return data.matches(expr);
    }

    /**
     * 移动手机号码验证
     *
     * @param data 可能是手机号码字符串
     * @return 是否是手机号码
     */
    public static boolean isMobileNumber(final String data) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(data);
        b = m.matches();
        return b;
    }

    /**
     * 只含字母和数字
     *
     * @param data 可能只包含字母和数字的字符串
     * @return 是否只包含字母和数字
     */
    public static boolean isNumberLetter(String data) {
        String expr = "^[A-Za-z0-9]+$";
        return data.matches(expr);
    }

    /**
     * 只含数字
     *
     * @param data 可能只包含数字的字符串
     * @return 是否只包含数字
     */
    public static boolean isNumber(String data) {
        String expr = "^[0-9]+$";
        return data.matches(expr);
    }

    /**
     * 只含字母
     *
     * @param data 可能只包含字母的字符串
     * @return 是否只包含字母
     */
    public static boolean isLetter(String data) {
        String expr = "^[A-Za-z]+$";
        return data.matches(expr);
    }

    /**
     * 只是中文
     *
     * @param data 可能是中文的字符串
     * @return 是否只是中文
     */
    public static boolean isChinese(String data) {
        String expr = "^[\u0391-\uFFE5]+$";
        return data.matches(expr);
    }

    /**
     * 包含中文
     *
     * @param data 可能包含中文的字符串
     * @return 是否包含中文
     */
    public static boolean isContainChinese(String data) {
        String chinese = "[\u0391-\uFFE5]";
        if (isEmpty(data)) {
            for (int i = 0; i < data.length(); i++) {
                String temp = data.substring(i, i + 1);
                boolean flag = temp.matches(chinese);
                if (flag) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 小数点位数
     *
     * @param data   可能包含小数点的字符串
     * @param length 小数点后的长度
     * @return 是否小数点后有length位数字
     */
    public static boolean isDianWeiShu(String data, int length) {
        String expr = "^[1-9][0-9]+\\.[0-9]{" + length + "}$";
        return data.matches(expr);
    }

    /**
     * 身份证号码验证
     *
     * @param data 可能是身份证号码的字符串
     * @return 是否是身份证号码
     */
    public static boolean isCard(String data) {
        String expr = "^[0-9]{17}[0-9xX]$";
        return data.matches(expr);
    }

    /**
     * 邮政编码验证
     *
     * @param data 可能包含邮政编码的字符串
     * @return 是否是邮政编码
     */
    public static boolean isPostCode(String data) {
        String expr = "^[0-9]{6,10}";
        return data.matches(expr);
    }

    /**
     * 长度验证
     *
     * @param data   源字符串
     * @param length 期望长度
     * @return 是否是期望长度
     */
    public static boolean isLength(String data, int length) {

        return data != null && data.length() == length;
    }
}
