package com.munch.exchange.model.core.ib.bar;

import com.munch.exchange.model.core.ib.IbContract;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-08-09T19:50:16.734+0200")
@StaticMetamodel(IbBarContainer.class)
public class IbBarContainer_ extends IbBar_ {
	public static volatile SingularAttribute<IbBarContainer, IbContract> contract;
	public static volatile ListAttribute<IbBarContainer, IbBar> allBars;
}
