/*******************************************************************************
*   Copyright 2014 Analog Devices, Inc.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
********************************************************************************/

package com.analog.lyric.dimple.jsproxy;

import netscape.javascript.JSException;

import com.analog.lyric.collect.BitSetUtil;
import com.analog.lyric.dimple.factorfunctions.core.FactorTable;
import com.analog.lyric.dimple.factorfunctions.core.FactorTableRepresentation;
import com.analog.lyric.dimple.factorfunctions.core.IFactorTable;
import com.analog.lyric.dimple.model.domains.DiscreteDomain;
import com.analog.lyric.dimple.model.domains.JointDomainIndexer;
import com.analog.lyric.dimple.model.variables.Discrete;

/**
 * 
 * @since 0.07
 * @author Christopher Barber
 */
public class JSFactorTable extends JSProxyObjectWithApplet<IFactorTable>
{
	/*--------------
	 * Construction
	 */

	/**
	 * @param function
	 * @since 0.07
	 */
	JSFactorTable(DimpleApplet applet, IFactorTable factorTable)
	{
		super(applet, factorTable);
	}

	JSFactorTable(DimpleApplet applet, Object[] domainsOrVariables)
	{
		super(applet, createTable(domainsOrVariables));
	}
	
	/**
	 * Returns a new copy of the factor table.
	 */
	public JSFactorTable copy()
	{
		return new JSFactorTable(getApplet(), _delegate.clone());
	}
	
	private static IFactorTable createTable(Object[] domainsOrVariables)
	{
		DiscreteDomain[] domains = new DiscreteDomain[domainsOrVariables.length];
		for (int i = domains.length; --i>=0;)
		{
			final Object obj = domainsOrVariables[i];
			
			if (obj instanceof DiscreteDomain)
			{
				domains[i] = (DiscreteDomain)obj;
			}
			else if (obj instanceof JSDiscreteDomain)
			{
				domains[i] = ((JSDiscreteDomain)obj).getDelegate();
			}
			else if (obj instanceof Discrete)
			{
				domains[i] = ((Discrete)obj).getDomain();
			}
			else if (obj instanceof JSVariable)
			{
				JSDomain<?> jsdomain = ((JSVariable)obj).domain();
				if (jsdomain.isDiscrete())
				{
					domains[i] = jsdomain.getDelegate().asDiscrete();
				}
			}
			
			if (domains[i] == null)
			{
				throw new JSException(String.format("'%s' is not a discrete domain or variable", obj));
			}
		}
		return FactorTable.create(domains);
	}
	
	/*-----------------------
	 * JSFactorTable methods
	 */
	
	public double getEnergyForIndices(int[] indices)
	{
		return _delegate.getEnergyForIndices(indices);
	}
	
	public double getEnergyForElements(Object[] values)
	{
		return _delegate.getEnergyForElements(values);
	}
	
	public double getWeightForIndices(int[] indices)
	{
		return _delegate.getWeightForIndices(indices);
	}

	public double getWeightForElements(Object[] values)
	{
		return _delegate.getWeightForElements(values);
	}
	
	/**
	 * Returns the number of dimensions indexed by the factor table.
	 * @since 0.07
	 */
	public int getDimensions()
	{
		return _delegate.getDimensions();
	}
	
	/**
	 * Returns the discrete domains that are indexed by the factor table.
	 * <p>
	 * The length will be equal to {@link #getDimensions()}.
	 * @since 0.07
	 */
	public JSDiscreteDomain[] getDomains()
	{
		JSDomainFactory factory = getApplet().domains;
		final JointDomainIndexer domains = _delegate.getDomainIndexer();
		final int size = domains.size();
		final JSDiscreteDomain[] jsdomains = new JSDiscreteDomain[size];
		for (int i = size; --i>=0;)
		{
			jsdomains[i] = factory.wrap(domains.get(i));
		}
		return jsdomains;
	}
	
	public String getRepresentation()
	{
		return _delegate.getRepresentation().name();
	}
	
	public void normalize()
	{
		if (_delegate.isDirected())
		{
			_delegate.normalizeConditional();
		}
		else
		{
			_delegate.normalize();
		}
	}
	
	public void setDirected(int[] outputIndices)
	{
		_delegate.setDirected(BitSetUtil.bitsetFromIndices(getDimensions(), outputIndices));
	}
	
	public void setEnergyForIndices(double energy, int[] indices)
	{
		_delegate.setEnergyForIndices(energy, indices);
	}
	
	public void setEnergyForElements(double energy, Object[] values)
	{
		_delegate.setEnergyForElements(energy, values);
	}

	public void setRepresentation(String representation)
	{
		_delegate.setRepresentation(FactorTableRepresentation.valueOf(representation));
	}
		
	public void setWeightForIndices(double energy, int[] indices)
	{
		_delegate.setWeightForIndices(energy, indices);
	}

	public void setWeightForElements(double energy, Object[] values)
	{
		_delegate.setWeightForElements(energy, values);
	}

	public void setWeights(int[][] indices, double[] weights)
	{
		_delegate.setWeightsSparse(indices, weights);
	}
}
