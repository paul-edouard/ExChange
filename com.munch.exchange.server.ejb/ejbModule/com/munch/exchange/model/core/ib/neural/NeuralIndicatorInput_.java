package com.munch.exchange.model.core.ib.neural;

import com.ib.controller.Types.BarSize;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-02T20:32:19.548+0100")
@StaticMetamodel(NeuralIndicatorInput.class)
public class NeuralIndicatorInput_ extends NeuralInput_ {
	public static volatile SingularAttribute<NeuralIndicatorInput, BarSize> size;
	public static volatile SingularAttribute<NeuralIndicatorInput, Integer> ibBarContainerId;
}
