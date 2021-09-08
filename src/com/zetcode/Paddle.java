package com.zetcode;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Paddle extends Sprite  {

    private int dx;

    public Paddle() {
        
        initPaddle();        
    }





    private void initPaddle() {

        loadImage();
        getImageDimensions();

        resetState();
    }
    
    private void loadImage() {
        
        var ii = new ImageIcon("src/resources/paddle.png");
        image = ii.getImage();        
    }    

    void move() {

        x += dx;

        if (x <= 0) {

            x = 0;
        }

        if (x >= Commons.WIDTH - imageWidth) {

            x = Commons.WIDTH - imageWidth;
        }
    }

    //TODO:
    void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            dx = 2;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            dx = -2;
        }
        //move the paddle here
    }

    //TODO:
    void keyReleased(KeyEvent e) {
        dx = 0;
        //stops the paddle when no key pressed
    }

    private void resetState() {

        x = Commons.INIT_PADDLE_X;
        y = Commons.INIT_PADDLE_Y;
    }
}
