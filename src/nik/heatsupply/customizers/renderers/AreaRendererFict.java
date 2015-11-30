package nik.heatsupply.customizers.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class AreaRendererFict extends XYLineAndShapeRenderer {
	private static final long serialVersionUID = 1L;
	private Paint fillColor;
	private Color fillColor1;
	private Color fillColor2;
	private boolean isGradientFill = false;
	private boolean isHorizontal;
	private Color lineColor;
	private Font font;
	private boolean withValues = false;
	private Stroke borderStroke;

	public AreaRendererFict(Font font) {
		this.font = font;
		fillColor = Color.YELLOW;
		lineColor = Color.BLACK;
		borderStroke = new BasicStroke(1);
		setBaseItemLabelsVisible(false);
		setBaseSeriesVisibleInLegend(false);
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
	public void setFillColor(Color fillColor1, Color fillColor2, boolean isHorizontal) {
		this.fillColor1 = fillColor1;
		this.fillColor2 = fillColor2;
		this.isHorizontal = isHorizontal;
		this.isGradientFill = true;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public void setWithValues(boolean withValues) {
		this.withValues = withValues;
	}

	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {

		double x1 = dataset.getXValue(series, item);
		double y1 = dataset.getYValue(series, item);
		int itemCount = dataset.getItemCount(series);
		double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
		if (Double.isNaN(y0)) y0 = 0.0;
		double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
		double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
		if (Double.isNaN(y2)) y2 = 0.0;

		double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
		double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
		double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());

		if(y0 == 0 && y1 > 0 && y2 > 0) {
			g2.setStroke(new BasicStroke(1.5f));

			Path2D p = new Path2D.Double();
			p.moveTo(transX1, transY0);
			p.lineTo(transX1, transY1);
			p.lineTo(transX2, transY1);
			p.lineTo(transX2, transY0);
			p.closePath();
			
			if(isGradientFill) {
				GradientPaint gradientPaint = new GradientPaint((float)transX1, (float)transY0, fillColor1,
								isHorizontal ? (float)transX2 : (float)transX1, isHorizontal ? (float)transY0 : (float)transY1, fillColor2);
				g2.setPaint(gradientPaint);
			} else {
				g2.setPaint(fillColor);
			}
			g2.fill(p);
			g2.setPaint(lineColor);
			g2.setStroke(borderStroke);
			g2.draw(p);
			
			if(withValues) {
				String text = String.format("%4.0f", y1);
				
				GlyphVector vect = font.createGlyphVector(g2.getFontRenderContext(), text);
				Shape shape = vect.getOutline((float)transX1, (float)transY1 - 3);
				g2.fill(shape);
			}
		} else {
			g2.setPaint(lineColor);
			g2.draw(new Line2D.Double(transX1, transY1, transX2, transY1));
		}
	}
}