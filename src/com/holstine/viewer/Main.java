package com.holstine.viewer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box.Filler;

import com.holstine.viewer.SerialIngester.IngestVectorObserver;

import processing.core.PApplet;
import processing.serial.Serial;

public class Main extends PApplet implements IngestVectorObserver  {

	int lf = 10; // Linefeed in ASCII

	String myString = null;
	Serial myPort; // The serial port
	
	List<float[]> points = new ArrayList<float[]>();
	float[] currentPoint = new float[3];
	SerialIngester ingester = SerialIngester.getInstance();
	float[] prevPoint = new float[3];
	private boolean newPoint =false;
	float[] screenPoint = new float [3];
	float[] centerPoint = new float [3];
	SlamProcessor slamProcessor = new SlamProcessor();
	
	public static void main(String[] args) {
		PApplet.main ("com.holstine.viewer.Main");
	}

	@Override
	public void settings() {
		size(1000, 1000);
	}

	@Override
	public void setup() {
		fill(120, 50, 240);

		ingester.addObserver(this);
		ingester.addObserver(slamProcessor);
		
		centerPoint[0]= width/2;
		centerPoint[1]= height/2;

	}

	@Override
	public void draw() {
		if(newPoint  && currentPoint != null){
			
	
			
			stroke(255);
			fill(255,255,255);
			rect(0, 0, 1000, 1000);
			
			drawSlamCells();
			
			screenPoint  = toScreenCoords( currentPoint[0], currentPoint[1]);
			
			points.add(screenPoint);
			
			stroke(255);
			line(centerPoint[0],centerPoint[1],screenPoint[0],screenPoint[1] );
			stroke(0);
			fill(250,0,0 ); 
			ellipse(screenPoint[0],screenPoint[1], 1, 1);
			newPoint =false;
			
			ellipse(screenPoint[0],screenPoint[1],12, 12); 
			fill(250,255,0 ); 
			for(float[] p:points) {
			
				ellipse(p[0],p[1], 6, 6);
			}
		}
		
	
				  
	}

	@Override
	public void newVector(float[] vector) {
		prevPoint = currentPoint;
		 currentPoint = vector;
		 newPoint = true; 
	}

	public void drawSlamCells() {
	
		SlamCell candidate = slamProcessor.candidateCell;
		drawCell(candidate);
	}

	private void drawCell(SlamCell candidate) {
		if(candidate == null) return;
	
	
		
//		line(screenCoordsMax[0], screenCoordsMax[1], screenCoordsMin[0], screenCoordsMin[1]);
		
		drawCell(candidate.lowerLeft);
		drawCell(candidate.upperLeft);
		drawCell(candidate.lowerRight);
		drawCell(candidate.upperRight);
		
		if(candidate.numberOfvalues >0) {
			float[] screenCoordsMean = toScreenCoords(candidate.meanX, candidate.meanY); 
		
			fill(100 );
			
			float[] screenCoordsMax = toScreenCoords(candidate.maxX, candidate.maxY);
			float[] screenCoordsMin = toScreenCoords(candidate.minX, candidate.minY);
			
			float width = screenCoordsMin[0] - screenCoordsMax[0];
			float height = screenCoordsMin[1] - screenCoordsMax[1];
			
			float[] point1 = toScreenCoords(candidate.point1X, candidate.point1Y);
			float[] point2 = toScreenCoords(candidate.point2X, candidate.point2Y);
			
			float[] point1Upper = toScreenCoords(candidate.point1X, candidate.point1Y + candidate.getStandardDeviations());
			float[] point2Upper = toScreenCoords(candidate.point2X, candidate.point2Y + candidate.getStandardDeviations());
			
			float[] point1Lower = toScreenCoords(candidate.point1X, candidate.point1Y - candidate.getStandardDeviations());
			float[] point2Lower = toScreenCoords(candidate.point2X, candidate.point2Y - candidate.getStandardDeviations());
			
			ellipse(screenCoordsMean[0], screenCoordsMean[1],5,5);
			System.out.println(candidate.getStandardDeviations());
			rect(screenCoordsMax[0],
					screenCoordsMax[1],width,height,8);
//			System.out.printf("minY %f   point1Y %f point2Y %f maxY %f \n", 	
//					candidate.minY, candidate.point1Y, candidate.point2Y, candidate.maxY);
			
//			System.out.printf("min  %f   point1  %f point2  %f max  %f \n", 	
//					screenCoordsMin[1], point1[1], point2[1], screenCoordsMax[1]);
			fill(250,0,0 ); 
			
			line(point1[0],
					point1[1], 
					point2[0],
					point2[1]);
			line(point1Upper[0],
					point1Upper[1], 
					point2Upper[0],
					point2Upper[1]);
			line(point1Lower[0],
					point1Lower[1], 
					point2Lower[0],
					point2Lower[1]);
 		ellipse(screenCoordsMean[0], screenCoordsMean[1],(int)Math.sqrt(candidate.linearLeastSquares.variance),(int) candidate.linearLeastSquares.variance);
		
		}
		
		
	}
	
	float[] toScreenCoords(float x, float y) {
		float[] returnVector = new float[2];
		returnVector[1] = centerPoint[1] -y;
		returnVector[0] = -x + centerPoint[0];
		
		return returnVector;
	}
	
}
