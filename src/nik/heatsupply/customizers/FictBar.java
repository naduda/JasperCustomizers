package nik.heatsupply.customizers;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

public class FictBar implements JRChartCustomizer {
	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			Tools.createBarChart(plot, 0.1);
			plot.getRenderer().setSeriesPaint(0, Color.BLUE);
			System.out.println("FictBar end " + plot.getDatasetCount());
		}
	}
}