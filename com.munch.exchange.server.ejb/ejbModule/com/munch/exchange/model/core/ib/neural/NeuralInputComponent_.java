package com.munch.exchange.model.core.ib.neural;

import com.munch.exchange.model.core.ib.neural.NeuralInputComponent.ComponentType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-03T11:07:39.491+0100")
@StaticMetamodel(NeuralInputComponent.class)
public class NeuralInputComponent_ {
	public static volatile SingularAttribute<NeuralInputComponent, Integer> id;
	public static volatile SingularAttribute<NeuralInputComponent, ComponentType> componentType;
	public static volatile SingularAttribute<NeuralInputComponent, NeuralInput> neuralInput;
	public static volatile SingularAttribute<NeuralInputComponent, Integer> offset;
	public static volatile SingularAttribute<NeuralInputComponent, Integer> period;
	public static volatile SingularAttribute<NeuralInputComponent, Double> upperRange;
	public static volatile SingularAttribute<NeuralInputComponent, Double> lowerRange;
}
