package com.analog.lyric.dimple.test.FactorFunctions.core;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.analog.lyric.dimple.factorfunctions.core.IFactorTable;
import com.analog.lyric.dimple.factorfunctions.core.NewFactorTable;
import com.analog.lyric.dimple.model.DiscreteDomain;
import com.google.common.base.Stopwatch;

public class FactorTablePerformanceTester
{
	private final IFactorTable _table;
	private final Stopwatch _timer;
	private final int _iterations;
	private final Random _random;
	public boolean showLog = true;
	
	/*---------------
	 * Construction
	 */
	
	FactorTablePerformanceTester(IFactorTable table, int iterations)
	{
		_table = table;
		_iterations = iterations;
		_random = new Random(42);
		_timer = new Stopwatch();
	}
	
	/*------------
	 * Test cases
	 */
	
	public double testEvalAsFactorFunction()
	{
		_random.setSeed(23);
		final Object [][] argrows = new Object[_iterations][];
		for (int i = 0; i < _iterations; ++i)
		{
			argrows[i] = randomArguments();
		}

		Runnable test = new Runnable() {
			@Override
			public void run()
			{
				for (Object[] args : argrows)
				{
					_table.evalAsFactorFunction(args);
				}
			}
		};
		
		return runTest("evalAsFactorFunction", test);
	}
	
	public double testGet()
	{
		_random.setSeed(42);
		final int [][] rows = new int[_iterations][];
		for (int i = 0; i < _iterations; ++i)
		{
			rows[i] = randomIndices();
		}
		
		Runnable test = new Runnable() {
			@Override
			public void run()
			{
				for (int[] indices : rows)
				{
					_table.get(indices);
				}
			}
		};
		
		return runTest("get", test);
	}

	public double testGetWeightIndexFromTableIndices()
	{
		_random.setSeed(42);
		final int [][] rows = new int[_iterations][];
		for (int i = 0; i < _iterations; ++i)
		{
			rows[i] = randomIndices();
		}
		
		Runnable test = new Runnable() {
			@Override
			public void run()
			{
				for (int[] indices : rows)
				{
					_table.getWeightIndexFromTableIndices(indices);
				}
			}
		};
		
		 return runTest("getWeightIndexFromTableIndices", test);
	}
	
	public double testGetWeightForIndices()
	{
		_random.setSeed(43);
		final int [][] rows = new int[500][];
		for (int i = 0; i < rows.length; ++i)
		{
			rows[i] = randomIndices();
		}

		Runnable test = new Runnable() {
			double total = 0.0;

			@Override
			public void run()
			{
				final NewFactorTable newTable = getNewTable();
				
				if (newTable != null)
				{
					runNewTable(newTable);
				}
				else
				{
					runOldTable(_table);
				}
			}
			
			private void runNewTable(NewFactorTable newTable)
			{
				for (int i = _iterations; --i>=0;)
				{
					for (int[] indices : rows)
					{
//						int ji = newTable.getDomainIndexer().jointIndexFromIndices(indices);
//						total += newTable.getWeightForJointIndex(ji);
						total += newTable.getWeightForIndices(indices);
					}
				}
			}

			private void runOldTable(IFactorTable table)
			{
				for (int i = _iterations; --i>=0;)
				{
					double[] weights = table.getWeights();
					for (int[] indices : rows)
					{
						total += weights[table.getWeightIndexFromTableIndices(indices)];
					}
				}
			}
		};
		
		 return runTest("getWeightForIndices", rows.length, "call", 42, test);
	}
	
	/*-----------------
	 * Private methods
	 */
	
	private NewFactorTable getNewTable()
	{
		if (_table instanceof NewFactorTable)
		{
			return (NewFactorTable)_table;
		}
		
		return null;
	}
	
	private Object[] randomArguments()
	{
		DiscreteDomain[] domains = _table.getDomains();
		Object[] arguments = new Object[domains.length];
		for (int i = 0; i < domains.length; ++i)
		{
			arguments[i] = domains[i].getElement(_random.nextInt(domains[i].size()));
		}
		
		return arguments;
	}
	
	private int[] randomIndices()
	{
		DiscreteDomain[] domains = _table.getDomains();
		int[] indices = new int[domains.length];
		for (int i = 0; i < domains.length; ++i)
		{
			indices[i] = _random.nextInt(domains[i].size());
		}
		return indices;
	}
	
	private double runTest(String name, int unitMultiplier, String unit, int seed, Runnable test)
	{
		_random.setSeed(seed);
		
		// Warmup
		test.run();
		
		_random.setSeed(seed);
		_timer.reset();
		_timer.start();
		test.run();
		_timer.stop();
		
		 long ns = _timer.elapsed(TimeUnit.NANOSECONDS);
		 return logTime(name, ns / (_iterations * unitMultiplier), unit);
	}
	
	private double runTest(String name, String unit, int seed, Runnable test)
	{
		return runTest(name, 1, unit, seed, test);
	}

	private double runTest(String name, Runnable test)
	{
		return runTest(name, "call", 42, test);
	}
	
	private double logTime(String name, double time, String unit)
	{
		if (showLog)
		{
			System.out.format("%s.%s: %f/%s\n", _table.getClass().getSimpleName(), name, time, unit);
		}
		return time;
	}
}