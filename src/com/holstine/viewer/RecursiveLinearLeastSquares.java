package com.holstine.viewer;

import static com.holstine.viewer.MyMath.*;
public class RecursiveLinearLeastSquares {
	// P is 2x2
	// K is 2x1
	// xs is 1x2
	// y is double
	// coeffiecients is 2x1
	private int n = 2; // number of parameters 
	private double[][] P;
	private double[] coefficients;
	double[] K;
	
	double lambda =.9  ; // memory factor (1 is complete memory )
	double variance = 0;
	float numberOfvalues;
	
	
	public RecursiveLinearLeastSquares() {
		P = identity(100);
		K =  new double[n];
		coefficients = new double [n];
	}
	
	/**
	 * Yang, Applied Numerical Methods using Matlab, pg 76. function [x,K,P] =
	 * rlse_online(aT_k1,b_k1,x,P) K = P*aT_k1’/(aT_k1*P*aT_k1’+1); %Eq.(2.1.17)
	 * the last part 1//(aT_k1*P*aT_k1’+1) is a single number x = x
	 * +K*(b_k1-aT_k1*x); %Eq.(2.1.16) the coefficients are modified by K times
	 * the inovation (diff between model and data) P = P-K*aT_k1*P; %Eq.(2.1.18)
	 * in this example it is 2x2 since two coefficeints
	 * 
	 * @param xs
	 *            the "x"s (the independent variables) for an equation aX+ b x =
	 *            [ x 1]
	 * @param y
	 *            [ the dependent or y value as in y = aX+b
	 * @param coefficients
	 *            the coefficents to be solved for [a b]
	 * @param P
	 *            ?
	 * @return
	 */
	double[] addData(double[] xs, double y) {
		numberOfvalues ++;
		K = scale(mult(P, xs), lambda / (aPaT(xs, P) + 1));
		// 2x1 = 2x2 . 2x1 / double
		double inovation = (y - dot(xs, coefficients) );
		coefficients = add(coefficients, scale(K, inovation /lambda ));
		// 2x1 = 2x1 + 2x1 * (double - 1x2 . 2x1

		P = subtract(P, mult(outer(K, xs), P));
		// 2x2 = 2x2 - (2x1 . 1x2 . 2x2 )
		
		variance = (lambda * variance * numberOfvalues + (lambda )* inovation * inovation)/ (numberOfvalues +1);
		
		return coefficients;

	}

	public float getStandardDeviations() {
		return (float) Math.sqrt(variance);
	}
	public double[] getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}
	
	/*
	 *
	 * n = 2 double [] (all data) vals =
	 * np.array([[3.0,4.0,6.0,3.0,8.0,7.0,5.0]]).T 
	 * double [] [] 2x2 P =  np.eye(n,n)*100. 
	 * double [] 2x1 x = np.zeros((n,1)) for k in
	 * range(len(vals)):
	 * 
	 * double [] 2x1 v = np.array([[k,1]]) x's [ itterator, 1] double [] 1x1
	 * vk = vals[ k , : ] (kth value, all columns (but there is only one) )
	 * y's
	 * 
	 * x,K,P = rls.rlse_online(v ,vk , x, P) print x
	 */

	/*
	 * %do_rlse clear xo = [2 1]’; %The true value of unknown coefficient
	 * vector NA = length(xo); x = zeros(NA,1); P = 100*eye(NA,NA); for k =
	 * 1:100 A(k,:) = [k*0.01 1]; b(k,:) = A(k,:)*xo +0.2*rand; [x,K,P] =
	 * rlse_online(A(k,:),b(k,:),x,P); end x % the final parameter estimate
	 * A\b % for comparison with the off-line processing (batch job)
	 */
	public static void main(String[] args) {
		RecursiveLinearLeastSquares rls = new RecursiveLinearLeastSquares();
		
		// 2 x +1 +rand =y
		double c1 = 2;
		double c0  =.1;
		
		for (int i = 1; i < 100;i ++) {
			
		double t = .01 * i;
			double y = (c1 + .001 * (Math.random() -.5  ))   *t + c0 +.001* (Math.random() -.5  );
		
			double[] a = { t, 1};
			rls.addData(a, y);
		}
		
		System.out.println( rls.getCoefficients()[1] +"  "+ rls.getCoefficients()[0] );
	}
}
