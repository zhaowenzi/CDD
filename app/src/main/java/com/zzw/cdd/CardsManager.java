package com.zzw.cdd;

import java.util.Random;

/**
 * Created by 子文 on 2017/5/24.
 */

public class CardsManager {
    public static Random rand = new Random();
    public static boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        // 必须要有等号，否则触摸在相邻牌的同一边上会出错
        if (x <= rectX || x >= rectX + rectW || y <= rectY || y >= rectY + rectH) {
            return false;
        }
        return true;
    }

    // 洗牌，cards[0]~cards[52]表示52张牌:3333444455556666...KKKKAAAA2222
    public static void shuffle(int[] cards) {
        int len = cards.length;
        // 对于52张牌中的任何一张，都随机找一张和它互换，将牌顺序打乱。
        for (int l = 0; l < len; l++) {
            int des = rand.nextInt(52);
            int temp = cards[l];
            cards[l] = cards[des];
            cards[des] = temp;
        }
    }

    //返回有方片3的那位玩家的id，第一个出牌
//    public static int getBoss() {
//
//    }

    //对牌进行从大到小排序，冒泡排序
    public static void sort(int[] cards) {
        for(int i = 0; i < cards.length; i++) {
            for(int j = i + 1; i < cards.length; j++) {
                if(cards[i] < cards[j]) {
                    int temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                }
            }
        }
    }
    public static int getImageRow(int poke) {
        return poke / 4;
    }
    public static int getImageCol(int poke) {
        return poke % 4;
    }

    //获取某张牌的大小
    public static int getCardNumber(int card) {
        return getImageRow(card) + 3;
    }

    /*
    牌型说明：
    1、单张：任何一张单牌
    2、一对：二张牌点相同的牌
    3、三个：三张牌点相同的牌
    4、顺：连续五张牌点相邻的牌，如“34567”“910JQK”“10JQKA”“A2345”等，顺的张数必须是五张，A既可在顺的最后，
    也可在顺的最前，但不能在顺的中间，如“JQKA2”不是顺
    5、杂顺：花色不全部相同的牌称为杂顺
    6、同花顺：每张牌的花色都相同的顺称为同花顺
    7、同花五：由相同花色的五张牌组成，但不是顺，称“同花五”，如红桃“278JK”
    8、三个带一对：例如：99955
    9、四个带单张：例如：99995
     */

    //判断是否为顺
    public static boolean isShun(int[] cards) {
        int start = getCardNumber(cards[0]);
        if(cards[0] == 5 && cards[1] == 4 && cards[2] == 3 && cards[3] == 15 && cards[4] == 14)
            return true;
        //单顺最大一张不能大于A
        if(start > 14) {
            return false;
        }
        int next;
        for(int i = 1; i < cards.length; i++) {
            next = getCardNumber(cards[i]);
            if (start - next != 1) {
                return false;
            }
            start = next;
        }
        return true;
   }

   //判断是否为杂顺
    public static boolean isZaShun(int[] cards) {
        if (!isShun(cards))
            return false;
        for (int i = 0; i < cards.length - 1; i++) {
            if (getImageCol(cards[i]) == getImageCol(cards[i + 1]))
                return false;
        }
        return true;
    }

    //判断是否为同花顺
    public static boolean isTongHuaShun(int[] cards) {
        if(!isShun(cards))
            return false;
        for(int i = 0; i < cards.length - 1; i++) {
            if(getImageCol(cards[i]) != getImageCol(cards[i + 1]));
                return false;
        }
        return true;
    }

    //判断是否为同花五
    public static boolean isTongHuaWu(int[] cards) {
        for(int i = 0; i < cards.length - 1; i++) {
            if(getImageCol(cards[i]) != getImageCol(cards[i + 1]))
                return false;
        }
        return true;
    }


    //判断基本牌型，判断时已从大到小排序
    public static int getType(int[] cards) {
        // TODO Auto-generated method stub
        int len = cards.length;

        //当牌数量为1时，单牌
        if(len == 1) {
            return CardsType.danzhang;
        }

        //当牌数量为2是，一对
        if(len == 2) {
            if(getCardNumber(cards[0]) == getCardNumber(cards[1])) {
                return CardsType.yidui;
            }
        }

        // 当牌数为3时,三个
        if (len == 3) {
            if (getCardNumber(cards[0]) == getCardNumber(cards[2])) {
                return CardsType.sange;
            }
        }

        //当排数为5时，可能为顺、杂顺、同花顺、同花五、三带一对、四带一张、同花五
        if(len == 5) {
            if (isTongHuaShun(cards))
                return CardsType.tonghuashun;
            if (isZaShun(cards))
                return CardsType.zashun;
            if (isShun(cards))
                return CardsType.shun;
            if(getCardNumber(cards[0]) == getCardNumber(cards[2])
                    && getCardNumber(cards[3]) == getCardNumber(cards[4])) {
                return CardsType.sangedaiyidui;
            }
            if(isTongHuaWu(cards))
                return CardsType.tonghuawu;
            if (getCardNumber(cards[0]) == getCardNumber(cards[1])
                    && getCardNumber(cards[2]) == getCardNumber(cards[4])) {
                return CardsType.sangedaiyidui;
            }
            if(getCardNumber(cards[0]) == getCardNumber(cards[3])
                    || getCardNumber(cards[1]) == getCardNumber(cards[4]))
                return CardsType.sigedaidanzhang;
        }
        return CardsType.error;
    }
}
