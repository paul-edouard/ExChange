package com.munch.exchange.model.core.ib.chart.signals;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-12-20T16:20:19.767+0100")
@StaticMetamodel(IbChartSignalOptimizedParameters.class)
public class IbChartSignalOptimizedParameters_ {
	public static volatile SingularAttribute<IbChartSignalOptimizedParameters, Integer> id;
	public static volatile SingularAttribute<IbChartSignalOptimizedParameters, BarSize> size;
	public static volatile ListAttribute<IbChartSignalOptimizedParameters, IbChartParameter> parameters;
	public static volatile SingularAttribute<IbChartSignalOptimizedParameters, IbChartSignal> parent;
}
