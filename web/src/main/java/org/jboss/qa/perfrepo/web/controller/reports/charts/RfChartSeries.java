package org.jboss.qa.perfrepo.web.controller.reports.charts;

import org.richfaces.ui.output.chart.ChartDataModel;

/**
 * Simple series of RichFaces chart.
 *
 * @author jholusa <jholusa@redhat.com>
 * @since 4.0
 */
public class RfChartSeries {

	private ChartDataModel data;
	private String name;

	public RfChartSeries(ChartDataModel dataModel) {
		this.data = dataModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChartDataModel getData() {
		return data;
	}

	public void setData(ChartDataModel data) {
		this.data = data;
	}
}
