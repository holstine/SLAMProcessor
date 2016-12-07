package com.holstine.viewer;

import java.util.Collection;

import com.holstine.viewer.SerialIngester.IngestVectorObserver;

public class SlamProcessor implements IngestVectorObserver {
	private static final float MIN_Y = -512;
	private static final float MAX_X = 512;
	private static final float MAX_Y = 512;
	private static final float MIN_X = -512;
	SlamCell rootCell;

	SlamCell candidateCell;
	
	float[] position;

	public SlamProcessor() {
		rootCell = new SlamCell(MAX_X, MAX_Y, MIN_X, MIN_Y);
		
		candidateCell = new SlamCell(MAX_X, MAX_Y, MIN_X, MIN_Y);
	}

	public void newCell() {
		candidateCell = new SlamCell(MAX_X, MAX_Y, MIN_X, MIN_Y);
	}
	
	@Override 
	public void newVector(float[] data) { 
		candidateCell.setPosition(position);
		candidateCell.add(data);
	}

	public void processCandidateCell() {

		float[] differenceVector = rootCell.difference(candidateCell);
		
		position[0] = differenceVector[0] + position[0];
		position[1] = differenceVector[1] + position[1];
		position[2] = differenceVector[2] + position[2];
		
		candidateCell.setPosition(position);
		rootCell.add(candidateCell);
	}

	/**
	 * take two cells and come up with a list of positions 
	 * that are moving or different from the other  
	 * @param a
	 * @param b
	 * @return the return collection has array defined by [ x , y , mag, dirX, dirY]
	 */
	public static Collection< float[] >  findMovement(SlamCell a, SlamCell b) {
		
		
		
		
		return null;
		
	}
}
