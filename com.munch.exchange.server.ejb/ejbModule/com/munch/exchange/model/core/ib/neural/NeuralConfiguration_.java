package com.munch.exchange.model.core.ib.neural;

import com.munch.exchange.model.core.ib.IbContract;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-02T13:27:38.928+0100")
@StaticMetamodel(NeuralConfiguration.class)
public class NeuralConfiguration_ {
	public static volatile SingularAttribute<NeuralConfiguration, Integer> id;
	public static volatile SingularAttribute<NeuralConfiguration, IbContract> contract;
	public static volatile SingularAttribute<NeuralConfiguration, String> name;
	public static volatile SingularAttribute<NeuralConfiguration, Long> creationDate;
	public static volatile ListAttribute<NeuralConfiguration, NeuralInput> neuralInputs;
}
