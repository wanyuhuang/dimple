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

import static java.util.Objects.*;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import org.eclipse.jdt.annotation.Nullable;

import com.analog.lyric.dimple.factorfunctions.core.FactorFunction;
import com.analog.lyric.dimple.model.core.FactorGraph;
import com.analog.lyric.dimple.model.core.INode;
import com.analog.lyric.dimple.model.factors.Factor;
import com.analog.lyric.dimple.model.variables.Discrete;
import com.analog.lyric.dimple.model.variables.Real;
import com.analog.lyric.dimple.model.variables.RealJoint;
import com.analog.lyric.dimple.model.variables.Variable;
import com.analog.lyric.dimple.solvers.interfaces.IFactorGraphFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Javascript API representation of a Dimple factor graph.
 * <p>
 * Delegates to an underlying Dimple {@link FactorGraph} object.
 * <p>
 * @since 0.07
 * @author Christopher Barber
 */
public class JSFactorGraph extends JSNode<FactorGraph>
{
	/*-------
	 * State
	 */
	
	/**
	 * The applet that created this graph.
	 */
	private final DimpleApplet _applet;
	
	/**
	 * Cache of proxy objects owned by this graph.
	 */
	private final Cache<Object, JSNode<?>> _proxyCache;
	
	/*--------------
	 * Construction
	 */
	
	JSFactorGraph(DimpleApplet applet, FactorGraph graph)
	{
		super(null, graph);
		_applet = applet;
		_proxyCache = CacheBuilder.newBuilder().build();
		_proxyCache.put(graph, this);
	}

	/*-----------------------
	 * JSProxyObject methods
	 */
	
	@Override
	public DimpleApplet getApplet()
	{
		return _applet;
	}
	
	/*-------------
	 * JSProxyNode
	 */
	
	@Override
	public JSFactorGraph getGraph()
	{
		return this;
	}
	
	@Override
	public JSNode.Type getNodeType()
	{
		return JSNode.Type.GRAPH;
	}
	
	/*----------------------
	 * JSFactorGraph methods
	 */

	/**
	 * Adds a new factor to the table using specified factor function and variables.
	 * @param function is either an already constructed {@link JSFactorFunction} instance
	 * or a string naming the function to create. If the latter, and the first argument of
	 * {@code args} is a Javascript object (represented by {@link JSObject}), that will be used
	 * to construct the function.
	 * @param args apart from the first argument, which may be used to construct the factor function, the
	 * rest of the array should contain {@link JSVariable}s defining the connections of the factor.
	 * @since 0.07
	 */
	public JSFactor addFactor(Object function, Object[] args)
	{
		int firstArg = 0;
		FactorFunction ff = null;

		if (function instanceof FactorFunction)
		{
			ff = (FactorFunction)function;
		}
		else if (function instanceof JSFactorFunction)
		{
			ff = ((JSFactorFunction)function)._delegate;
		}
		else if (function instanceof String)
		{
			String name = (String)function;
			if (args.length > 0 && args[0] instanceof JSObject)
			{
				firstArg = 1;
				ff = _applet.functions.create(name, (JSObject)args[0])._delegate;
			}
			else
			{
				ff = _applet.functions.create(name)._delegate;
			}
		}
		// TODO factor tables
		
		if (ff == null)
		{
			throw new IllegalArgumentException("Bad factor function argument: " + function);
		}
		
		final int nArgs = args.length - firstArg;
		Object[] unwrapped = new Object[nArgs];
		for (int i = 0; i < nArgs; ++i)
		{
			Object arg = args[firstArg + i];
			if (arg instanceof JSProxyObject<?>)
			{
				arg = ((JSProxyObject<?>)arg).getDelegate();
			}
			unwrapped[i] = arg;
		}
		
		return wrap(_delegate.addFactor(ff, unwrapped));
	}
	
	public JSFactor addFactor(Object function, Object arg1)
	{
		return addFactor(function, new Object[] { arg1 });
	}

	public JSFactor addFactor(Object function, Object arg1, Object arg2)
	{
		return addFactor(function, new Object[] { arg1, arg2 });
	}

	public JSFactor addFactor(Object function, Object arg1, Object arg2, Object arg3)
	{
		return addFactor(function, new Object[] { arg1, arg2, arg3 });
	}

	public JSFactor addFactor(Object function, Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return addFactor(function, new Object[] { arg1, arg2, arg3, arg4 });
	}

	public JSFactor addFactor(Object function, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5)
	{
		return addFactor(function, new Object[] { arg1, arg2, arg3, arg4, arg5 });
	}
	
	public JSFactor addTableFactor(Object[] variables)
	{
		return addFactor(_applet.functions.createTable(variables), variables);
	}
	
	/**
	 * Adds a new variable to the graph with given name and domain.
	 * @since 0.07
	 */
	public JSVariable addVariable(Object domain, String name)
	{
		if (!(domain instanceof JSDomain))
		{
			throw new JSException(String.format("%s is not a domain", domain));
		}
		
		JSDomain<?> jsdomain = (JSDomain<?>)domain;

		Variable variable = null;

		switch (jsdomain.getDomainType())
		{
		case DISCRETE:
			variable = new Discrete(requireNonNull(jsdomain._delegate.asDiscrete()));
			break;
		case REAL:
			variable = new Real(requireNonNull(jsdomain._delegate.asReal()));
			break;
		case REAL_JOINT:
			variable = new RealJoint(requireNonNull(jsdomain._delegate.asRealJoint()));
			break;
		}

		requireNonNull(variable).setName(name);
		_delegate.addVariables(variable);

		return wrap(variable);
	}
	
	/**
	 * Current solver configured for this graph.
	 * @since 0.07
	 */
	public @Nullable JSSolver getSolver()
	{
		IFactorGraphFactory<?> solver = _delegate.getFactorGraphFactory();
		return solver != null ? new JSSolver(getApplet(), solver) : null;
	}
	
	/**
	 * Returns variable in graph with given identifier.
	 * @since 0.07
	 */
	public @Nullable JSVariable getVariable(int id)
	{
		Variable variable = _delegate.getVariable(id);
		return variable != null ? wrap(variable) : null;
	}
	
	/**
	 * Returns variable in graph with given name.
	 * @since 0.07
	 */
	public @Nullable JSVariable getVariable(String name)
	{
		Variable variable = _delegate.getVariableByName(name);
		return variable != null ? wrap(variable) : null;
	}
	
	/**
	 * Initializes graph state in preparation for solve.
	 * <p>
	 * This is invoked automatically by {@link #solve()} and only needs to be
	 * called directly if manually invoking solve steps (e.g. {@link #solveOneStep()}).
	 * 
	 * @since 0.07
	 */
	public void initialize()
	{
		_delegate.initialize();
	}
	
	/**
	 * Sets configured solver to specified name.
	 * 
	 * @param solver is the name of a supported solver. See {@link JSSolverFactory#get}.
	 * @since 0.07
	 */
	public void setSolver(String solver)
	{
		setSolver(_applet.solvers.get(solver));
	}
	
	/**
	 * Sets currently configured solver.
	 * @since 0.07
	 */
	public void setSolver(JSSolver solver)
	{
		_delegate.setSolverFactory(solver.getDelegate());
	}

	/**
	 * Runs inference using currently configured solver.
	 * <p>
	 * The details will depend on the solver. For instance, when using SumProduct marginal beliefs will
	 * be computed for all variables, when using Gibbs samples may be generated.
	 * <p>
	 * This method automatically invokes {@link #initialize()} prior to inference.
	 * <p>
	 * @since 0.07
	 */
	public void solve()
	{
		_delegate.solve();
	}

	/**
	 * Runs one step of the solver.
	 * <p>
	 * The exact behavior will depend on the solver.
	 * <p>
	 * Unlike {@link #solve()} this does not first invoke {@link #initialize()}.
	 * @since 0.07
	 */
	public void solveOneStep()
	{
		_delegate.solveOneStep();
	}
	
	/*-----------------
	 * Internal methods
	 */
	
	@SuppressWarnings("unchecked")
	JSNode<?> wrap(INode node)
	{
		JSNode<?> jsnode = _proxyCache.getIfPresent(node);
		if (jsnode == null)
		{
			switch (node.getNodeType())
			{
			case FACTOR:
				jsnode = new JSFactor(this, (Factor)node);
				break;
			case GRAPH:
				jsnode = new JSFactorGraph(_applet, (FactorGraph)node);
				break;
			case VARIABLE:
				jsnode = new JSVariable(this, (Variable)node);
				break;
			}
		}
		return requireNonNull(jsnode);
	}
	
	JSFactor wrap(Factor factor)
	{
		JSFactor jsfactor = (JSFactor)_proxyCache.getIfPresent(factor);
		if (jsfactor == null)
		{
			jsfactor = new JSFactor(this, factor);
			_proxyCache.put(factor, jsfactor);
		}
		return jsfactor;
	}

	JSVariable wrap(Variable variable)
	{
		JSVariable jsvariable = (JSVariable)_proxyCache.getIfPresent(variable);
		if (jsvariable == null)
		{
			jsvariable = new JSVariable(this, variable);
			_proxyCache.put(variable, jsvariable);
		}
		return jsvariable;
	}
}
