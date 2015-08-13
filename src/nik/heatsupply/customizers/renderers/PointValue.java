package nik.heatsupply.customizers.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class PointValue extends XYLineAndShapeRenderer {
	private static final long serialVersionUID = 1L;
	private Color fillColor;
	private Color textColor;
	private double rectWidth;
	private Font font;
	
	public PointValue() {
		fillColor =  Color.YELLOW;
		textColor = Color.BLACK;
		rectWidth = 3;
	}
	
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
		setSeriesPaint(0, fillColor);
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setRectWidth(double rectWidth) {
		this.rectWidth = rectWidth;
	}

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {

		double x1 = dataset.getXValue(series, item);
		double y1 = dataset.getYValue(series, item);
		double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
		if (Double.isNaN(y0)) y0 = 0.0;

		double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());

		if(y0 != y1 || item == 0) {
			g2.setStroke(new BasicStroke(1.5f));
			Shape shape = new Rectangle2D.Double(transX1 - rectWidth, transY1 - rectWidth, 
					2 * rectWidth, 2 * rectWidth);
			g2.setPaint(fillColor);
			g2.fill(shape);

			if(font != null) {
				String text = String.format("%4.0f", y1);
				
				GlyphVector vect = font.createGlyphVector(g2.getFontRenderContext(), text);
				shape = vect.getOutline((float)(transX1 - vect.getVisualBounds().getWidth()) - 5, (float)transY1 - 5);
				g2.setPaint(textColor);
				g2.fill(shape);
			}
		}
	}
}