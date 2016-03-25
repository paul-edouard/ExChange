package com.munch.exchange.model.core.ib.neural;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-25T16:09:41.767+0100")
@StaticMetamodel(NeuralNetwork.class)
public class NeuralNetwork_ {
	public static volatile SingularAttribute<NeuralNetwork, Integer> id;
	public static volatile SingularAttribute<NeuralNetwork, NeuralArchitecture> neuralArchitecture;
	public static volatile SingularAttribute<NeuralNetwork, byte[]> network;
	public static volatile SingularAttribute<NeuralNetwork, String> networkName;
	public static volatile SingularAttribute<NeuralNetwork, byte[]> pareto;
	public static volatile SingularAttribute<NeuralNetwork, String> paretoName;
}
