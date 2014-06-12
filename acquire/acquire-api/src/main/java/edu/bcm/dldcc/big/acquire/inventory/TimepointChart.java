package edu.bcm.dldcc.big.acquire.inventory;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import edu.bcm.dldcc.big.acquire.scoreboard.TumorTimepoint;

@Named("timepointChart")
@RequestScoped
public class TimepointChart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5743765440296269887L;

	private CartesianChartModel model;

	@Inject
	@Named("timepoints")
	private List<TumorTimepoint> timepoints;

	public TimepointChart() {
	}

	@PostConstruct
	private void createModel() {
		model = new CartesianChartModel();

		ChartSeries total = new ChartSeries();
		total.setLabel("Total");

		ChartSeries qualified = new ChartSeries();
		qualified.setLabel("Qualified");
		
		for (TumorTimepoint timepoint : timepoints) {
			total.set(timepoint.getTimepointString(), timepoint.getTotalSamples());
			qualified.set(timepoint.getTimepointString(), timepoint.getQualifiedSamples());			
		}

		model.addSeries(total);
		model.addSeries(qualified);
	}

	public CartesianChartModel getModel() {
		return model;
	}

}
