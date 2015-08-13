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

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import nik.heatsupply.customizers.renderers.AreaRendererFict;

public class Spozh2 implements JRChartCustomizer {
	private final double BAR_OFFSET = 0.1;

	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		Font font = Tools.getChartFont(jChart);

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

					XYSeries lastYearSeries = new XYSeries("");
					XYSeriesCollection dsLastYear = new XYSeriesCollection(lastYearSeries);

					for(int j = 5; j > -1; j--) {
						lastYearSeries.add((XYDataItem) s0.getItems().get(s0.getItemCount() - 1 - j));
					}
					
					for(int j = 0; j < lastYearSeries.getItemCount() - 1; j++) {
						s0.remove(s0.getItemCount() - 1);
					}
					
					AreaRendererFict rendererMain = new AreaRendererFict(Tools.fontResize(font, -2));
					rendererMain.setFillColor(new Color(255,140,0));
					rendererMain.setWithValues(true);
					rendererMain.setBorderStroke(new BasicStroke(1.5f));
					plot.setRenderer(i, rendererMain);
					
					plot.setDataset(plot.getDatasetCount(), dsLastYear);
					AreaRendererFict rendererLast = new AreaRendererFict(Tools.fontResize(font, -2));
					rendererLast.setFillColor(new Color(0,128,255));
					rendererLast.setWithValues(true);
					rendererLast.setBorderStroke(new BasicStroke(1.5f));
					plot.setRenderer(plot.getRendererCount(), rendererLast);
				}
			}

			if(minX != 0) plot.getDomainAxis().setLowerBound(minX + 0.4);
			if(maxX != 0) plot.getDomainAxis().setUpperBound(maxX);
			Tools.drawXaxis(plot);
		}
	}
}