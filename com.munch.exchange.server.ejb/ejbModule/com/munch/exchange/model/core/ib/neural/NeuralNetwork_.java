package com.munch.exchange.model.core.ib.neural;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-02T16:43:01.632+0100")
@StaticMetamodel(NeuralNetwork.class)
public class NeuralNetwork_ {
	public static volatile SingularAttribute<NeuralNetwork, Integer> id;
	public static volatile SingularAttribute<NeuralNetwork, NeuralArchitecture> neuralArchitecture;
	public static volatile SingularAttribute<NeuralNetwork, byte[]> network;
}
