package edu.bcm.dldcc.big.acquire.inventory;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import edu.bcm.dldcc.big.acquire.qualifiers.CollectionSite;
import edu.bcm.dldcc.big.acquire.scoreboard.Scoreboard;

@Named("collectionSiteChart")
@RequestScoped
public class CollectionChart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5743765440296269887L;

	private CartesianChartModel categoryModel;

	@Inject
	@CollectionSite
	private List<Scoreboard> scoreboards;

	public CollectionChart() {
	}

	@PostConstruct
	private void createCategoryModel() {
		categoryModel = new CartesianChartModel();

		ChartSeries total = new ChartSeries();
		total.setLabel("Total");

		ChartSeries qualified = new ChartSeries();
		qualified.setLabel("Qualified");
		
		for (Scoreboard scoreboard : scoreboards) {
			// different logic for BCM - we break it down by subsite
			if(scoreboard.getChildren().size() > 0) {
				for (Scoreboard child : scoreboard.getChildren()) {
					total.set(child.getName(), child.getTotalSamples());
					qualified.set(child.getName(), child.getQualified());
				}
			} else {
			total.set(scoreboard.getName(), scoreboard.getTotalSamples());
			qualified.set(scoreboard.getName(), scoreboard.getQualified());
			}
		}

		categoryModel.addSeries(total);
		categoryModel.addSeries(qualified);
	}

	public CartesianChartModel getCategoryModel() {
		return categoryModel;
	}

}
