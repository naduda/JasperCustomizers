package nik.heatsupply.customizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;
import nik.heatsupply.customizers.renderers.AreaRendererFict;

public class Spozh2 extends JRAbstractChartCustomizer {
	private final double BAR_OFFSET = 0.1;

	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		Font font = Tools.getChartFont(jChart);
		int curYear = 0;
		if(jChart.getPropertiesMap() != null) {
			try {
				curYear = Integer.parseInt(jChart.getPropertiesMap().getProperty("currentYear"));
			} catch (NumberFormatException e) {
				System.out.println("\n\n\nProperty currentYear not exist\n\n\n");
			}
		}
		chart.setTitle("");
		
		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			Tools.setFonts(plot, font);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
			double minX = 0;
			double maxX = 0;

			for(int i = 0; i < plot.getRendererCount(); i++) {
				XYItemRenderer renderer = plot.getRenderer(i);
				if(renderer instanceof XYAreaRenderer) {
					Tools.createBarChart(plot, i, BAR_OFFSET);
					renderer.setSeriesPaint(0, Color.ORANGE);
					renderer.setBaseItemLabelsVisible(false);
					renderer.setBaseSeriesVisibleInLegend(false);

					XYSeriesCollection ds = (XYSeriesCollection)plot.getDataset(i);
					
					XYSeries s0 = (XYSeries)ds.getSeries().get(0);
					minX = ((XYDataItem)s0.getItems().get(0)).getXValue();
					maxX = ((XYDataItem)s0.getItems().get(s0.getItemCount() - 1)).getXValue();

					XYSeries currentYearSeries = new XYSeries("");
					XYSeriesCollection dsCurrentYear = new XYSeriesCollection(currentYearSeries);

					boolean isFirst = true;
					for(int j = 0; j < s0.getItemCount(); j++) {
						XYDataItem it = (XYDataItem) s0.getItems().get(j);
						int xVal = it.getX().intValue();
						if(xVal == curYear - 1 || xVal == curYear) {
							if(isFirst) {
								if(j > 0) {
									currentYearSeries.add((XYDataItem) s0.getItems().get(j - 1));
								}
								isFirst = false;
							} 
							currentYearSeries.add((XYDataItem) s0.getItems().get(j));
						}
					}
					
					AreaRendererFict rendererMain = new AreaRendererFict(Tools.fontResize(font, -2));
					rendererMain.setFillColor(new Color(255,140,0));
					rendererMain.setWithValues(true);
					rendererMain.setBorderStroke(new BasicStroke(1.5f));
					plot.setRenderer(i, rendererMain);
					
					plot.setDataset(plot.getDatasetCount(), dsCurrentYear);
					AreaRendererFict rendererLast = new AreaRendererFict(Tools.fontResize(font, -2));
					rendererLast.setFillColor(new Color(0,128,255));
					rendererLast.setFillColor(new Color(0,128,255), new Color(0,0,255), true);
					rendererLast.setWithValues(true);
					rendererLast.setBorderStroke(new BasicStroke(1.5f));
					plot.setRenderer(plot.getRendererCount(), rendererLast);
				}
			}

			if(minX != 0) plot.getDomainAxis().setLowerBound(minX + 0.4);
			if(maxX != 0) {
				plot.getDomainAxis().setUpperBound(maxX);
				plot.getRangeAxis().setUpperBound(plot.getRangeAxis().getUpperBound() * 1.2);
			}
//			System.out.println(axis.getUpperBound() + "        1111111111111");
			
			Tools.drawXaxis(plot);
		}
	}
}