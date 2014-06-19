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

package com.analog.lyric.options.tests;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.analog.lyric.options.AbstractOptionHolder;
import com.analog.lyric.options.IOptionHolder;
import com.analog.lyric.options.IOptionKey;
import com.analog.lyric.util.misc.Nullable;

class ExampleOptionHolder extends AbstractOptionHolder
{
	private @Nullable ConcurrentMap<IOptionKey<?>,Object> _localOptions = null;
	private @Nullable IOptionHolder _parent;
	
	public ExampleOptionHolder()
	{
		_parent = null;
	}
	
	public ExampleOptionHolder(IOptionHolder parent)
	{
		_parent = parent;
	}
	
	@Override
	public ConcurrentMap<IOptionKey<?>, Object> createLocalOptions()
	{
		ConcurrentMap<IOptionKey<?>,Object> localOptions = _localOptions;
		if (localOptions == null)
		{
			localOptions = _localOptions = new ConcurrentHashMap<IOptionKey<?>,Object>();
		}
		return localOptions;
	}
	
	@Override
	public @Nullable ConcurrentMap<IOptionKey<?>, Object> getLocalOptions(boolean create)
	{
		return create? createLocalOptions() : _localOptions;
	}

	@Override
	public @Nullable IOptionHolder getOptionParent()
	{
		return _parent;
	}
}