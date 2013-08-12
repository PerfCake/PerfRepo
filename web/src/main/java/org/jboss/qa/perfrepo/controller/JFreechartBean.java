package org.jboss.qa.perfrepo.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.bean.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

@RequestScoped
@Named("jfreechart")
public class JFreechartBean extends ControllerBase {
   private static final Logger log = Logger.getLogger(JFreechartBean.class);

   public static class XYLineChartSpec implements Serializable {
      public String title;
      public String xAxisLabel;
      public String yAxisLabel;
      public XYSeriesCollection dataset;
      public int width = 640;
      public int height = 320;
   }

   public void generate(OutputStream out, Object data) throws IOException {
      if (data instanceof XYLineChartSpec) {
         XYLineChartSpec chartSpec = (XYLineChartSpec) data;
         try {
            JFreeChart chart = ChartFactory.createXYLineChart(chartSpec.title, chartSpec.xAxisLabel, chartSpec.yAxisLabel, chartSpec.dataset,
                  PlotOrientation.VERTICAL, false, false, false);
            BufferedImage buffImg = chart.createBufferedImage(chartSpec.width, chartSpec.height, BufferedImage.TYPE_INT_RGB, null);
            ImageIO.write(buffImg, "png", out);
         } catch (Exception e) {
            log.error("Error while creating chart", e);
         }
      }
   }
}
