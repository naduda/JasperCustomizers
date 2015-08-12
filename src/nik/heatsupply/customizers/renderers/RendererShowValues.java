package nik.heatsupply.customizers.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import nik.heatsupply.customizers.Tools;

public class RendererShowValues extends XYLineAndShapeRenderer {
	private static final long serialVersionUID = 1L;
	private XYSeriesCollection ds;
	private XYSeriesCollection dsVal;
	private Font font;
	private boolean isBottom = false;
	private boolean isLeft = true;
	private float offsetX = 0;

	public RendererShowValues(XYSeriesCollection ds, Font font) {
		this.ds = ds;
		this.font = font;
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			setSeriesStroke(i, new BasicStroke(0));
			setSeriesOutlineStroke(i, new BasicStroke(0));
			setSeriesPaint(i, Color.BLACK);
			setBaseItemLabelsVisible(false);
			setBaseSeriesVisibleInLegend(false);
		}
	}
	
	public RendererShowValues(XYSeriesCollection ds, Font font, boolean isBottom) {
		this(ds, font);
		this.isBottom = isBottom;
	}
	
	public RendererShowValues(XYSeriesCollection ds, Font font, boolean isBottom, boolean isLeft) {
		this(ds, font, isBottom);
		this.isLeft = isLeft;
	}
	
	public RendererShowValues(XYSeriesCollection ds, Font font, boolean isBottom, boolean isLeft, XYSeriesCollection dsVal) {
		this(ds, font, isBottom);
		this.isLeft = isLeft;
		this.dsVal = dsVal;
	}

	public void setBottom(boolean isBottom) {
		this.isBottom = isBottom;
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public void setDsVal(XYSeriesCollection dsVal) {
		this.dsVal = dsVal;
	}

	public void setColor(Color color) {
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			setSeriesPaint(i, color);
		}
	}

	@Override
	public Shape getItemShape(int row, int col) {
		String text = "";

		for(int i = 0; i < ds.getSeriesCount(); i++) {
			XYSeries series = ds.getSeries(i);
			XYDataItem item = (XYDataItem) series.getItems().get(col);
			XYDataItem itemVal = null;
			if(dsVal != null && dsVal.getSeries(0).getItems().size() > col)
				itemVal = (XYDataItem) dsVal.getSeries(0).getItems().get(col);

			if(item != null) {
				if(col < series.getItems().size() - 1) {
					XYDataItem itemNext = (XYDataItem) series.getItems().get(col + 1);
					if(isLeft) {
						if(item.getYValue() == itemNext.getYValue() && item.getYValue() > 0) {
							text += itemVal == null ? item.getYValue() : itemVal.getYValue();
						}
					} else {
						if(col > 0) {
							XYDataItem itemPrev = (XYDataItem) series.getItems().get(col - 1);
							if(item.getYValue() == itemPrev.getYValue())
								text += itemVal == null ? item.getYValue() : itemVal.getYValue();
						}
					}
				}
				if(text.length() > 0 && text.indexOf(".") != -1) text = text.substring(0, text.indexOf("."));
				return Tools.generateShapeFromText(font, text, isLeft,
						offsetX, isBottom ? 20 : 0);
			}
		}
		return Tools.generateShapeFromText(font, "", isLeft, offsetX, isBottom ? 20 : 0);
	}
}