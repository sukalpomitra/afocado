package com.web.afocado;

import java.util.ArrayList;
import java.util.List;

public class ComputeSteps {

	public int computeSteps(final List<String> accelerometer)
	{
		int steps = 0;
		//Get accelerometer values
		List<Float> accX = convertListFromStringToFloat(accelerometer, Utils.X);
		List<Float> accY = convertListFromStringToFloat(accelerometer, Utils.Y);
		List<Float> accZ = convertListFromStringToFloat(accelerometer, Utils.Z);

		//Calculate the accelerometer magnitude
		List<Double> magnitude = calculateAccMagnitude(accX, accY, accZ);
		List<Double> filterMagnitude = calculateAccMagFilter(magnitude);
		filterMagnitude = applySmoother(filterMagnitude);
		List<Integer> stationary = calculateStationery(filterMagnitude);
		steps = calculateSteps(stationary);
		return steps;
	}

	protected List<Float> convertListFromStringToFloat(final List<String> input, final int axis)
	{
		List<Float> output = new ArrayList<Float>();
		for (int i = 0; i <= input.size() - 1; i++)
		{
			String element = input.get(i);
			output.add(Float.parseFloat(element.split(",")[axis]));
		}
		return output;
	}


	protected List<Double> calculateAccMagnitude(final List<Float> accX, final List<Float> accY, final List<Float> accZ)
	{
		List<Double> magnitude = new ArrayList<Double>();
		for (int i = 0; i <= accX.size() - 1; i++)
		{
			magnitude.add(Math.sqrt(Math.pow(accX.get(i), 2) + Math.pow(accY.get(i), 2) + Math.pow(accZ.get(i), 2)));
		}
		return magnitude;
	}

	protected List<Double> calculateAccMagFilter(final List<Double> magnitude)
	{
		List<Double> filterMagnitude = new ArrayList<Double>();
		if (magnitude.size()> 0 )
		{
			filterMagnitude.add(magnitude.get(0));
		}
		else
			filterMagnitude.add(0.0);
		for (int i = 1; i <= magnitude.size() - 1; i++)
		{
			filterMagnitude.add(Math.abs(Utils.REAL_ALPHA * (filterMagnitude.get(i-1) + magnitude.get(i) - magnitude.get(i-1))));
		}

		return filterMagnitude;
	}

	protected List<Double> applySmoother(final List<Double> filterMagnitude)
	{
		List<Double> smoothenedFilter = new ArrayList<Double>();
		double value = 0;
		if (filterMagnitude.size()> 0)
			value = filterMagnitude.get(0);
		smoothenedFilter.add(value);
		for (int i = 1; i <= filterMagnitude.size() - 1; i++)
		{
			double currentVal = filterMagnitude.get(i);
			value = value + ((currentVal - value) / Utils.SMOOTHING);
			smoothenedFilter.add(value);
		}
		return smoothenedFilter;

	}

	protected List<Integer> calculateStationery(final List<Double> smoothenedFilter)
	{
		List<Integer> stationary = new ArrayList<Integer>();
		for (int i = 0; i <= smoothenedFilter.size() - 1; i++)
		{
			if (smoothenedFilter.get(i) > 0.05)
				stationary.add(1);
			else
				stationary.add(0);
		}
		return stationary;
	}

	protected int calculateSteps(final List<Integer> stationary)
	{
		int steps = 0;
		if (stationary.size() < 2)
			return 0;

		for (int i = 1; i <= stationary.size() - 1; i++)
		{
			if ((stationary.get(i) - stationary.get(i - 1)) == -1)
				steps += 1;
		}
		return steps;
	}

}
