package raven.panel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import raven.shadow.ShadowRenderer;

public class PanelShadow extends JPanel {

    public ShadowType getShadowType() {
        return shadowType;
    }

    public void setShadowType(ShadowType shadowType) {
        this.shadowType = shadowType;
    }

    public int getShadowSize() {
        return shadowSize;
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        this.shadowOpacity = shadowOpacity;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    private BufferedImage renderImage;
    private ShadowType shadowType = ShadowType.CENTER;
    private int shadowSize = 6;
    private float shadowOpacity = 0.5f;
    private Color shadowColor = Color.BLACK;
    //  Gradient Option
    private GradientType gradientType = GradientType.HORIZONTAL;
    private Color colorGradient = new Color(255, 255, 255);
    private int radius;

    public PanelShadow() {
        setOpaque(false);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (renderImage == null) {
            createRenderImage();
        }
        grphcs.drawImage(renderImage, 0, 0, null);
        super.paintComponent(grphcs);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        createRenderImage();
    }

    private void createRenderImage() {
        renderImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = renderImage.createGraphics();
        int size = shadowSize * 2;
        int x;
        int y;
        int width = getWidth() - size;
        int height = getHeight() - size;
        if (shadowType == ShadowType.TOP) {
            x = shadowSize;
            y = size;
        } else if (shadowType == ShadowType.BOT) {
            x = shadowSize;
            y = 0;
        } else if (shadowType == ShadowType.TOP_LEFT) {
            x = size;
            y = size;
        } else if (shadowType == ShadowType.TOP_RIGHT) {
            x = 0;
            y = size;
        } else if (shadowType == ShadowType.BOT_LEFT) {
            x = size;
            y = 0;
        } else if (shadowType == ShadowType.BOT_RIGHT) {
            x = 0;
            y = 0;
        } else {
            x = shadowSize;
            y = shadowSize;
        }
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        createBackground(g, width, height);
        ShadowRenderer render = new ShadowRenderer(shadowSize, shadowOpacity, shadowColor);
        g2.drawImage(render.createShadow(img), 0, 0, null);
        g2.drawImage(img, x, y, null);
        g2.dispose();
    }

    private void createBackground(Graphics2D g2, int width, int height) {
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x1, x2, y1, y2;
        if (gradientType == GradientType.HORIZONTAL || gradientType == null) {
            x1 = 0;
            y1 = 0;
            x2 = width;
            y2 = 0;
        } else if (gradientType == GradientType.VERTICAL) {
            x1 = 0;
            y1 = 0;
            x2 = 0;
            y2 = height;
        } else if (gradientType == GradientType.DIAGONAL_1) {
            x1 = 0;
            y1 = height;
            x2 = width;
            y2 = 0;
        } else {
            x1 = 0;
            y1 = 0;
            x2 = width;
            y2 = height;
        }
        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);
        g2.setPaint(new GradientPaint(p1, getBackground(), p2, colorGradient));
        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        g2.dispose();
    }

    public GradientType getGradientType() {
        return gradientType;
    }

    public void setGradientType(GradientType gradientType) {
        this.gradientType = gradientType;
        repaint();
    }

    public Color getColorGradient() {
        return colorGradient;
    }

    public void setColorGradient(Color colorGradient) {
        this.colorGradient = colorGradient;
        repaint();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public static enum GradientType {
        VERTICAL, HORIZONTAL, DIAGONAL_1, DIAGONAL_2
    }

    public static enum ShadowType {
        CENTER, TOP_RIGHT, TOP_LEFT, BOT_RIGHT, BOT_LEFT, BOT, TOP
    }
}
