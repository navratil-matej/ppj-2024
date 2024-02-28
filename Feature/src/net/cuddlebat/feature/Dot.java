package net.cuddlebat.feature;

import jdk.incubator.vector.VectorSpecies;

import java.util.Random;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;

public class Dot
{
	static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
	static final int ITERS = 500_000_003;
	
	public static void main(String[] args)
	{
		System.out.println("Using " + SPECIES.length() + " lanes");
		Random rand = new Random();
		var u = new float[ITERS];
		var v = new float[ITERS];
		for(int i = 0; i < ITERS; i++)
		{
			u[i] = (float)rand.nextGaussian();
			v[i] = (float)rand.nextGaussian();
		}
		long time;
		time = System.currentTimeMillis();
		System.out.println(simpleLoopDot(u, v));
		System.out.println("Simple loop took " + (System.currentTimeMillis() - time) + "ms");
		time = System.currentTimeMillis();
		System.out.println(vectorLoopDot(u, v));
		System.out.println("Vector loop took " + (System.currentTimeMillis() - time) + "ms");
	}

	private static float simpleLoopDot(float[] u, float[] v)
	{
		float sum = 0.0f;
		for(int i = 0; i < ITERS; i++)
		{
			sum += u[i] * v[i];
		}
		return sum;
	}

	private static float vectorLoopDot(float[] u, float[] v)
	{
		var sumVec = FloatVector.zero(SPECIES);
		int i = 0;
	    int to = SPECIES.loopBound(u.length);
	    for (; i < to; i += SPECIES.length())
	    {
	        var uVec = FloatVector.fromArray(SPECIES, u, i);
	        var vVec = FloatVector.fromArray(SPECIES, v, i);
	        sumVec = uVec.fma(vVec, sumVec);
	    }
	    float sum = sumVec.reduceLanes(VectorOperators.ADD);
	    for (; i < u.length; i++)
	    {
			sum += u[i] * v[i];
	    }
	    return sum;
	}
}
