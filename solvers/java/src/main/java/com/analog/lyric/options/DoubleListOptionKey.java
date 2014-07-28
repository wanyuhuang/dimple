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

package com.analog.lyric.options;

/**
 * Key for options with list of dou les value.
 * @since 0.07
 * @author Christopher Barber
 */
public class DoubleListOptionKey extends OptionKey<OptionDoubleList>
{
	private static final long serialVersionUID = 1L;
	
	private final OptionDoubleList _defaultValue;
	
	/*--------------
	 * Construction
	 */
	
	/**
	 * @param declaringClass
	 * @param name
	 * @since 0.07
	 */
	public DoubleListOptionKey(Class<?> declaringClass, String name)
	{
		this(declaringClass, name, OptionDoubleList.EMPTY);
	}
	
	public DoubleListOptionKey(Class<?> declaringClass, String name, OptionDoubleList defaultValue)
	{
		super(declaringClass, name);
		_defaultValue = defaultValue;
	}

	public DoubleListOptionKey(Class<?> declaringClass, String name, double ... defaultValue)
	{
		this(declaringClass, name, new OptionDoubleList(defaultValue));
	}
	
	/*--------------------
	 * IOptionKey methods
	 */
	
	@Override
	public Class<OptionDoubleList> type()
	{
		return OptionDoubleList.class;
	}

	@Override
	public OptionDoubleList defaultValue()
	{
		return _defaultValue;
	}

	/*-----------------------------
	 * StringListOptionKey methods
	 */
	
	/**
	 * Sets option to specified list of values.
	 * @since 0.07
	 */
	public void set(IOptionHolder holder, double ... values)
	{
		set(holder, new OptionDoubleList(values));
	}

}