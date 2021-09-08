package com.zetcode;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Board extends JPanel {

    private String Score = "Score:";
    private int ballcount = Commons.ballCounter;
    private int scoreCounter = 0;
    private String scoreString = String.valueOf(scoreCounter);
    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Ball ball2;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;

    public String paddleSound;
    public String filePath;
    public String scoreUp;
    public String filePath2;
    public main.SimpleAudioPlayer hittingBall;
    public main.SimpleAudioPlayer backgroundSong;
    public main.SimpleAudioPlayer coin;
    public main.SimpleAudioPlayer yay;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));

        gameInit();
    }

    private void gameInit() {

        bricks = new Brick[Commons.N_OF_BRICKS];

        ball = new Ball();
        ball.initBall(230,335);
        ball2 = new Ball();
        ball2.initBall(200,335);
        paddle = new Paddle();

        int k = 0;


        //TODO:
        //initialize ALL the new bricks here

        for (int i = 0; i < 5; i++) {

            for (int j = 0; j < 6; j++) {

                bricks[k] = new Brick(i * 40 + 50, j * 10 + 50);
                k++;

            }
        }

        try{
            filePath = "src/resources/backgroundSong.wav";
            backgroundSong = new main.SimpleAudioPlayer(filePath);
            backgroundSong.play();
        }catch(Exception ex){
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }


        timer = new Timer(Commons.PERIOD, new GameCycle());
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (inGame) {

            drawObjects(g2d);

            Toolkit.getDefaultToolkit().sync();
            Font small = new Font("Comic Sans", Font.BOLD, 14);
            g.setColor(Color.black);
            g.setFont(small);
            g.drawString(scoreString, 57, 15);
            g.drawString(Score, 2, 15);


        } else {

            gameFinished(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics2D g2d) {

        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getImageWidth(), ball.getImageHeight(), this);
        g2d.drawImage(ball2.getImage(), ball2.getX(), ball2.getY(),
                ball2.getImageWidth(), ball2.getImageHeight(), this);
        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getImageWidth(), paddle.getImageHeight(), this);

        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {

            if (!bricks[i].isDestroyed()) {

                g2d.drawImage(bricks[i].getImage(), bricks[i].getX(),
                        bricks[i].getY(), bricks[i].getImageWidth(),
                        bricks[i].getImageHeight(), this);
            }
        }
    }

    private void gameFinished(Graphics2D g2d) {
        //TODO: design a finished page when the game is over!

//        var font = new Font("Verdana", Font.BOLD, 18);
//        FontMetrics fontMetrics = this.getFontMetrics(font);
//
//        g2d.setColor(Color.BLACK);
//        g2d.setFont(font);
//        g2d.drawString(message, (Commons.WIDTH - fontMetrics.stringWidth(message)) / 2, Commons.WIDTH/2);
//        String aa = "your final score is";
//        g2d.drawString(aa, (Commons.WIDTH - fontMetrics.stringWidth(aa)) / 2, Commons.WIDTH/2 + 50);

    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            paddle.keyPressed(e);
        }
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    private void doGameCycle() {

        ball.move();
        ball2.move();
        paddle.move();
        checkCollision();
        repaint();
    }

    private void stopGame() {
        inGame = false;
        timer.stop();
        scoreCounter = 0;
        backgroundSong.pause();

        String[] options = new String[] {"Play again","Quit"};
        int choice = JOptionPane.showOptionDialog(null, message, "Game over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);

        if (choice == 0) {
            timer.stop();
            inGame=true;
            scoreCounter = 0;
            message = "Game over";
            initBoard();
        } else {
            System.exit(0);
        }


    }

    private void checkCollision() {
        //TODO:
        if(ballcount == 0){
            stopGame();
        }
        //main game logic to check if ball hit the brick
        if (ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {

                ballcount -= 1;
                ball.deleteBall(ball);

        }

        if (ball2.getRect().getMaxY() > Commons.BOTTOM_EDGE) {

                ballcount -= 1;
                ball2.deleteBall(ball2);

        }

        for (int i = 0, j = 0; i < Commons.N_OF_BRICKS; i++) {

            if (bricks[i].isDestroyed()) {

                j++;
            }

            if (j == Commons.N_OF_BRICKS) {

                message = "Victory!";
                try{
                    filePath2 = "src/resources/yay.wav";
                    yay = new main.SimpleAudioPlayer(filePath2);
                    yay.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
                stopGame();
            }
        }

//ball 1 paddle collision
        if ((ball.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
            }

            if (ballLPos >= first && ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
            }

            if (ballLPos >= second && ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }

            if (ballLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }
        }

        //ball 2 paddle collision
        if ((ball2.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball2.getRect().getMinX();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {
                ball2.setXDir(-1);
                ball2.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
            }

            if (ballLPos >= first && ballLPos < second) {
                ball2.setXDir(-1);
                ball2.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }
            }

            if (ballLPos >= second && ballLPos < third) {
                ball2.setXDir(0);
                ball2.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball2.setXDir(1);
                ball2.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }

            if (ballLPos > fourth) {
                ball2.setXDir(1);
                ball2.setYDir(-1);
                try{
                    paddleSound = "src/resources/Paddle.wav";
                    hittingBall = new main.SimpleAudioPlayer(paddleSound);
                    hittingBall.play();
                }catch(Exception ex){
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

            }
        }





//ball 1 bricks collision check

                for (int i = 0; i < Commons.N_OF_BRICKS; i++) {

                    if ((ball.getRect()).intersects(bricks[i].getRect())) {

                        int ballLeft = (int) ball.getRect().getMinX();
                        int ballWidth = (int) ball.getRect().getWidth();
                        int ballTop = (int) ball.getRect().getMinY();
                        int ballHeight = (int) ball.getRect().getHeight();

                        var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                        var pointLeft = new Point(ballLeft - 1, ballTop);
                        var pointTop = new Point(ballLeft, ballTop - 1);
                        var pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                        if (bricks[i].isDestroyed() == false) {

                            if (bricks[i].getRect().contains(pointRight)) {
                                try{
                                    scoreUp = "src/resources/coin.wav";
                                    coin = new main.SimpleAudioPlayer(scoreUp);
                                    coin.play();
                                }catch(Exception ex){
                                    System.out.println("Error with playing sound.");
                                    ex.printStackTrace();
                                }

                                ball.setXDir(-1);
                            }else if (bricks[i].getRect().contains(pointLeft)) {
                                try{
                                    scoreUp = "src/resources/coin.wav";
                                    coin = new main.SimpleAudioPlayer(scoreUp);
                                    coin.play();
                                }catch(Exception ex){
                                    System.out.println("Error with playing sound.");
                                    ex.printStackTrace();
                                }

                                ball.setXDir(1);
                            }

                            if (bricks[i].getRect().contains(pointTop)) {
                                try{
                                    scoreUp = "src/resources/coin.wav";
                                    coin = new main.SimpleAudioPlayer(scoreUp);
                                    coin.play();
                                }catch(Exception ex){
                                    System.out.println("Error with playing sound.");
                                    ex.printStackTrace();
                                }

                                ball.setYDir(1);

                            }else if (bricks[i].getRect().contains(pointBottom)) {
                                try{
                                    scoreUp = "src/resources/coin.wav";
                                    coin = new main.SimpleAudioPlayer(scoreUp);
                                    coin.play();
                                }catch(Exception ex){
                                    System.out.println("Error with playing sound.");
                                    ex.printStackTrace();
                                }

                                ball.setYDir(-1);

                            }

                            bricks[i].setDestroyed(true);
                            scoreCounter ++;
                            scoreString = String.valueOf(scoreCounter);

                        }
                    }
                }


        //ball 2 bricks collision check

        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {

            if ((ball2.getRect()).intersects(bricks[i].getRect())) {

                int ballLeft = (int) ball2.getRect().getMinX();
                int ballWidth = (int) ball2.getRect().getWidth();
                int ballTop = (int) ball2.getRect().getMinY();
                int ballHeight = (int) ball2.getRect().getHeight();

                var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                var pointLeft = new Point(ballLeft - 1, ballTop);
                var pointTop = new Point(ballLeft, ballTop - 1);
                var pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (bricks[i].isDestroyed() == false) {

                    if (bricks[i].getRect().contains(pointRight)) {
                        try{
                            scoreUp = "src/resources/coin.wav";
                            coin = new main.SimpleAudioPlayer(scoreUp);
                            coin.play();
                        }catch(Exception ex){
                            System.out.println("Error with playing sound.");
                            ex.printStackTrace();
                        }

                        ball2.setXDir(-1);
                    }else if (bricks[i].getRect().contains(pointLeft)) {
                        try{
                            scoreUp = "src/resources/coin.wav";
                            coin = new main.SimpleAudioPlayer(scoreUp);
                            coin.play();
                        }catch(Exception ex){
                            System.out.println("Error with playing sound.");
                            ex.printStackTrace();
                        }

                        ball2.setXDir(1);
                    }

                    if (bricks[i].getRect().contains(pointTop)) {
                        try{
                            scoreUp = "src/resources/coin.wav";
                            coin = new main.SimpleAudioPlayer(scoreUp);
                            coin.play();
                        }catch(Exception ex){
                            System.out.println("Error with playing sound.");
                            ex.printStackTrace();
                        }

                        ball2.setYDir(1);

                    }else if (bricks[i].getRect().contains(pointBottom)) {
                        try{
                            scoreUp = "src/resources/coin.wav";
                            coin = new main.SimpleAudioPlayer(scoreUp);
                            coin.play();
                        }catch(Exception ex){
                            System.out.println("Error with playing sound.");
                            ex.printStackTrace();
                        }

                        ball2.setYDir(-1);

                    }

                    bricks[i].setDestroyed(true);
                    scoreCounter ++;
                    scoreString = String.valueOf(scoreCounter);

                }
            }
        }

        }
    }

