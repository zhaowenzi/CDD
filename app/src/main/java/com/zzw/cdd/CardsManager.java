package com.zzw.cdd;

/**
 * Created by 子文 on 2017/5/24.
 */

public class CardsManager {
    public static boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        // 必须要有等号，否则触摸在相邻牌的同一边上会出错
        if (x <= rectX || x >= rectX + rectW || y <= rectY || y >= rectY + rectH) {
            return false;
        }
        return true;
    }
}
