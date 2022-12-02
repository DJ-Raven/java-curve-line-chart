package raven.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import raven.chart.blankchart.BlankPlotChart;
import raven.chart.blankchart.BlankPlotChatRender;
import raven.spline.Spline;
import raven.spline.SplinePoint;

public class CurveLineChart extends JComponent {

    public boolean isFillColor() {
        return fillColor;
    }

    public void setFillColor(boolean fillColor) {
        this.fillColor = fillColor;
    }

    private final List<ModelLegend> legends = new ArrayList<>();
    private final List<ModelChart> model = new ArrayList<>();
    private Animator animator;
    private Animator animatorChange;
    private Animator animatorLabel;
    private float animate;
    private float animateChange;
    private final Spline spline = new Spline();
    private SplinePoint[] current;
    private SplinePoint[] lastPoint;
    private int index = 0;
    private Color color1;
    private Color color2;
    private boolean fillColor = false;
    private TimingTarget timingColor1;
    private TimingTarget timingColor2;
    private int selectedIndex = -1;
    private float currentPoint = -1;
    private float oldPoint = -1;
    private float targetPoint = -1;
    private float alphaLable;

    public CurveLineChart() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, inset 0", "[fill]", "[]10[fill,100%]5"));
        setForeground(new Color(120, 120, 120));
        createPanelLegend();
        createBlankChart();
        createChart();
        createAnimatorChart();
        initAnimatorChange();
        initAnimatorLabel();
    }

    private void createAnimatorChart() {
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animate = fraction;
                repaint();
            }
        };
        animator = new Animator(1500, target);
        animator.setResolution(5);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
    }

    private void initAnimatorChange() {
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animateChange = fraction;
                repaint();
            }
        };
        animatorChange = new Animator(800, target);
        animatorChange.setResolution(5);
        animatorChange.setAcceleration(0.5f);
        animatorChange.setDeceleration(0.5f);
    }

    private void initAnimatorLabel() {
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                currentPoint = oldPoint + ((targetPoint - oldPoint) * fraction);
                if (alphaLable != 1) {
                    alphaLable = fraction;
                }
                repaint();
            }
        };
        animatorLabel = new Animator(500, target);
        animatorLabel.setResolution(5);
    }

    private void createBlankChart() {
        blankPlotChart = new BlankPlotChart();
        add(blankPlotChart);
    }

    private void createChart() {
        blankPlotChart.setBlankPlotChatRender(new BlankPlotChatRender() {

            @Override
            public String getLabelText(int index) {
                return model.get(index).getLabel();
            }

            @Override
            public void renderGraphics(BlankPlotChart chart, Graphics2D g2, Rectangle2D rectangle) {
                if (!model.isEmpty() && animate > 0 && index >= 0 && index <= legends.size() - 1) {
                    draw(g2, rectangle, index, chart.getNiceScale().getTickSpacing() * chart.getNiceScale().getMaxTicks());
                }
            }

            @Override
            public void mouseMove(Rectangle2D rectangle, MouseEvent mouse) {
                if (!model.isEmpty()) {
                    int per = (int) (rectangle.getWidth() / model.size());
                    int index = -1;
                    for (int i = 0; i < per; i++) {
                        double x = i * per + rectangle.getX();
                        if (mouse.getX() >= x && mouse.getX() <= x + per && mouse.getY() >= rectangle.getY() && mouse.getY() <= rectangle.getY() + rectangle.getHeight()) {
                            index = i;
                            break;
                        }
                    }
                    if (index >= model.size() - 1) {
                        index = model.size() - 1;
                    }
                    if (index != selectedIndex) {
                        changeSelectedIndex(index);
                    }
                }
            }
        });
    }

    private void changeSelectedIndex(int index) {
        if (index != -1) {
            this.selectedIndex = index;
            if (animatorLabel.isRunning()) {
                animatorLabel.stop();
            }
            if (selectedIndex >= model.size() - 1) {
                oldPoint = currentPoint;
                targetPoint = model.size() - 1 - 0.01f;
                animatorLabel.start();
            } else if (selectedIndex >= 0) {
                oldPoint = currentPoint;
                targetPoint = selectedIndex;
                animatorLabel.start();
            } else {
                repaint();
            }
        }
    }

    private void draw(Graphics2D g2, Rectangle2D rec, int index, double maxValue) {
        SplinePoint points[];
        if (lastPoint == null || !animatorChange.isRunning()) {
            points = toPoint(rec, index, maxValue);
        } else {
            points = copyPoint(lastPoint);
        }
        if (animatorChange.isRunning()) {
            SplinePoint pointsNew[] = toPoint(rec, index, maxValue);
            for (int i = 0; i < points.length; i++) {
                double b = pointsNew[i].getY() - points[i].getY();
                points[i].setY(points[i].getY() + (b * animateChange));
            }
        }
        g2.setColor(legends.get(index).getColor1());
        current = copyPoint(points);
        List<SplinePoint> list = spline.createSpline(animate, points);
        Path2D.Double path = new Path2D.Double();
        boolean first = true;
        for (SplinePoint p : list) {
            if (first) {
                path.moveTo(p.getX(), p.getY());
                first = false;
            } else {
                path.lineTo(p.getX(), p.getY());
            }
        }
        float size = 6;
        g2.setPaint(new GradientPaint((int) rec.getX(), 0, color1, (int) (rec.getX() + rec.getWidth()), 0, color2));
        g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (fillColor && !list.isEmpty()) {
            Path2D p = new Path2D.Double(path);
            SplinePoint f = list.get(0);
            SplinePoint l = list.get(list.size() - 1);
            float s = size / 2;
            p.moveTo(l.getX(), l.getY());
            p.lineTo(l.getX() + s, l.getY());
            p.lineTo(l.getX() + s, rec.getY() + rec.getHeight());
            p.lineTo(f.getX() - s, rec.getY() + rec.getHeight());
            p.lineTo(f.getX() - s, f.getY());
            p.lineTo(f.getX(), f.getY());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.fill(p);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        g2.draw(path);
        if (currentPoint != -1) {
            SplinePoint s = spline.getSpline(currentPoint);
            drawLabel(g2, s);
        }
    }

    private void drawLabel(Graphics2D g2, SplinePoint s) {
        g2.setStroke(new BasicStroke(1f));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaLable * 0.3f));
        g2.fill(new Ellipse2D.Double(s.getX() - 13, s.getY() - 13, 26, 26));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaLable));
        g2.fill(new Ellipse2D.Double(s.getX() - 8, s.getY() - 8, 16, 16));
        g2.setColor(getForeground());
        g2.fill(new Ellipse2D.Double(s.getX() - 5, s.getY() - 5, 10, 10));
        if (selectedIndex >= 0) {
            String text = blankPlotChart.getFormat().format(model.get(selectedIndex).getValues()[index]);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double space = 5;
            double w = r2.getWidth() + space * 2;
            double h = r2.getHeight() + space * 2;
            double x = (s.getX() - r2.getWidth() / 2) - space;
            double y = s.getY() + fm.getAscent() - r2.getHeight() - h - 13;
            g2.translate(x, y);
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 5, 5));
            g2.setColor(new Color(200, 200, 200, 100));
            g2.draw(new RoundRectangle2D.Double(0, 0, w, h, 5, 5));
            g2.setColor(getForeground());
            double fx = (w - r2.getWidth()) / 2;
            double fy = (h - r2.getHeight()) / 2;
            fy += fm.getAscent();
            g2.drawString(text, (int) fx, (int) fy);
        }
    }

    public SplinePoint[] copyPoint(SplinePoint[] points) {
        SplinePoint[] newPoints = new SplinePoint[points.length];
        for (int i = 0; i < points.length; i++) {
            newPoints[i] = points[i].copy();
        }
        return newPoints;
    }

    private SplinePoint[] toPoint(Rectangle2D rec, int index, double maxValue) {
        SplinePoint points[] = new SplinePoint[model.size() + 2];
        for (int i = 0; i < model.size(); i++) {
            points[i + 1] = toPoint(rec, i, model.size(), model.get(i).getValues()[index], maxValue);
        }
        points[0] = points[1];
        points[points.length - 1] = points[points.length - 2];
        return points;
    }

    private SplinePoint toPoint(Rectangle2D rec, int index, int max, double values, double maxValues) {
        double perX = rec.getWidth() / max;
        double x = (rec.getX() + perX * index) + perX / 2;
        double y = rec.getHeight() + rec.getY() - convertPoint(values, rec.getHeight(), maxValues);
        return new SplinePoint(x, y);
    }

    private double convertPoint(double values, double size, double maxValues) {
        return values * 1 / maxValues * size;
    }

    private void createPanelLegend() {
        panelLegend = new JPanel();
        panelLegend.setOpaque(false);
        panelLegend.setLayout(new MigLayout("filly, center, inset 0", "[]10[]"));
        labelTitle = new JLabel();
        labelTitle.setForeground(new Color(229, 229, 229));
        labelTitle.setFont(labelTitle.getFont().deriveFont(Font.BOLD, 15));
        panelLegend.add(labelTitle, "push, gap left 10");
        add(panelLegend, "wrap");
    }

    public void addLegend(String name, Color color1, Color color2) {
        ModelLegend data = new ModelLegend(name, color1, color2);
        legends.add(data);
        LegendItem legend = new LegendItem(data, legends.size() - 1);
        legend.setForeground(getForeground());
        legend.addActionListener((ActionEvent e) -> {
            if (animate > 0) {
                startChange(legend.getIndex());
                clearLegendSelected(legend);
            }
        });
        if (legends.size() - 1 == index) {
            legend.setSelected(true);
        }
        panelLegend.add(legend);
        panelLegend.repaint();
        panelLegend.revalidate();
    }

    private void clearLegendSelected(LegendItem item) {
        item.setSelected(true);
        for (Component com : panelLegend.getComponents()) {
            if (com instanceof LegendItem) {
                LegendItem l = (LegendItem) com;
                if (l != item) {
                    l.setSelected(false);
                }
            }
        }
    }

    public void addData(ModelChart data) {
        model.add(data);
        blankPlotChart.setLabelCount(model.size());
        double max = data.getMaxValues();
        if (max > blankPlotChart.getMaxValues()) {
            blankPlotChart.setMaxValues(max);
        }
    }

    public void clear() {
        animate = 0;
        blankPlotChart.setLabelCount(0);
        model.clear();
        selectedIndex = -1;
        alphaLable = 0f;
        currentPoint = 0f;
        animator.stop();
        animatorChange.stop();
        animatorLabel.stop();
        repaint();
    }

    public void start() {
        if (!animator.isRunning()) {
            color1 = legends.get(index).getColor1();
            color2 = legends.get(index).getColor2();
            animator.removeTarget(timingColor1);
            animator.removeTarget(timingColor2);
            animatorChange.stop();
            animatorLabel.stop();
            selectedIndex = -1;
            alphaLable = 0f;
            currentPoint = 0;
            animator.start();
        }
    }

    private void startChange(int index) {
        if (this.index != index) {
            if (animatorChange.isRunning()) {
                animatorChange.stop();
            }
            lastPoint = copyPoint(current);
            animateChange = 0;
            this.index = index;
            animatorChange.removeTarget(timingColor1);
            animatorChange.removeTarget(timingColor2);
            timingColor1 = new PropertySetter(this, "color1", color1, legends.get(index).getColor1());
            timingColor2 = new PropertySetter(this, "color2", color2, legends.get(index).getColor2());
            animatorChange.addTarget(timingColor1);
            animatorChange.addTarget(timingColor2);
            animatorChange.start();
        }
    }

    public void resetAnimation() {
        animate = 0;
        repaint();
    }

    public void setTitle(String title) {
        labelTitle.setText(title);
    }

    public String getTitle() {
        return labelTitle.getText();
    }

    public void setTitleFont(Font font) {
        labelTitle.setFont(font);
    }

    public Font getTitleFont() {
        return labelTitle.getFont();
    }

    public void setTitleColor(Color color) {
        labelTitle.setForeground(color);
    }

    public Color getTitleColor() {
        return labelTitle.getForeground();
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (blankPlotChart != null) {
            blankPlotChart.setForeground(fg);
            labelTitle.setForeground(fg);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    private BlankPlotChart blankPlotChart;
    private JPanel panelLegend;
    private JLabel labelTitle;
}
