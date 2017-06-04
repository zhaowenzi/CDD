package com.zzw.cdd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by 子文 on 2017/5/25.
 */

public class Desk {
    public static int winId = -1;       //赢家的ID
    Bitmap cardImg;
    Bitmap redoImage;
    Bitmap passImage;
    Bitmap chupaiImage;
    Bitmap tishiImage;
    Bitmap farmerImage;
    Bitmap landlordImage;
    Context context;
    private int[] scores = new int[4];
    private int[][] timeLimitePosition = {{130, 190}, {80, 80}, {360, 80}, {150,80}}; //剩余时间View的位置
    private int[][] passPosition = {{130, 190}, {80, 80}, {360, 80}, {150,80}};
    private int[][] playerLatestCardsPosition = {{130, 140}, {80, 60}, {360, 60},{250, 60}};
    private int[][] playerCardsPosition = {{30, 210}, {30, 60}, {410, 60}, {200,60}};
    private int[][] scorePosition = {{70, 290}, {70, 30}, {340, 30}, {240,30}};
    private int[][] iconPosition = {{30, 270}, {30, 10}, {410, 10}, {200,10}};
    private int buttonPosition_X = 240;
    private int buttonPosition_Y = 160;
    private boolean[] canPass = new boolean[4];
    private int[][] playerCards = new int[4][13];
    private boolean canDrawLatestCards = false;
    private int[] allCards = new int[52];// 一副扑克牌
    private int currentScore = 10;// 当前分数
    private int currentId = 0;// 当前操作的人
    private int currentCircle = 0;// 本轮次数
    public static CardsHolder cardsOnDesktop = null;// 最新的一手牌
    private int timeLimite = 300;
    //存储胜负得分信息
    private int result[] = new int[4];
    /**
     * -1:重新开始 0:游戏中 1:本局结束
     */
    private int op = -1;  //控制游戏进度
    public static Player[] players = new Player[4];  //四个玩家
    public static int multiple = 1;   //当前倍数
    public static int boss = 0;  //第1个出牌的人
    public boolean ifClickChupai = false;

    public Desk(Context context) {
        this.context = context;
        redoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_redo);
        passImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_pass);
        chupaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_chupai);
        tishiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tishi);
        farmerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_farmer);
        landlordImage = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_landlord);
    }

    public void gameLogic() {
        switch (op) {
            case -1:
                init();
                op = 0;
                break;
            case 0:
                checkGameOver();
                break;
            case 1:
                break;
        }
    }

    public void controlPaint(Canvas canvas) {
        switch (op) {
            case -1:
                break;
            case 0:
                paintGaming(canvas);
                break;
            case 1:
                paintResult(canvas);
                break;
        }
    }
    private void checkGameOver() {
        for(int k = 0; k < 4; k++) {
            //当三个人中其中一个人牌的数量为0，则游戏结束
            if(players[k].cards.length == 0) {
                //切换到游戏结束状态
                op = 1;
                //得到最先出去的人的id
                winId = k;
                //判断哪方获胜
                for(int i = 0; i < 4; i++) {
                    if(i == k) {
                        result[i] = currentScore * multiple;
                        scores[i] += currentScore * multiple;
                    }
                    else {
                        //其他人需要减分
                        result[i] = -currentScore * multiple;
                        scores[i] -= currentScore * multiple;
                    }
                }
                return;
            }
        }
        //游戏没有结束，结束。
        //如果轮到电脑出来
        if(currentId == 1 || currentId == 2 || currentId == 3) {
            if(timeLimite <= 300 && timeLimite >= 0) {
                //获取手中的牌中能够打过当前手牌
                CardsHolder tempcard = players[currentId].chupaiAI(cardsOnDesktop);
                if(tempcard != null) {
                    //手中有大过的牌，则出
                    cardsOnDesktop = tempcard;
                    nextPerson();
                }
                else{
                    //没有打过的牌，则不要
                    buyao();
                }
            }
        }
        if(currentId == 0) {
            if(timeLimite <= 300 && timeLimite >= 0) {
                if(ifClickChupai == true) {
                    CardsHolder card = players[0].chupai(cardsOnDesktop);
                    if(card != null) {
                        cardsOnDesktop = card;
                        nextPerson();
                    }
                    ifClickChupai = false;
                }
            }
            else {
                if(currentCircle != 0) {
                    buyao();
                }
                else {
                    CardsHolder autoCard = players[currentId].chupaiAI(cardsOnDesktop);
                    cardsOnDesktop = autoCard;
                    nextPerson();
                }
            }
        }
        //时间倒计时
        timeLimite -= 2;
        canDrawLatestCards = true;
    }
    //初始化游戏
    public void init() {
        allCards = new int[52];
        playerCards = new int[4][13];
        winId = -1;
        currentScore = 3;
        multiple = 1;
        cardsOnDesktop = null;
        currentCircle = 0;
        currentId = 0;
        for(int i = 0; i < 4; i++) {
            scores[i] = 50;
        }
        for(int i = 0; i < 4; i++) {
            canPass[i] = false;
        }
        for(int i = 0; i < allCards.length; i++) {
            allCards[i] = i;
        }
        CardsManager.shuffle(allCards);
        fapai(allCards);
        chooseBoss();
        CardsManager.sort(playerCards[0]);
        CardsManager.sort(playerCards[1]);
        CardsManager.sort(playerCards[2]);
        CardsManager.sort(playerCards[3]);
        players[0] = new Player(playerCards[0], playerCardsPosition[0][0],
                playerCardsPosition[0][1], CardsType.direction_Horizontal, 0, this, context);
        players[1] = new Player(playerCards[1], playerCardsPosition[1][0],
                playerCardsPosition[1][1], CardsType.direction_Vertical, 1, this, context);
        players[2] = new Player(playerCards[2], playerCardsPosition[2][0],
                playerCardsPosition[2][1], CardsType.direction_Vertical, 2, this, context);
        players[3] = new Player(playerCards[3], playerCardsPosition[3][0],
                playerCardsPosition[3][1], CardsType.direction_Vertical, 3, this, context);
        players[0].setLastAndNext(players[1], players[3]);
        players[1].setLastAndNext(players[2], players[0]);
        players[2].setLastAndNext(players[3], players[1]);
        players[3].setLastAndNext(players[0], players[2]);
    }
    public void fapai(int[] cards) {
        for(int i = 0; i < 52; i++) {
            playerCards[i / 13][i % 13] = cards[i];
        }
    }
    private void chooseBoss() {
        //选择有一个方片3的第一个出牌
    }
    //不要牌的操作
    private void buyao() {
        //清空当前不要牌的人的最后一手牌
        players[currentId].latestCards = null;
        canPass[currentId] = true;
        //定位下一个人的id
        nextPerson();
        //如果已经转回来，则该人继续出牌，本轮清空，新一轮开始
        if(cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
            currentCircle = 0;
            cardsOnDesktop = null;  //轮回到最大牌的那个人再出牌
            players[currentId].latestCards = null;
        }
    }
    private void nextPerson() {
        switch (currentId) {
            case 0:
                currentId = 3;
                break;
            case 1:
                currentId = 0;
                break;
            case 2:
                currentId = 1;
                break;
            case 3:
                currentId = 2;
                break;
        }
        currentCircle++;
        timeLimite = 300;
    }

    //绘制游戏画面
    private void paintGaming(Canvas canvas) {
        players[0].paint(canvas);
        players[1].paint(canvas);
        players[2].paint(canvas);
        players[3].paint(canvas);
        paintIconAndScore(canvas);
        paintTimeLimite(canvas);

        //如果轮到本人，画“不要”“出牌”“重新开始”按钮
        if(currentId == 0) {
            Rect src = new Rect();
            Rect dst = new Rect();

            src.set(0, 0, chupaiImage.getWidth(), chupaiImage.getHeight());
            dst.set((int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(chupaiImage, src, dst, null);

            if(currentCircle != 0) {
                src.set(0, 0, passImage.getWidth(), passImage.getHeight());
                dst.set((int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                        (int) ((buttonPosition_X) * MainActivity.SCALE_HORIAONTAL),
                        (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
                canvas.drawBitmap(passImage, src, dst, null);
            }

            src.set(0, 0, redoImage.getWidth(), redoImage.getHeight());
            dst.set((int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y) * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(redoImage, src, dst, null);

            src.set(0, 0, tishiImage.getWidth(), tishiImage.getHeight());
            dst.set((int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y) * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 240) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(tishiImage, src, dst, null);
        }

        //画各自刚出的牌或“不要”
        for(int i = 0; i < 4; i++) {
            if (currentId != i && players[i].latestCards != null && canDrawLatestCards == true) {
                players[i].latestCards.paint(canvas, playerLatestCardsPosition[i][0],
                        playerLatestCardsPosition[i][1], players[i].paintDirection);
            }
            if (currentId != i && players[i].latestCards == null && canPass[i] == true) {
                paintPass(canvas, i);
            }
        }
    }
    // 画倒计时
    private void paintTimeLimite(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 3; i++) {
            if (i == currentId) {
                canvas.drawText("" + (timeLimite / 10),
                        (int) (timeLimitePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (timeLimitePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
            }
        }
    }

    // 画“不要”二字
    private void paintPass(Canvas canvas, int id) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("不要", (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (passPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);

    }

    // 画游戏中的分数
    private void paintIconAndScore(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        Rect src = new Rect();
        Rect dst = new Rect();
        for (int i = 0; i < 4; i++) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            src.set(0, 0, landlordImage.getWidth(), landlordImage.getHeight());
            dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
                    (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
            RectF rectF = new RectF(dst);
            canvas.drawRoundRect(rectF, 5, 5, paint);
            canvas.drawBitmap(landlordImage, src, dst, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawText("玩家" + i,
                    (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
            canvas.drawText("得分：" + scores[i],
                    (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText("当前底分：" + currentScore + "  当前倍数：" + multiple,
                (int) (150 * MainActivity.SCALE_HORIAONTAL),
                (int) (150 * MainActivity.SCALE_VERTICAL), paint);
    }

    // 画游戏结束时的分数和各自剩余牌
    private void paintResult(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (20 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 4; i++) {
            canvas.drawText("玩家" + i + ":本局得分:" + result[i] + "   总分：" + scores[i],
                    (int) (110 * MainActivity.SCALE_HORIAONTAL),
                    (int) ((96 + i * 30) * MainActivity.SCALE_VERTICAL), paint);
        }
        for (int i = 0; i < 4; i++) {
            players[i].paintResultCards(canvas);
        }
    }
    public void restart() {
        op = 1;
    }

    // 触屏的处理
    public void onTuch(int x, int y) {
        // for (int i = 0; i < players.length; i++) {
        // StringBuffer sb = new StringBuffer();
        // sb.append(i + " : ");
        // for (int j = 0; j < players[i].cards.length; j++) {
        // sb.append(players[i].cards[j] + (players[i].cards[j] >= 10 ? "" :
        // " ") + ",");
        // }
        // System.out.println(sb.toString());
        // }

        // 若游戏结束，则点击任意屏幕重新开始
        if (op == 1) {
            op = -1;
        }
        players[0].onTuch(x, y);
        if (currentId == 0) {

            if (CardsManager.inRect(x, y, (int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("出牌");
                ifClickChupai = true;

            }
            if (currentCircle != 0) {
                if (CardsManager.inRect(x, y,
                        (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                        (int) (80 * MainActivity.SCALE_HORIAONTAL),
                        (int) (40 * MainActivity.SCALE_VERTICAL))) {
                    System.out.println("不要");
                    buyao();
                }
            }
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("重选");
                players[0].redo();
            }
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("提示（重新）");
                restart();
            }
        }
    }
}

