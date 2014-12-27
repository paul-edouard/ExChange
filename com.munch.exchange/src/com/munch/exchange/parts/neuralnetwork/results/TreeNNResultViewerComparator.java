package com.munch.exchange.parts.neuralnetwork.results;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.goataa.impl.utils.Constants;

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
		if (p1 == null || p2 == null )
			return rc;
		
		switch (propertyIndex) {

		// Id
		case 0:
			rc = p2.getId().compareTo(p1.getId());
			break;

		// InnerNeurons
		case 1:
			if(p2.getNumberOfInnerNeurons() > p1.getNumberOfInnerNeurons())
				rc = 1;
			else if(p2.getNumberOfInnerNeurons() < p1.getNumberOfInnerNeurons())
				rc = -1;
			break;
			
		// Best Result
		case 2:
			if(p2.getBestValue() > p1.getBestValue())
				rc = 1;
			else if(p2.getBestValue() < p1.getBestValue())
				rc = -1;
			break;
			
		// Best Optimization
		case 3:
			if(p2.getBestOptimizationRate() > p1.getBestOptimizationRate())
				rc = 1;
			else if(p2.getBestOptimizationRate() < p1.getBestOptimizationRate())
				rc = -1;
			break;
			
		// Middle Optimization
		case 4:
			if(p2.getMiddleOptimzationRate() > p1.getMiddleOptimzationRate())
				rc = 1;
			else if(p2.getMiddleOptimzationRate() < p1.getMiddleOptimzationRate())
				rc = -1;
			break;
			
		// Nb. Of Optimization
		case 5:
			if(p2.getNumberOfOptimization() > p1.getNumberOfOptimization())
				rc = 1;
			else if(p2.getNumberOfOptimization() < p1.getNumberOfOptimization())
				rc = -1;
			break;
			
		// Nb. Of Optimization
		case 9:
			if(p2.getLastOptimization().getTimeInMillis() > p1.getLastOptimization().getTimeInMillis())
				rc = 1;
			else if(p2.getLastOptimization().getTimeInMillis() < p1.getLastOptimization().getTimeInMillis())
				rc = -1;
			break;			
			

		// Best Training
		case 6:
			if(p2.getBestTrainingRate() > p1.getBestTrainingRate())
				rc = 1;
			else if(p2.getBestTrainingRate() < p1.getBestTrainingRate())
				rc = -1;
			break;
				
		// Middle Training
		case 7:
			if(p2.getMiddleTrainingRate() > p1.getMiddleTrainingRate())
				rc = 1;
			else if(p2.getMiddleTrainingRate() < p1.getMiddleTrainingRate())
				rc = -1;
			break;
				
		// Middle Training
		case 8:
			if(p2.getNumberOfTraining() > p1.getNumberOfTraining())
				rc = 1;
			else if(p2.getNumberOfTraining() < p1.getNumberOfTraining())
				rc = -1;
			break;
			
		// Last Training
		case 10:
			if(p2.getLastTraining().getTimeInMillis() > p1.getLastTraining().getTimeInMillis())
				rc = 1;
			else if(p2.getLastTraining().getTimeInMillis() < p1.getLastTraining().getTimeInMillis())
				rc = -1;
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
