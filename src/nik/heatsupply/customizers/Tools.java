package nik.heatsupply.customizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRFont;

public class Tools {
	public static void append2File(String text) {
		File file = new File("d:/1.txt");
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			if (!file.exists()) file.createNewFile();
			out.println(text);
		}catch (IOException e) {
		}
	}
	
	public static void drawXaxis(XYPlot plot) {
		double minX = plot.getDomainAxis().getLowerBound();
		double maxX = plot.getDomainAxis().getUpperBound();
		XYSeries domainAxis = new XYSeries("");
		XYSeriesCollection dsAxis = new XYSeriesCollection(domainAxis);
		domainAxis.add(minX, 0);
		domainAxis.add(maxX, 0);

		plot.setDataset(plot.getDatasetCount(), dsAxis);
		DefaultXYItemRenderer rendererBorder = new DefaultXYItemRenderer();
		rendererBorder.setBaseItemLabelsVisible(false);
		rendererBorder.setBaseSeriesVisibleInLegend(false);
		rendererBorder.setSeriesPaint(0, Color.BLACK);
		rendererBorder.setSeriesShapesVisible(0, false);
		rendererBorder.setSeriesStroke(0, new BasicStroke(1.5f));
		plot.setRenderer(plot.getRendererCount(), rendererBorder);
	}

	public static Shape generateShapeFromText(Font font, String string, boolean isLeft, float offsetX, float offsetY) {
		Font fontNew = new Font(font.getName(), font.getStyle(), font.getSize() - 4);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2 = img.createGraphics();

		try {
			GlyphVector vect = fontNew.createGlyphVector(g2.getFontRenderContext(), string);
			offsetX = (float) (isLeft ? offsetX : -vect.getVisualBounds().getWidth() - offsetX);
			Shape shape = vect.getOutline(offsetX, (float) -vect.getVisualBounds().getY() - fontNew.getSize() + offsetY);
			return shape;
		} finally {
			g2.dispose();
		}
	}
	
	public static Font getChartFont(JRChart jChart) {
		JRFont f = jChart.getTitleFont();
		Font font = new Font(f != null ? f.getFontName() : "Arial", Font.PLAIN, f != null ? (int)f.getFontsize() : 10);
		return font;
	}
	
	public static Font fontResize(Font font, int diffSize) {
		return new Font(font.getFontName(), Font.PLAIN, font.getSize() + diffSize);
	}
	
	public static Font fontResize(Font font, int diffSize, boolean isBold) {
		return new Font(font.getFontName(), isBold ? Font.BOLD : Font.PLAIN, font.getSize() + diffSize);
	}
	
	public static void setSameBounds(XYPlot plot) {
		setSameBounds(plot, 1.1);
	}
	
	public static void setSameBounds(XYPlot plot, double upKoef) {
		int asixCount = plot.getRangeAxisCount();
		double maxYvalue = plot.getRangeAxis(0).getUpperBound();
		double minYvalue = plot.getRangeAxis(0).getLowerBound();
		for(int i = 1; i < asixCount; i++) {
			ValueAxis axis = plot.getRangeAxis(i);
			maxYvalue = axis.getUpperBound() > maxYvalue ? axis.getUpperBound() : maxYvalue;
			minYvalue = axis.getLowerBound() < minYvalue ? axis.getLowerBound() : minYvalue;
			minYvalue = minYvalue < 0 ? 0 : minYvalue;
		}
		for(int i = 0; i < asixCount; i++) {
			ValueAxis axis = plot.getRangeAxis(i);
			axis.setUpperBound(maxYvalue * upKoef);
			axis.setLowerBound(minYvalue);
			
			if(i !=  0) axis.setVisible(false);
		}
		if(plot.getDomainAxis().getLowerBound() < 1)
			plot.getDomainAxis().setLowerBound(0.4);
	}
	
	public static void setFonts(XYPlot plot, Font font) {
		Font fontAxis = fontResize(font, -4);
		plot.getDomainAxis().setTickLabelFont(fontResize(font, -6, true));
		plot.getRenderer().setBaseItemLabelFont(fontAxis);
		plot.setDomainGridlinesVisible(false);
		
		int asixCount = plot.getRangeAxisCount();
		for(int i = 0; i < asixCount; i++) {
			ValueAxis axis = plot.getRangeAxis(i);

			axis.setLabelFont(fontResize(font, -2));
			axis.setTickLabelFont(fontResize(font, -6, true));
		}
	}
	
	public static void createBarChart(XYPlot plot, double offset) {
		for(int i = 0; i < plot.getDatasetCount(); i++) {
			createBarChart(plot, i, offset);
		}
	}
	
	public static void createBarChart(XYPlot plot, int indexDS, double offset) {
		XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(indexDS);
		plot.setDataset(indexDS, convertToBar(ds, offset));
	}
	
	public static XYSeriesCollection convertToBar(XYSeriesCollection ds, double offset) {
		XYSeriesCollection dsNew = new XYSeriesCollection();
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			XYSeries series = ds.getSeries(i);
	
			int itemsCount = series.getItems().size();
			XYSeries newSeries = new XYSeries(series.getKey());
			for(int j = 0; j < itemsCount; j++) {
				XYDataItem item = (XYDataItem) series.getItems().get(j);
				XYDataItem itemNext = j < itemsCount - 1 ? (XYDataItem) series.getItems().get(j + 1) : null;
				if(j == 0) {
					double diff = itemNext.getXValue() - item.getXValue();
					newSeries.add(item.getXValue() - diff, 0);
					newSeries.add(item.getXValue() - diff * (0.5 - offset), 0);
					newSeries.add(item.getXValue() - diff * (0.5 - offset), item.getYValue());
					newSeries.add(item.getXValue() + diff * (0.5 - offset), item.getYValue());
					newSeries.add(item.getXValue() + diff * (0.5 - offset), 0);
					newSeries.add(item.getXValue() + diff * (0.5 - offset) + 2 * offset, 0);
				}
				if(itemNext != null && item.getYValue() != itemNext.getYValue()) {
					double diff = itemNext.getXValue() - item.getXValue();
					double diffabs = newSeries.getX(newSeries.getItemCount() - 1).doubleValue();
					newSeries.add(diffabs, itemNext.getYValue());
					newSeries.add(diffabs + diff * (1 - offset * 2), itemNext.getYValue());
					newSeries.add(diffabs + diff * (1 - offset * 2), 0);
					newSeries.add(diffabs + diff, 0);
				}
			}
			dsNew.addSeries(newSeries);
		}
		return dsNew;
	}
	
	public static void createStepChart(XYPlot plot) {
		for(int i = 0; i < plot.getDatasetCount(); i++) {
			XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(i);
			plot.setDataset(i, convertToStep(ds));
		}
	}
	
	public static void createStepChart(XYPlot plot, int indexDS) {
		XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(indexDS);
		plot.setDataset(indexDS, convertToStep(ds));
	}
	
	public static XYSeriesCollection convertToStep(XYSeriesCollection ds) {
		XYSeriesCollection dsNew = new XYSeriesCollection();
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			int itemsCount = ds.getSeries(i).getItems().size();
			XYSeries newSeries = new XYSeries(ds.getSeries(i).getKey());
			for(int j = 0; j < itemsCount; j++) {
				XYDataItem item = (XYDataItem) ds.getSeries(i).getItems().get(j);
				XYDataItem itemNext = j < itemsCount - 1 ? (XYDataItem) ds.getSeries(i).getItems().get(j + 1) : null;
				
				double diff = 0;
				if(j == 0 && item.getXValue() > 0) {
					diff = itemNext.getXValue() - item.getXValue();
					newSeries.add(item.getXValue() - diff * 0.5, item.getYValue());
					newSeries.add(item.getXValue() + diff * 0.5, item.getYValue());
				} else {
					XYDataItem itemPrev = (XYDataItem) ds.getSeries(i).getItems().get(j - 1);
					diff = item.getXValue() - itemPrev.getXValue();
					newSeries.add(item.getXValue() + diff * 0.5, item.getYValue());
				}
				if(itemNext != null && item.getYValue() != itemNext.getYValue())
					newSeries.add(item.getXValue() + diff * 0.5, itemNext.getYValue());
				
			}
			dsNew.addSeries(newSeries);
		}
		return dsNew;
	}
	
	public static XYSeriesCollection divideChartData(XYSeriesCollection ds) {
		List<XYSeries> listSeries = new ArrayList<>();
		XYSeriesCollection dsNew = new XYSeriesCollection();
		for(int i = 0; i < ds.getSeriesCount(); i++) {
			int itemsCount = ds.getSeries(i).getItems().size();
			listSeries.add(new XYSeries(""));
			for(int j = 0; j < itemsCount; j++) {
				XYDataItem item = (XYDataItem) ds.getSeries(i).getItems().get(j);
				XYDataItem itemNext = j < itemsCount - 1 ? (XYDataItem) ds.getSeries(i).getItems().get(j + 1) : null;
				listSeries.get(listSeries.size() - 1).add(item.getXValue(), item.getYValue());
				
				if(itemNext != null && item.getYValue() != itemNext.getYValue()) {
					listSeries.add(new XYSeries(""));
				}
			}
		}
		for(XYSeries s : listSeries) {
			if(((XYDataItem)s.getItems().get(0)).getYValue() > 0)
				dsNew.addSeries(s);
		}
		return dsNew;
	}
}