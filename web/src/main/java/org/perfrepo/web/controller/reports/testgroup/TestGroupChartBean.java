package org.perfrepo.web.controller.reports.testgroup;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.richfaces.resource.SerializableResource;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.NumberFormat;

@RequestScoped
@Named("testGroupChart")
public class TestGroupChartBean implements Serializable {

	private static final long serialVersionUID = -1775541851083603748L;

	public static class ChartData implements SerializableResource {

		private static final long serialVersionUID = 4067171484547385543L;
		String[] tests;
		Double[] values;
		String title;

		public String[] getTests() {
			return tests;
		}

		public void setTests(String[] tests) {
			this.tests = tests;
		}

		public Double[] getValues() {
			return values;
		}

		public void setValues(Double[] values) {
			this.values = values;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	public void drawChart(OutputStream out, Object data) throws IOException {
		if (data instanceof ChartData) {
			ChartData chartData = (ChartData) data;
			JFreeChart chart = ChartFactory.createBarChart(chartData.getTitle(), "Test", "%", processDataSet(chartData), PlotOrientation.HORIZONTAL, false, true, false);
			chart.addSubtitle(new TextTitle("Comparison", new Font("Dialog",
					Font.ITALIC, 10)));
			chart.setBackgroundPaint(Color.white);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			CustomRenderer renderer = new CustomRenderer();

			plot.setBackgroundPaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
					"{2}%", NumberFormat.getInstance()));
			renderer.setBaseItemLabelsVisible(true);
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(1d / (chartData.getTests().length + 4.0));
			plot.setRenderer(renderer);

			CategoryAxis categoryAxis = plot.getDomainAxis();
			categoryAxis.setCategoryMargin(0.1);

			categoryAxis.setUpperMargin(0.1);
			categoryAxis.setLowerMargin(0.1);
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rangeAxis.setUpperMargin(0.10);
			BufferedImage buffImg = chart.createBufferedImage(640, chartData.getTests().length * 100 + 100);
			ImageIO.write(buffImg, "gif", out);
		}
	}

	private CategoryDataset processDataSet(ChartData map) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < map.getTests().length; i++) {
			dataset.addValue(map.getValues()[i], "", map.getTests()[i]);
		}
		return dataset;
	}

	class CustomRenderer extends BarRenderer {

		private static final long serialVersionUID = 7640860407096418280L;

		public Paint getItemPaint(final int row, final int column) {
			double value = getPlot().getDataset().getValue(row, column)
					.doubleValue();
			return (value < -5) ? Color.red : (value < 0 ? Color.orange : Color.green);
		}
	}
}
