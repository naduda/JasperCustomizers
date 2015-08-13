package nik.heatsupply.customizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import nik.heatsupply.customizers.renderers.PointValue;

public class DateCustomizer implements JRChartCustomizer {

	@Override
	public void customize(JFreeChart chart, JRChart jChart) {
		if(chart.getPlot().getPlotType().toLowerCase().equals("xy")) {
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);

			DateAxis dAxis = (DateAxis) plot.getDomainAxis();
			dAxis.setDateFormatOverride(new DateFormat() {
				private static final long serialVersionUID = 1L;
				@Override
				public Date parse(String source, ParsePosition pos) {
					return null;
				}

				@Override
				public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
					DateFormat df = new SimpleDateFormat("dd.MM");
					return new StringBuffer(df.format(date));
				}
			});
			
			Tools.setSameBounds(plot, 1);
			Tools.setFonts(plot, Tools.getChartFont(jChart));
			plot.setDomainGridlinesVisible(true);
			
			TimeSeriesCollection rozDS = null;
			Calendar c = Calendar.getInstance();
			for(int j = 0; j < plot.getDatasetCount(); j++) {
				TimeSeriesCollection curDS = (TimeSeriesCollection) plot.getDataset(j);
				for(int i = 0; i < plot.getSeriesCount(); i++) {
					TimeSeries ser = (TimeSeries) curDS.getSeries(i);
					Date fDate = new Date(((TimeSeriesDataItem)ser.getItems().get(0)).getPeriod().getFirstMillisecond());
					c.setTime(fDate);
					int numDate = c.get(Calendar.DAY_OF_MONTH);
					if(numDate != 1) {
						PointValue renderer = new PointValue();
						renderer.setFillColor(Color.BLUE);
						renderer.setFont(Tools.fontResize(Tools.getChartFont(jChart), -4));
						plot.setRenderer(j, renderer);
					} else {
						plot.getRenderer(j).setSeriesPaint(0, new Color(255,140,0));
						plot.getRenderer(j).setSeriesShape(0, new Rectangle2D.Double());
						plot.getRenderer(j).setSeriesStroke(0, new BasicStroke(1.5f));
						TimeSeries ts = null;
						try {
							ts = (TimeSeries) ser.clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
						ts.setKey("Розрахункові");
						rozDS = new TimeSeriesCollection(ts);
						
						Date dtBeg = new Date((long)dAxis.getLowerBound());
						c.setTime(dtBeg);
						c.add(Calendar.YEAR, 1);
						dAxis.setLowerBound(curDS.getXValue(0, 0));
						dAxis.setUpperBound(c.getTimeInMillis());
					}
				}
			}

			plot.setDataset(plot.getDatasetCount(), rozDS);
			PointValue renderer = new PointValue();
			renderer.setFillColor(Color.RED);
			renderer.setRectWidth(2);
			renderer.setSeriesShape(0, new Rectangle2D.Double(-1, -1, 2, 2));
			
			plot.setRenderer(plot.getRendererCount(), renderer);
		}
	}

}
