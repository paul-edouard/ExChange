package com.munch.exchange.model.analytic;

import java.util.Arrays;

public class functions {
	
	/**
	 * Der Median oder Zentralwert ist ein Mittelwert f�r Stichproben
	 *  in der Statistik und ein Lageparameter. Der Median einer Auflistung 
	 *  von Zahlenwerten ist der Wert, der an der mittleren (zentralen) Stelle steht, 
	 *  wenn man die Werte der Gr��e nach sortiert. Beispielsweise ist f�r die Werte
	 *   4, 1, 37, 2, 1 die Zahl 2 der Median, n�mlich die mittlere Zahl in 1, 1, 2, 4, 37.
	 * @return
	 */
	static double  median(double[] numArray){
		Arrays.sort(numArray);
		double median;
		if (numArray.length % 2 == 0)
		    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
		else
		    median = (double) numArray[numArray.length/2];
		return median;
	}
	
	public static double[] median(double[] numArray, int period){
		double[] median = new double[numArray.length]; 
		for(int i = 0; i < numArray.length ; i++){
			
			int a_l = Math.min(i+1, period);
			double[] n_array = new double[a_l];
			for(int j=0;j<a_l;j++){
				n_array[j] = numArray[i-a_l+1+j];
			}
			Arrays.sort(n_array);
			//double median;
			if (n_array.length % 2 == 0)
				median[i] = ((double)n_array[n_array.length/2] + (double)n_array[n_array.length/2 - 1])/2;
			else
				median[i] = (double) n_array[n_array.length/2];
			//return median;
		}
		return median;
	}
	
	public static void main(String[] args){
		double[] list = new double[5];
		list[0]= 1;
		list[1]= 8;
		list[2]= 2;
		list[3]= 4;
		list[4]= 10;
		
		double[] med = median(list, 6);
		System.out.println(Arrays.toString(med));
		
	}
	

}
