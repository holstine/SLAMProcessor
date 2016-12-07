package com.holstine.viewer;

 public  class SlamCell {
	
	private static float MIN_SIZE =100f;
	
	 float maxX;
	 float maxY;
	 float minX;
	 float minY;
	
	 float midX;
	 float midY;
	
	 float meanY;
	 float sumSquaresY;
	
	 float numberOfvalues;
	
	 float meanX;
	 float sumSquaresX;
	 
	 RecursiveLinearLeastSquares linearLeastSquares = new RecursiveLinearLeastSquares();
	
	 SlamCell upperLeft;
	 SlamCell upperRight;
	 SlamCell lowerLeft;
	 SlamCell lowerRight;

	float point1X;

	float point2X;

	float  point1Y;

	float point2Y;

	private float deltaY;

	private float deltaX;
	
	 public SlamCell(float maxX, float maxY, float minX, float minY) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.minX = minX;
		this.minY = minY;
		
		this.midX = (maxX + minX)/2.0f;
		this.midY = (maxY + minY)/2.0f;

		this.deltaX = maxX - minX;
		this.deltaY = maxY - minY;
	}

	public void add(float[] vector) {
		if(vector[0] < minX) return;
		if(vector[0] > maxX) return;
		if(vector[1] < minY) return;
		if(vector[1] > maxY) return;
		
		boolean addedToChildren =addToChilderenCells(vector);
		if(! addedToChildren) addToCell(vector);
	}

	synchronized	private void addToCell(float[] vector) {
	
		double x = (vector[0] - minX  ) / (deltaX);
		double y = (vector[1] - minY   ) / (deltaY);
		
		x -= .5;  // -.5 to .5
		y -= .5;
		
		double [] a = {x,1};
		
		linearLeastSquares.addData(a, y);
		
		
		meanX = (meanX *numberOfvalues + vector[0] )/(numberOfvalues+1);
		meanY = (meanY *numberOfvalues + vector[1] )/(numberOfvalues+1);

		sumSquaresX += vector[0] * vector[0];
		sumSquaresY += vector[1] * vector[1];
		
		numberOfvalues ++;
		
		double[] coef = linearLeastSquares.getCoefficients();
		
		
		float rise = (float) (coef[0] *  (deltaX) *(deltaY) /deltaX); 
		float intercept =  (float) (coef[1] *deltaY + midY );

//		System.out.printf("slope: %6.5f intercept %6.0f\n",rise, intercept);
		
		
		point1Y = (float) (-.5 * rise + intercept);
		point2Y =  (float) (.5 *  rise + intercept);
		point1X = minX;
		point2X = maxX;
		
//		System.out.printf("minX %12.0f   point1X %12.0f point2X %12.0f maxX %12.0f \n", 
//				minX, point1X, point2X, maxX);
		
		
//		if (point1Y < minY) {
//			point1X = (float) (minY - intercept) / rise;
//			point1Y = minY;
//		}
//		if (point2Y < minY) {
//			point2X = (float) (minY - intercept) /  rise;
//			point2Y = minY;
//		}
//
//		if (point1Y > maxY) {
//			point1X = (float) (maxY -intercept) / rise;
//			point1Y = minY;
//		}
//		if (point2Y > maxY) {
//			point2X = (float) (maxY - intercept) / rise;
//			point2Y = maxY;
//		}
		
	}

	
	private boolean shouldIAddChildrenCells(){
		if(maxY-minY > MIN_SIZE) return true;
		return false;
	}
	
	private boolean addToChilderenCells(float[] vector){
		if (! shouldIAddChildrenCells()) return false;
		
		boolean upper = false;
		boolean left = false;
		
		if(vector[0] < midX ) left = true;
		if(vector[1] > midY ) upper = true;
		
		if( upper && left) {
			 if(upperLeft == null) {
//					System.out.printf("upperLeft   %3.0f    %3.0f  %3.0f %3.0f %3.0f  --  %3.0f  %3.0f\n" ,maxX-minX,  midX, maxY, minX, midY, vector[0], vector[1]);
				 upperLeft = new SlamCell(midX, maxY, minX, midY);
			 }
			 upperLeft.add(vector);
		}
		if( upper && !left) {
			 if(upperRight == null) {
//					System.out.printf("upperRight   %3.0f    %3.0f  %3.0f %3.0f %3.0f  --  %3.0f  %3.0f\n" ,maxX-minX,  maxX, maxY, midX, midY, vector[0], vector[1]);
				 upperRight = new SlamCell(maxX, maxY, midX, midY);
			 }
			 upperRight.add(vector);
		}
		
		
		if( !upper && left) {
			 if(lowerLeft == null) {
//					System.out.printf("lowerLeft   %3.0f    %3.0f  %3.0f %3.0f %3.0f  --  %3.0f  %3.0f\n" ,maxX-minX,  midX, midY, minX, minY, vector[0], vector[1]);
				 lowerLeft = new SlamCell(midX, midY, minX, minY);
			 }
			 lowerLeft.add(vector);
		}
		if( !upper && !left) {
			 if(lowerRight == null) {
//					System.out.printf("lowerRight   %3.0f    %3.0f  %3.0f %3.0f %3.0f  --  %3.0f  %3.0f\n" ,maxX-minX,  maxX, midY, midX, minY, vector[0], vector[1]);
				 lowerRight = new SlamCell(maxX, midY, midX, minY);
			 }
			 lowerRight.add(vector);
		}
		
		return true;
	}
	
	public float getStandardDeviations() {
		return linearLeastSquares.getStandardDeviations() * deltaY;
	}

	public float[] difference(SlamCell candidateCell) {
		upperLeft.difference(candidateCell);
		
		// TODO Auto-generated method stub
		return null;
	}

	public void setPosition(float[] position) {
		// TODO Auto-generated method stub
		
	}

	public void add(SlamCell candidateCell) {
		// TODO Auto-generated method stub
		
	}
	
}
