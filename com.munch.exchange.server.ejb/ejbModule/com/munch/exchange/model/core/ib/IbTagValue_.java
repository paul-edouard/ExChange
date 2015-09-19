package com.munch.exchange.model.core.ib;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-09-19T21:24:10.400+0200")
@StaticMetamodel(IbTagValue.class)
public class IbTagValue_ {
	public static volatile SingularAttribute<IbTagValue, Integer> id;
	public static volatile SingularAttribute<IbTagValue, String> tag;
	public static volatile SingularAttribute<IbTagValue, String> value;
	public static volatile SingularAttribute<IbTagValue, IbContract> owner;
}
