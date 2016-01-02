package com.munch.exchange.model.core.ib.neural;

import com.munch.exchange.model.core.ib.neural.NeuralInputComponent.ComponentType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-02T15:34:50.673+0100")
@StaticMetamodel(NeuralInputComponent.class)
public class NeuralInputComponent_ {
	public static volatile SingularAttribute<NeuralInputComponent, Integer> id;
	public static volatile SingularAttribute<NeuralInputComponent, ComponentType> componentType;
	public static volatile SingularAttribute<NeuralInputComponent, NeuralInput> neuralInput;
	public static volatile SingularAttribute<NeuralInputComponent, Double> upperRange;
	public static volatile SingularAttribute<NeuralInputComponent, Double> lowerRange;
	public static volatile SingularAttribute<NeuralInputComponent, Integer> offset;
	public static volatile SingularAttribute<NeuralInputComponent, Integer> period;
}
