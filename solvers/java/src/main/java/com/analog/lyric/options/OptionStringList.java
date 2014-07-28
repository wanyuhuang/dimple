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

import net.jcip.annotations.Immutable;

import com.analog.lyric.collect.ArrayUtil;

/**
 * 
 * @since 0.07
 * @author Christopher Barber
 */
@Immutable
public final class OptionStringList extends AbstractOptionValueList<String>
{
	private static final long serialVersionUID = 1L;

	final public static OptionStringList EMPTY = new OptionStringList(ArrayUtil.EMPTY_STRING_ARRAY);
	
	/**
	 * @param elements
	 * @since 0.07
	 */
	public OptionStringList(String ... elements)
	{
		super(String.class, elements);
	}
}
