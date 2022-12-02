package raven.chart;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class LegendItem extends JButton {

    public int getIndex() {
        return index;
    }

    private final ModelLegend legend;
    private final int index;

    public LegendItem(ModelLegend legend, int index) {
        this.legend = legend;
        this.index = index;
        setText(legend.getName());
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(2, 25, 2, 1));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int height = getHeight();
        int size = 8;
        int sizeWidth = 20;
        int y = (height - size) / 2;
        g2.setPaint(new GradientPaint(0, 0, legend.getColor1(), sizeWidth, sizeWidth, legend.getColor2()));
        g2.fillRect(0, y, sizeWidth, size);
        if (isSelected()) {
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
