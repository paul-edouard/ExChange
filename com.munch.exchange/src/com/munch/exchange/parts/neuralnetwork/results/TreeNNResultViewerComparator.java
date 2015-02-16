package com.munch.exchange.parts.neuralnetwork.results;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.goataa.impl.utils.Constants;

import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.ResultEntity;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;
import com.munch.exchange.parts.neuralnetwork.results.NeuralNetworkResultsPart.ResultsInfo;

public class TreeNNResultViewerComparator extends ViewerComparator {
	
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	
	private static Logger logger = Logger.getLogger(TreeNNResultViewerComparator.class);
	
	private HashMap<String, ResultsInfo> resultsInfoMap;
	private Stock stock=null;
	
	public TreeNNResultViewerComparator(HashMap<String, ResultsInfo> resultsInfoMap) {
		this.propertyIndex = 0;
		direction = DESCENDING;
		this.resultsInfoMap=resultsInfoMap;
	}
	
	
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int rc = 0;

		if (e1 instanceof NetworkArchitecture
				&& e2 instanceof NetworkArchitecture) {

			NetworkArchitecture p1 = (NetworkArchitecture) e1;
			ResultsInfo ri_1 = resultsInfoMap.get(p1.getId());
	

			NetworkArchitecture p2 = (NetworkArchitecture) e2;
			ResultsInfo ri_2 = resultsInfoMap.get(p2.getId());

			if (p1 == null || p2 == null)
				return rc;

			switch (propertyIndex) {

			// Id
			case 0:
				if (p2.getSelfIndex() > p1.getSelfIndex())
					rc = 1;
				else if (p2.getSelfIndex() < p1
						.getSelfIndex())
					rc = -1;
				break;

			// InnerNeurons
			case 1:
				if (p2.getNumberOfInnerNeurons() > p1.getNumberOfInnerNeurons())
					rc = 1;
				else if (p2.getNumberOfInnerNeurons() < p1
						.getNumberOfInnerNeurons())
					rc = -1;
				break;

			// Best Result
			case 2:
				if (p2.getBestValue() > p1.getBestValue())
					rc = 1;
				else if (p2.getBestValue() < p1.getBestValue())
					rc = -1;
				break;

			// Best Optimization
			case 3:
				if (p2.getBestOptimizationRate() > p1.getBestOptimizationRate())
					rc = 1;
				else if (p2.getBestOptimizationRate() < p1
						.getBestOptimizationRate())
					rc = -1;
				break;

			// Middle Optimization
			case 4:
				if (p2.getMiddleOptimzationRate() > p1
						.getMiddleOptimzationRate())
					rc = 1;
				else if (p2.getMiddleOptimzationRate() < p1
						.getMiddleOptimzationRate())
					rc = -1;
				break;

			// Nb. Of Optimization
			case 5:
				if (p2.getNumberOfOptimization() > p1.getNumberOfOptimization())
					rc = 1;
				else if (p2.getNumberOfOptimization() < p1
						.getNumberOfOptimization())
					rc = -1;
				break;

			// Nb. Of Optimization
			case 9:
				if (p2.getLastOptimization().getTimeInMillis() > p1
						.getLastOptimization().getTimeInMillis())
					rc = 1;
				else if (p2.getLastOptimization().getTimeInMillis() < p1
						.getLastOptimization().getTimeInMillis())
					rc = -1;
				break;

			// Best Training
			case 6:
				if (p2.getBestTrainingRate() > p1.getBestTrainingRate())
					rc = 1;
				else if (p2.getBestTrainingRate() < p1.getBestTrainingRate())
					rc = -1;
				break;

			// Middle Training
			case 7:
				if (p2.getMiddleTrainingRate() > p1.getMiddleTrainingRate())
					rc = 1;
				else if (p2.getMiddleTrainingRate() < p1
						.getMiddleTrainingRate())
					rc = -1;
				break;

			// Middle Training
			case 8:
				if (p2.getNumberOfTraining() > p1.getNumberOfTraining())
					rc = 1;
				else if (p2.getNumberOfTraining() < p1.getNumberOfTraining())
					rc = -1;
				break;

			// Last Training
			case 10:
				if (p2.getLastTraining().getTimeInMillis() > p1
						.getLastTraining().getTimeInMillis())
					rc = 1;
				else if (p2.getLastTraining().getTimeInMillis() < p1
						.getLastTraining().getTimeInMillis())
					rc = -1;
				break;

			// Prediction
			case 11:
				rc=compareResultsInfo(ri_1,ri_2,ri_1.PREDICTION);
				break;

			// Total Profit
			case 12:
				rc=compareResultsInfo(ri_1,ri_2,ri_1.TOTAL_PROFIT);
				break;

			// Tain Profit
			case 13:
				rc=compareResultsInfo(ri_1,ri_2,ri_1.TRAIN_PROFIT);
				break;

			// Validate Profit
			case 14:
				rc=compareResultsInfo(ri_1,ri_2,ri_1.VALIDATE_PROFIT);
				break;

			default:
				rc = 0;
			}

		}
		
		else if (e1 instanceof ResultEntity && e2 instanceof ResultEntity) {
			ResultEntity p1 = (ResultEntity) e1;
			ResultsInfo ri_1 = resultsInfoMap.get(p1.getId());

			ResultEntity p2 = (ResultEntity) e2;
			ResultsInfo ri_2 = resultsInfoMap.get(p2.getId());

			if (p1 == null || p2 == null)
				return rc;

			switch (propertyIndex) {

			// Id
			case 0:
				if(stock!=null){
					Configuration config=stock.getNeuralNetwork().getConfiguration();
					NetworkArchitecture archi_1=config.searchArchitecture(p1.getParentId());
					NetworkArchitecture archi_2=config.searchArchitecture(p2.getParentId());
					if(archi_1.getId().equals(archi_2.getId())){
						int pos_1=archi_1.getResultPosition(p1);
						int pos_2=archi_1.getResultPosition(p2);
						if (pos_2 > pos_1)
							rc = 1;
						else if (pos_2 < pos_1)
							rc = -1;
						
						
					}
				}
				else
					rc = p2.getId().compareTo(p1.getId());
				break;

			// Best Result
			case 2:
				if (p2.getValue() > p1.getValue())
					rc = 1;
				else if (p2.getValue() < p1.getValue())
					rc = -1;
				break;

			// Prediction
			case 11:
				rc = compareResultsInfo(ri_1, ri_2, ri_1.PREDICTION);
				break;

			// Total Profit
			case 12:
				rc = compareResultsInfo(ri_1, ri_2, ri_1.TOTAL_PROFIT);
				break;

			// Tain Profit
			case 13:
				rc = compareResultsInfo(ri_1, ri_2, ri_1.TRAIN_PROFIT);
				break;

			// Validate Profit
			case 14:
				rc = compareResultsInfo(ri_1, ri_2, ri_1.VALIDATE_PROFIT);
				break;

			default:
				rc = 0;
			}
			
		}
		
		
		

		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}

		return rc;
	}
	
	
	private int compareResultsInfo(ResultsInfo ri_1,ResultsInfo ri_2, String valueName){
		int rc=0;
		
		if ( ri_2 == null  && ri_1 == null )
			rc = 0;
		else if(ri_2 != null  && ri_1 == null)
			rc = 1;
		else if(ri_2 == null  && ri_1 != null)
			rc = -1;
		else if(Double.isNaN(ri_2.getValue(valueName)) && Double.isNaN(ri_1.getValue(valueName)))
			rc = 0;
		else if(!Double.isNaN(ri_2.getValue(valueName)) && Double.isNaN(ri_1.getValue(valueName)))
			rc = 1;
		else if(Double.isNaN(ri_2.getValue(valueName)) && !Double.isNaN(ri_1.getValue(valueName)))
			rc = -1;
		if (ri_2.getValue(valueName) > ri_1.getValue(valueName))
			rc = 1;
		else if (ri_2.getValue(valueName) < ri_1.getValue(valueName))
			rc = -1;
		
		return rc;
	}
	
	

}
