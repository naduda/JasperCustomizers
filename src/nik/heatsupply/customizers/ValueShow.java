package nik.heatsupply.customizers;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import nik.heatsupply.customizers.renderers.RendererShowValues;

public class ValueShow implements JRChartCustomizer {
	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			Tools.createBarChart(plot, 0.1);
			XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(0);

			RendererShowValues renderer = new RendererShowValues(ds, Tools.getChartFont(jChart));
			renderer.setSeriesPaint(0, new Color(0,0,0));
			renderer.setBaseSeriesVisibleInLegend(false);
			plot.setRenderer(renderer);
		}
	}
}