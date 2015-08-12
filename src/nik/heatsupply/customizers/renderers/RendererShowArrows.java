package nik.heatsupply.customizers.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RendererShowArrows extends XYLineAndShapeRenderer {
	private static final long serialVersionUID = 1L;
	private XYSeriesCollection ds;
	private boolean isRight = false;

	public RendererShowArrows(XYSeriesCollection ds) {
		this.ds = ds;
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			setSeriesStroke(i, new BasicStroke(0));
			setSeriesOutlineStroke(i, new BasicStroke(0));
			setSeriesPaint(i, Color.YELLOW);
			setBaseItemLabelsVisible(false);
			setBaseSeriesVisibleInLegend(false);
		}
	}
	
	public RendererShowArrows(XYSeriesCollection ds, boolean isRight) {
		this(ds);
		this.isRight = isRight;
	}

	@Override
	public Shape getItemShape(int row, int col) {
		Shape shape = new Ellipse2D.Double(0, 0, 0, 0);
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			XYSeries series = ds.getSeries(i);
			try {
				XYDataItem item = (XYDataItem) series.getItems().get(col);
			
				if(item != null) {
					if(col < series.getItems().size() - 1) {
						XYDataItem itemNext = (XYDataItem) series.getItems().get(col + 1);
						if(isRight) {
							if(item.getYValue() != itemNext.getYValue() && item.getYValue() > 0) {
								shape = new Ellipse2D.Double(0, 0, 0, 0);
								if(col > 0) {
									XYDataItem itemPrev = (XYDataItem) series.getItems().get(col - 1);
									if(item.getYValue() == itemPrev.getYValue())
										shape = getArrow();
								}
							}
						} else {
							if(item.getYValue() == itemNext.getYValue() && item.getYValue() > 0) {
								shape = getArrow2();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return shape;
	}
	
	private Shape getArrow() {
		return new CloseShape(new Point2D.Double(isRight ? -10 : 10, 2),
				new Point2D.Double(isRight ? -13 : 13, 7),
				new Point2D.Double(isRight ? -7 : 7, 7));
	}
	
	private Shape getArrow2() {
		Path2D p = new Path2D.Double();
		p.moveTo(10, 2); p.lineTo(13, 7); p.lineTo(7, 7); p.closePath();
		p.moveTo(9, 7); p.lineTo(9, 10); p.lineTo(11, 10); p.lineTo(11, 7); p.closePath();
		return p;
	}
	
	private class CloseShape extends Path2D.Double {
		private static final long serialVersionUID = 1L;

		public CloseShape(Point2D... points) {
			moveTo(points[0].getX(), points[0].getY());
			for (int i = 1; i < points.length; i++) {
				lineTo(points[i].getX(), points[i].getY());
			}
			closePath();
		}
	}
}