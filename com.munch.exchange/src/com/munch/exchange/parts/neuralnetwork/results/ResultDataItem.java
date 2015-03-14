package com.munch.exchange.parts.neuralnetwork.results;


import org.jfree.data.xy.XYDataItem;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.ResultEntity;

public class ResultDataItem extends XYDataItem {

	
	
	private static final long serialVersionUID = 973867662366473102L;
	
	
	NetworkArchitecture archi=null;
	ResultEntity resultEntity=null;
	
	
	public ResultDataItem(double x, double y,NetworkArchitecture archi) {
		super(x, y);
		this.archi=archi;
	}

	public ResultEntity getResultEntity() {
		return resultEntity;
	}

	public void setResultEntity(ResultEntity resultEntity) {
		this.resultEntity = resultEntity;
	}

	public NetworkArchitecture getArchi() {
		return archi;
	}

}
