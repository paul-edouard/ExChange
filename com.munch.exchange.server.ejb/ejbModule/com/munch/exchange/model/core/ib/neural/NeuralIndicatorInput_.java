package com.munch.exchange.model.core.ib.neural;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.BarType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-05-26T15:36:23.175+0200")
@StaticMetamodel(NeuralIndicatorInput.class)
public class NeuralIndicatorInput_ extends NeuralInput_ {
	public static volatile SingularAttribute<NeuralIndicatorInput, BarSize> size;
	public static volatile SingularAttribute<NeuralIndicatorInput, WhatToShow> type;
	public static volatile SingularAttribute<NeuralIndicatorInput, BarType> barType;
	public static volatile SingularAttribute<NeuralIndicatorInput, IbContract> contract;
	public static volatile SingularAttribute<NeuralIndicatorInput, BarContainer> barContainer;
	public static volatile SingularAttribute<NeuralIndicatorInput, IbChartIndicator> indicator;
	public static volatile SingularAttribute<NeuralIndicatorInput, Double> barRange;
}
