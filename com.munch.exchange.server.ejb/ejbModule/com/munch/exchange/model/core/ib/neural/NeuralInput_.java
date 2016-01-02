package com.munch.exchange.model.core.ib.neural;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-02T15:19:33.549+0100")
@StaticMetamodel(NeuralInput.class)
public class NeuralInput_ {
	public static volatile SingularAttribute<NeuralInput, Integer> id;
	public static volatile ListAttribute<NeuralInput, NeuralInputComponent> components;
	public static volatile SingularAttribute<NeuralInput, NeuralConfiguration> neuralConfiguration;
	public static volatile SingularAttribute<NeuralInput, String> name;
}
