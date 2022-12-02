package raven.chart.blankchart;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public abstract class BlankPlotChatRender {

    public abstract String getLabelText(int index);

    public abstract void renderGraphics(BlankPlotChart chart, Graphics2D g2, Rectangle2D rectangle);

    public abstract void mouseMove(Rectangle2D rectangle, MouseEvent mouse);
}
