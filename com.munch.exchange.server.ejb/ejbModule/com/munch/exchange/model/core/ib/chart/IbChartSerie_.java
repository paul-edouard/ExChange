package com.munch.exchange.model.core.ib.chart;

import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-19T21:59:57.595+0200")
@StaticMetamodel(IbChartSerie.class)
public class IbChartSerie_ {
	public static volatile SingularAttribute<IbChartSerie, Integer> id;
	public static volatile SingularAttribute<IbChartSerie, String> name;
	public static volatile SingularAttribute<IbChartSerie, Integer> validAtPosition;
	public static volatile SingularAttribute<IbChartSerie, Boolean> isMain;
	public static volatile SingularAttribute<IbChartSerie, Boolean> isActivated;
	public static volatile SingularAttribute<IbChartSerie, RendererType> rendererType;
	public static volatile SingularAttribute<IbChartSerie, IbChartIndicator> indicator;
}
