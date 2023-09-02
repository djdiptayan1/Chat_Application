package raven.chat.swing;

import com.vdurmont.emoji.EmojiParser;
import java.awt.Font;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Background extends JPanel {

    private Image backgroundImage; // Image to use as background

    public Background() {
        setOpaque(false);
        // Load the background image
        backgroundImage = new ImageIcon("/Users/djdiptayan/Documents/Developer/java programs/apps/chat/GUI/whatsapp_background copy.jpg").getImage();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        int width = getWidth();
        int height = getHeight();

        // Fill the gradient background
        g2.setPaint(new GradientPaint(0, 0, new Color(58, 72, 85), width, 0, new Color(28, 38, 50)));
        g2.fillRect(0, 0, width, height);

        // Draw the background image
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, this);
        }

        g2.dispose();
        super.paintComponent(grphcs);
    }
}
