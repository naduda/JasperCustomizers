package nik.heatsupply.customizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import nik.heatsupply.customizers.renderers.RendererShowArrows;
import nik.heatsupply.customizers.renderers.RendererShowValues;

public class StrokeStyle implements JRChartCustomizer {
	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		chart.setTitle("Споживання за 2015 рік\n\t\n\t");
		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			Tools.createStepChart(plot);

			XYSeriesCollection ds = Tools.divideChartData((XYSeriesCollection) plot.getDataset(0));
			plot.setDataset(1, ds);
			plot.setDataset(2, ds);

			plot.setRenderer(0, new RendererShowValues((XYSeriesCollection) plot.getDataset(0), Tools.getChartFont(jChart), true, true));
			plot.setRenderer(1, new RendererShowArrows((XYSeriesCollection) plot.getDataset(1)));

			XYLineAndShapeRenderer renderer = new DefaultXYItemRenderer();
			Stroke dashed =  new BasicStroke(1.0f,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {10.0f}, 0.0f);
			renderer.setBaseStroke(dashed);
			renderer.setBaseItemLabelsVisible(false);
			for(int i = 0; i < ds.getSeriesCount(); i++)
				renderer.setSeriesStroke(i, 
					new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,1.0f, new float[] {4.0f, 2.0f}, 0.0f));
			renderer.setBaseSeriesVisibleInLegend(false);
			renderer.setBaseItemLabelFont(Tools.getChartFont(jChart));
			renderer.setBaseLegendTextFont(Tools.getChartFont(jChart));
			renderer.setBaseShapesVisible(false);
			
			for(int i = 0; i < ds.getSeriesCount(); i++) renderer.setSeriesPaint(i, new Color(255,0,0));
			plot.setRenderer(2, renderer);
			System.out.println("StrokeStyle end " + plot.getDatasetCount());
		}
	}
}