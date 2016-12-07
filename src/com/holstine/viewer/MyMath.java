package com.holstine.viewer;

public abstract class MyMath {
	static int n = 2;

	static double aPaT(double[] a, double[][] P) {
		return dot(a, mult(P, a));
	}

	static double[] scale(double a[], double b) {
		double[] tempVector = new double[n];
		for (int i = 0; i < a.length; i++) {
			tempVector[i] = b * a[i];
		}
		return tempVector;
	}

	static double[][] mult(double[][] a, double[][] b) {
		// a * b
		double[][] tempMatrix = new double[n][n];
		for (int k = 0; k < n; k++) {

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					tempMatrix[k][i] += a[k][j] * b[j][i];
				}
			}
		}
		return tempMatrix;
	}

	static double dot(double[] a, double[] b) {
		double value = 0;
		for (int i = 0; i < b.length; i++) {
			value += a[i] * b[i];
		}
		return value;
	}

	static double[] mult(double[][] a, double[] b) {
		double[] tempVector = new double[n];

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				tempVector[i] += b[j] * a[i][j];
			}
		}

		return tempVector;
	}

	static double[] add(double[] a, double[] b) {
		double[] tempVector = new double[n];

		for (int i = 0; i < b.length; i++) {
			tempVector[i] = a[i] + b[i];
		}

		return tempVector;
	}

	static double[][] subtract(double[][] a, double[][] b) {
		double[][] tempMatrix = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tempMatrix[i][j] = a[i][j] - b[i][j];
			}
		}
		return tempMatrix;
	}

	static double[][] outer(double[] a, double[] b) {
		double[][] tempMatrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tempMatrix[j][i] = a[j] * b[i];
			}
		}
		return tempMatrix;
	}

	static double[][] identity(double val) {
		double[][] tempMatrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tempMatrix[i][j] = (i == j) ? val : 0;
			}
		}
		return tempMatrix;
	}

	/* For calculating Determinant of the Matrix */
static	float determinant(float a[][], int k) {
		float s = 1, det = 0, b[][] = new float[k][k];
		int i, j, m, n, c;
		if (k == 1) {
			return (a[0][0]);
		} else {
			det = 0;
			for (c = 0; c < k; c++) {
				m = 0;
				n = 0;
				for (i = 0; i < k; i++) {
					for (j = 0; j < k; j++) {
						b[i][j] = 0;
						if (i != 0 && j != c) {
							b[m][n] = a[i][j];
							if (n < (k - 2))
								n++;
							else {
								n = 0;
								m++;
							}
						}
					}
				}
				det = det + s * (a[0][c] * determinant(b, k - 1));
				s = -1 * s;
			}
		}

		return (det);
	}

static	float[] [] cofactor(float num[][], int f) {
		float b[][] = new float[f][f], fac[][] =  new float[f][f];
		int p, q, m, n, i, j;
		for (q = 0; q < f; q++) {
			for (p = 0; p < f; p++) {
				m = 0;
				n = 0;
				for (i = 0; i < f; i++) {
					for (j = 0; j < f; j++) {
						if (i != q && j != p) {
							b[m][n] = num[i][j];
							if (n < (f - 2))
								n++;
							else {
								n = 0;
								m++;
							}
						}
					}
				}
				fac[q][p] = flipSigns(q + p) * determinant(b, f - 1);
			}
		
		}

//		transpose(num, fac, f);
		return fac;
	}

static	private float flipSigns(int j) {
		if (j % 2 == 0) return 1;
		return -1;
	}

	/* Finding transpose of matrix */
	static	float [][] transpose( float fac[][], int r) {
		int i, j;
		float b[][] =  new float[r] [r];

		for (i = 0; i < r; i++) {
			for (j = 0; j < r; j++) {
				b[i][j] = fac[j][i];
			}
		}
		return b;
	}
 
	static float[][] inverse (float[] [] matrix, int size){
		float b[][] =  new float[size] [size], inverse[][] =  new float[size][size], d;
		b = transpose(cofactor(matrix, size),size);
		
		d = determinant(matrix, size);
		int i, j;
		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				inverse[i][j] = b[i][j] / d;
			}
		}
		System.out.printf("\n\n\nThe inverse of matrix is : \n");

		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				System.out.printf("\t%f", inverse[i][j]);
			}
			System.out.printf("\n");
		}
		return inverse;
	}
}
