package edu.bcm.dldcc.big.acquire.inventory;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.PieChartModel;

import edu.bcm.dldcc.big.acquire.qualifiers.DiseaseSite;
import edu.bcm.dldcc.big.acquire.scoreboard.Scoreboard;

@Named("diseaseChart")
@RequestScoped
public class DiseaseChart {

	private PieChartModel diseaseModel;

	@Inject
	@DiseaseSite
	private List<Scoreboard> diseaseScores;

	public DiseaseChart() {

	}

	@PostConstruct
	private void createDiseaseModel() {

		diseaseModel = new PieChartModel();

		for (Scoreboard scoreboard : diseaseScores) {
			//filter out zero values
			if(scoreboard.getTotalSamples() != 0) {
			diseaseModel.set(scoreboard.getName(), scoreboard.getTotalSamples());
			}
		}

	}

	public PieChartModel getDiseaseModel() {
		return diseaseModel;
	}

}
