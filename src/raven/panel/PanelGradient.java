package raven.panel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

public class PanelGradient extends JComponent {

    private GradientType gradientType = GradientType.HORIZONTAL;
    private Color colorGradient = new Color(255, 255, 255);
    private int radius;

    public PanelGradient() {
        setOpaque(true);
        setBackground(new Color(255, 255, 255));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Insets inset = getInsets();
            int width = getWidth() - inset.left - inset.right;
            int height = getHeight() - inset.top - inset.bottom;
            int x1, x2, y1, y2;
            if (gradientType == GradientType.HORIZONTAL || gradientType == null) {
                x1 = inset.left;
                y1 = inset.top;
                x2 = inset.left + width;
                y2 = inset.top;
            } else if (gradientType == GradientType.VERTICAL) {
                x1 = inset.left;
                y1 = inset.top;
                x2 = inset.left;
                y2 = inset.top + height;
            } else if (gradientType == GradientType.DIAGONAL_1) {
                x1 = inset.left;
                y1 = inset.top + height;
                x2 = inset.left + width;
                y2 = inset.top;
            } else {
                x1 = inset.left;
                y1 = inset.top;
                x2 = inset.left + width;
                y2 = inset.top + height;
            }
            Point p1 = new Point(x1, y1);
            Point p2 = new Point(x2, y2);
            g2.setPaint(new GradientPaint(p1, getBackground(), p2, colorGradient));
            g2.fill(new RoundRectangle2D.Double(inset.left, inset.top, width, height, radius, radius));
            g2.dispose();
        }
        super.paintComponent(g);
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
}
