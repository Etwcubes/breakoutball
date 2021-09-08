package com.zetcode;

import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.ImageIcon;
import java.sql.SQLOutput;

public class Ball extends Sprite {

    private int xdir;
    private int ydir;
    public String paddleSound;
    public main.SimpleAudioPlayer hittingBall;

    public Ball() {

    }

    public void deleteBall(Ball ball){
        ball.setX(-10);
        ball.setY(-10);
        ball.setYDir(-1);
        setXDir(0);
    }

    public void initBall(int x_pos, int y_pos) {
        
        xdir = 1;
        ydir = -1;

        loadImage();
        getImageDimensions();
        resetState(x_pos,y_pos);
    }

    private void loadImage() {

        var ii = new ImageIcon("src/resources/ball.png");
        image = ii.getImage();
    }

    //when ball hits roof of board , change y dir to 1. when changing directions use setXDir and setYDir declared in thisclass
    void move() {
        x += xdir ;
        y += ydir ;

        if (x == 0){
            try{
                paddleSound = "src/resources/Paddle.wav";
                hittingBall = new main.SimpleAudioPlayer(paddleSound);
                hittingBall.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
            setXDir(1);
        }

       if(x == Commons.WIDTH - imageWidth){
           try{
               paddleSound = "src/resources/Paddle.wav";
               hittingBall = new main.SimpleAudioPlayer(paddleSound);
               hittingBall.play();
           }catch(Exception ex){
               System.out.println("Error with playing sound.");
               ex.printStackTrace();
           }
           System.out.println(imageWidth);
           setXDir(-1);
       }

        if (y == 0){
            try{
                paddleSound = "src/resources/Paddle.wav";
                hittingBall = new main.SimpleAudioPlayer(paddleSound);
                hittingBall.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
            setYDir(1);
        }


        //TODO:
        //how does the ball move?
    }

    private void resetState(int x_pos, int y_pos) {
//230,355
        x = x_pos;
        y = y_pos;
    }

    void setXDir(int x) {

        xdir = x;
    }

    void setYDir(int y) {

        ydir = y;
    }

    int getYDir() {

        return ydir;
    }
}
