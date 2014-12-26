package com.munch.exchange.parts.neuralnetwork.results;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;

public class TreeNNResultViewerComparator extends ViewerComparator {
	
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public TreeNNResultViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
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
		NetworkArchitecture p1 = (NetworkArchitecture) e1;
		NetworkArchitecture p2 = (NetworkArchitecture) e2;

		int rc = 0;
		if (p1 == null || p2 == null)
			return rc;

		switch (propertyIndex) {

		// Id
		case 0:
			rc = p2.getId().compareTo(p1.getId());
			break;

		// InnerNeurons
		case 1:
			rc = (p2.getNumberOfInnerNeurons() >= p1.getNumberOfInnerNeurons() ? 1: -1);
			break;
			
		// Best Result
		case 2:
			rc = (p2.getBestValue() >= p1.getBestValue() ? 1: -1);
			break;
			
		// Best Optimization
		case 3:
			rc = (p2.getBestOptimizationRate() >= p1.getBestOptimizationRate() ? 1: -1);
			break;
			
		// Middle Optimization
		case 4:
			rc = (p2.getMiddleOptimzationRate() >= p1.getMiddleOptimzationRate() ? 1: -1);
			break;
			
		// Nb. Of Optimization
		case 5:
			rc = (p2.getNumberOfOptimization() >= p1.getNumberOfOptimization() ? 1: -1);
			break;

		// Best Optimization
		case 6:
			rc = (p2.getBestTrainingRate() >= p1.getBestTrainingRate() ? 1: -1);
			break;
				
		// Middle Optimization
		case 7:
			rc = (p2.getMiddleTrainingRate() >= p1.getMiddleTrainingRate() ? 1: -1);
			break;
				
		// Middle Optimization
		case 8:
			rc = (p2.getNumberOfTraining() >= p1.getNumberOfTraining() ? 1: -1);
			break;
			
			

		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
	
	
	
	
	

}
