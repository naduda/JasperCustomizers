package nik.heatsupply.customizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import nik.heatsupply.customizers.renderers.AreaRendererFict;
import nik.heatsupply.customizers.renderers.RendererShowArrows;
import nik.heatsupply.customizers.renderers.RendererShowValues;

public class Spozh1 implements JRChartCustomizer {
	private final double BAR_OFFSET = 0.1;

	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		Font font = Tools.getChartFont(jChart);

		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			Tools.setFonts(plot, font);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);

			NumberAxis axis = (NumberAxis) plot.getDomainAxis();
			Map<Double, String> month = new HashMap<>();
			month.put(1d, "Ciчень");
			month.put(2d, "Лютий");
			month.put(3d, "Березень");
			month.put(4d, "Квітень");
			month.put(5d, "Травень");
			month.put(6d, "Червень");
			month.put(7d, "Липень");
			month.put(8d, "Серпень");
			month.put(9d, "Вересень");
			month.put(10d, "Жовтень");
			month.put(11d, "Листопад");
			month.put(12d, "Грудень");
			XaxisFormat format = new XaxisFormat(month);
			axis.setNumberFormatOverride(format);
			plot.setDomainAxis(axis);
			plot.getDomainAxis().setTickMarksVisible(false);

			XYSeriesCollection valuesDS = null;
			XYSeriesCollection limitDS = null;
			XYSeriesCollection diffDS = null;

			for(int i = 0; i < plot.getRendererCount(); i++) {
				XYItemRenderer renderer = plot.getRenderer(i);
				if(renderer instanceof XYAreaRenderer) {
					valuesDS = (XYSeriesCollection) plot.getDataset(i);
				} else if(renderer instanceof XYLineAndShapeRenderer) {
					limitDS = (XYSeriesCollection) plot.getDataset(i);
				}
			}
			
			if(valuesDS != null && limitDS != null) {
				diffDS = new XYSeriesCollection();
				XYSeries seriesValue = valuesDS.getSeries(0);
				XYSeries seriesLimit = limitDS.getSeries(0);
				XYSeries seriesDiff = new XYSeries("diff");
				for(int j = 0; j < seriesLimit.getItemCount(); j++) {
					XYDataItem lIt = (XYDataItem) seriesLimit.getItems().get(j);
					XYDataItem vIt = (XYDataItem) seriesValue.getItems().get(j);
					XYDataItem item = new XYDataItem(vIt.getXValue(), vIt.getYValue() - lIt.getYValue());
					seriesDiff.add(item);
				}
				diffDS.addSeries(seriesDiff);
				diffDS = Tools.convertToBar(diffDS, BAR_OFFSET);
			}

			for(int i = 0; i < plot.getRendererCount(); i++) {
				XYItemRenderer renderer = plot.getRenderer(i);
				if(renderer instanceof XYAreaRenderer) {
					Tools.createBarChart(plot, i, BAR_OFFSET);
					AreaRendererFict rendererMain = new AreaRendererFict(Tools.fontResize(font, -2));
					rendererMain.setFillColor(new Color(0,128,255));
					rendererMain.setWithValues(true);
					rendererMain.setBorderStroke(new BasicStroke(1.5f));
					plot.setRenderer(i, rendererMain);
					
					XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(i);

					plot.setDataset(plot.getDatasetCount(), ds);
					plot.setRenderer(plot.getRendererCount(), new RendererShowArrows(ds, true));
					if(diffDS != null){
						plot.setDataset(plot.getDatasetCount(), ds);
						RendererShowValues rendererValuesDiff = 
								new RendererShowValues(ds, Tools.fontResize(font, -2));
						rendererValuesDiff.setBottom(true);
						rendererValuesDiff.setLeft(false);
						rendererValuesDiff.setDsVal(diffDS);
						rendererValuesDiff.setOffsetX(5);
						rendererValuesDiff.setColor(Color.WHITE);
						plot.setRenderer(plot.getRendererCount(), rendererValuesDiff);
					}
					plot.setDataset(plot.getDatasetCount(), ds);
					DefaultXYItemRenderer rendererBorder = new DefaultXYItemRenderer();
					rendererBorder.setBaseItemLabelsVisible(false);
					rendererBorder.setBaseSeriesVisibleInLegend(false);
					rendererBorder.setSeriesPaint(0, Color.BLACK);
					rendererBorder.setSeriesShapesVisible(0, false);
					plot.setRenderer(plot.getRendererCount(), rendererBorder);
					break;
				}
			}

			for(int i = 0; i < plot.getRendererCount(); i++) {
				XYItemRenderer renderer = plot.getRenderer(i);
				if(renderer instanceof XYLineAndShapeRenderer && !(renderer instanceof AreaRendererFict)) {
					Tools.createStepChart(plot, i);
					XYSeriesCollection ds = (XYSeriesCollection) plot.getDataset(i);

					RendererShowValues rendererValues = 
							new RendererShowValues((XYSeriesCollection) plot.getDataset(i), Tools.fontResize(font, -2));
					rendererValues.setBottom(true);
					rendererValues.setOffsetX(5);
					rendererValues.setColor(Color.WHITE);
					plot.setRenderer(i, rendererValues);

					plot.setDataset(plot.getDatasetCount(), ds);
					plot.setRenderer(plot.getRendererCount(), new RendererShowArrows(ds));

					ds = Tools.divideChartData((XYSeriesCollection) plot.getDataset(i));
					plot.setDataset(plot.getDatasetCount(), ds);

					XYLineAndShapeRenderer renderer2 = new DefaultXYItemRenderer();
					renderer2.setBaseItemLabelsVisible(false);
					renderer2.setBaseSeriesVisibleInLegend(false);
					renderer2.setBaseItemLabelFont(Tools.getChartFont(jChart));
					renderer2.setBaseLegendTextFont(Tools.getChartFont(jChart));
					renderer2.setBaseShapesVisible(false);
					
					for(int j = 0; j < ds.getSeriesCount(); j++) renderer2.setSeriesPaint(j, Color.RED);
					plot.setRenderer(plot.getRendererCount(), renderer2);
					break;
				}
			}
			
			Tools.setSameBounds(plot);
			Tools.drawXaxis(plot);
		}
	}
	
	private class XaxisFormat extends NumberFormat {
		private static final long serialVersionUID = 1L;
		private Map<Double, String> labels;

		public XaxisFormat(Map<Double, String> labels) {
			this.labels = labels;
		}

		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			String text = "";
			if(labels != null && labels.get(number) != null)
				text = labels.get(number);
			return new StringBuffer(text);
		}

		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			String text = "";
			if(labels != null && labels.get(number) != null)
				text = labels.get(number);
			return new StringBuffer(text);
		}

		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			return null;
		}
	}
}