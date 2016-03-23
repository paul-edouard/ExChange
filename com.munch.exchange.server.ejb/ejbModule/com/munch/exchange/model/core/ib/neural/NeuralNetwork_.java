package com.munch.exchange.model.core.ib.neural;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-22T20:29:38.491+0100")
@StaticMetamodel(NeuralNetwork.class)
public class NeuralNetwork_ {
	public static volatile SingularAttribute<NeuralNetwork, Integer> id;
	public static volatile SingularAttribute<NeuralNetwork, NeuralArchitecture> neuralArchitecture;
	public static volatile SingularAttribute<NeuralNetwork, byte[]> network;
	public static volatile SingularAttribute<NeuralNetwork, byte[]> pareto;
}
