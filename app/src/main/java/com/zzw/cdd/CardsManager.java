package com.zzw.cdd;

import java.security.SignatureException;
import java.util.Random;
import java.util.Vector;

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
    //返回的int分别对应的花色为黑桃、红心、梅花、方片
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
        if(getCardNumber(cards[0]) == 15
                && getCardNumber(cards[1]) == 14
                && getCardNumber(cards[2]) == 5
                && getCardNumber(cards[3]) == 4
                && getCardNumber(cards[4]) == 3)
            return true;

        if(getCardNumber(cards[0]) == 15
                && getCardNumber(cards[1]) == 6
                && getCardNumber(cards[2]) == 5
                && getCardNumber(cards[3]) == 4
                && getCardNumber(cards[4]) == 3)
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

    //返回的Int值包含花色和数值，后面处理一定要加上getCardNumber()和getImageCol()
    public static int getValue(int[] cards) {
        //TODO Auto-generated method stub
        int type;
        type = getType(cards);
        if(type == CardsType.danzhang)
            return cards[0];
        if(type == CardsType.yidui)
            return cards[1];
        if(type == CardsType.sange)
            return cards[2];
        //2在顺子中最为小牌；A和2在一起做顺时，做小牌处理，其余做打牌处理
        if(type == CardsType.zashun || type == CardsType.tonghuashun){
            if(getCardNumber(cards[0]) == 15){
                if(getCardNumber(cards[1]) == 14){
                    return cards[2];
                }
                else{
                    return cards[1];
                }
            }else {
                return cards[0];
            }
        }
        if(type == CardsType.tonghuawu)
            return cards[0];
        if(type == CardsType.sangedaiyidui){
            if(getCardNumber(cards[0]) == getCardNumber(cards[2])) {
                return cards[2];
            }else {
                return cards[4];
            }
        }
        if(type == CardsType.sigedaidanzhang) {
            if(getCardNumber(cards[0]) == getCardNumber(cards[3])) {
                return cards[3];
            }
            else{
                return cards[4];
            }
        }
        return 0;
    }

    /**
     * 是不是一个有效的牌型
     *
     * @param cards
     * @return
     */
    public static boolean isCard(int[] cards) {
        if(getType(cards) == CardsType.error)
            return false;
        return true;
    }

    /**
     * 返回true 前者牌大
     *
     * @param f
     * @param s
     * @return
     */
    public static int compare(CardsHolder f, CardsHolder s) {
        if(f.cards.length != s.cards.length)
            return -1;
        if(getCardNumber(f.value) > getCardNumber(s.value)) {
            return 1;
        }
        if(getCardNumber(f.value) == getCardNumber(s.value)) {
            if(getImageCol(f.value) < getImageCol(s.value)){
                return 1;
            }
            if(getImageCol(f.value) > getImageCol(s.value)){
                return 0;
            }
        }
        if(getCardNumber(f.value) < getCardNumber(s.value)) {
            return 0;
        }
        return -1;
    }
    public static int[] outCardByItself(int cards[], Player last, Player next) {
        CardsAnalyzer analyzer = CardsAnalyzer.getInstance();
        analyzer.setPokes(cards);
        Vector<int[]> card_danzhang = analyzer.getCard_danzhang();
        if(card_danzhang.size() > 0) {
            return card_danzhang.elementAt(0);
        }
        return new int[]{cards[0]};
    }

    //出牌智能
    public static int[] findTheRightCard(CardsHolder card, int cards[], Player last, Player next) {
        CardsAnalyzer cardsAnalyzer = CardsAnalyzer.getInstance();
        cardsAnalyzer.setPokes(cards);
        return findBigerCards(card, cards);
    }


    public static int[] findBigerCards(CardsHolder card, int cards[]) {
        try{
            //获取card的信息，牌值，牌型
            int[] cardPokes = card.cards;
            //此处牌的value包含了值和花色两个值
            int cardValue = card.value;
            int cardType = card.cardsType;
            int cardLength = cardPokes.length;
            //使用AnalyzerPoke来对牌进行分析
            CardsAnalyzer analyzer = CardsAnalyzer.getInstance();
            analyzer.setPokes(cards);

            Vector<int[]> temp;
            int size = 0;
            //根据适当牌型选取适当牌
            switch (cardType) {
                case CardsType.danzhang:
                    temp = analyzer.getCard_danzhang();
                    size = temp.size();
                    for(int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        int h = CardsManager.getImageCol(cardArray[0]);
                        if(v > getCardNumber(cardValue)){
                            return cardArray;
                        }
                        if(v == getCardNumber(cardValue) && h < getImageCol(cardValue)){
                            return cardArray;
                        }
                    }
                    break;
                case CardsType.yidui:
                    temp = analyzer.getCard_yidui();
                    size = temp.size();
                    for(int i = 0; i < size; i++){
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[1]);
                        int h = CardsManager.getImageCol(cardArray[1]);
                        if(v > getCardNumber(cardValue)) {
                            return cardArray;
                        }
                        if(v == getCardNumber(cardValue) && h < getImageCol(cardValue)){
                            return cardArray;
                        }
                    }
                    break;
                case CardsType.sange:
                    temp = analyzer.getCard_sange();
                    size = temp.size();
                    for(int i = 0; i < size; i++){
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        int h = CardsManager.getImageCol(cardArray[2]);
                        if(v > getCardNumber(cardValue)) {
                            return cardArray;
                        }
                        if(v == getCardNumber(cardValue) && h < getImageCol(cardValue)) {
                            return cardArray;
                        }
                    }
                    break;
                case CardsType.zashun:
                case CardsType.tonghuashun:
                case CardsType.tonghuawu:
                    temp = analyzer.getCard_zashun();
                    size = temp.size();
                    for(int i = 0; i < size; i++){
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(CardsManager.getValue(cardArray));
                        int h = CardsManager.getImageCol(CardsManager.getValue(cardArray));
                        if(v > getCardNumber(cardValue)) {
                            return cardArray;
                        }
                        if(v == getCardNumber(cardValue) && h < getImageCol(cardValue)) {
                            return cardArray;
                        }
                    }
                    break;
                case CardsType.sigedaidanzhang:
                    if(cards.length < 5) {
                        break;
                    }
                    temp = analyzer.getCard_sige();
                    size = temp.size();
                    for(int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[3]);
                        int h = CardsManager.getImageCol(cardArray[3]);
                        if(v > getCardNumber(cardValue)){
                            int[] sidaiyi = new int[5];
                            for(int j = 0; j < cardArray.length; j++){
                                sidaiyi[j] = cardArray[j];
                            }
                            temp = analyzer.getCard_danzhang();
                            size = temp.size();
                            if(size > 0){
                                int[] t = temp.get(0);
                                sidaiyi[4] = t[0];
                                return sidaiyi;
                            }
                        }
                        if(v == getCardNumber(cardValue) && h < getImageCol(cardValue)){
                            int[] sidaiyi = new int[5];
                            for(int j = 0; j < cardArray.length; j++){
                                sidaiyi[j] = cardArray[j];
                            }
                            temp = analyzer.getCard_danzhang();
                            size = temp.size();
                            if(size > 0){
                                int[] t = temp.get(0);
                                sidaiyi[4] = t[0];
                                return sidaiyi;
                            }
                        }
                    }
                    break;
                case CardsType.sangedaiyidui:
                    if(cards.length < 5) {
                        break;
                    }
                    temp = analyzer.getCard_sange();
                    size = temp.size();
                    for(int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[2]);
                        int h = CardsManager.getImageCol(cardArray[2]);

                    }


            }

        }
    }
}
