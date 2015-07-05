package com.munch.exchange.model.core.ib;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2015-07-05T13:33:49.118+0200")
@StaticMetamodel(ExTagValue.class)
public class ExTagValue_ {
	public static volatile SingularAttribute<ExTagValue, Integer> id;
	public static volatile SingularAttribute<ExTagValue, String> tag;
	public static volatile SingularAttribute<ExTagValue, String> value;
	public static volatile SingularAttribute<ExTagValue, ExContract> owner;
}
