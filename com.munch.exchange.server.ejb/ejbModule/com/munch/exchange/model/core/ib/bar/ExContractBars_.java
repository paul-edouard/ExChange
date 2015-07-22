package com.munch.exchange.model.core.ib.bar;

import com.munch.exchange.model.core.ib.ExContract;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-22T21:17:42.436+0200")
@StaticMetamodel(ExContractBars.class)
public class ExContractBars_ extends ExBar_ {
	public static volatile SingularAttribute<ExContractBars, ExContract> contract;
	public static volatile ListAttribute<ExContractBars, ExBar> allBars;
}
