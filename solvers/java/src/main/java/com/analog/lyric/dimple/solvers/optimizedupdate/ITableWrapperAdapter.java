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

package com.analog.lyric.dimple.solvers.optimizedupdate;

import com.analog.lyric.collect.Tuple2;
import com.analog.lyric.dimple.factorfunctions.core.IFactorTable;
import com.analog.lyric.util.misc.Internal;

/**
 * TableWrappers require an implementation of this interface, typically supplied by a SFactorGraph,
 * to access solver-specific functions.
 * 
 * @since 0.07
 * @author jking
 */
@Internal
public interface ITableWrapperAdapter
{
	double[] getSparseValues(IFactorTable factorTable);

	double[] getDenseValues(IFactorTable factorTable);

	IUpdateStep createSparseOutputStep(int outPortNum, TableWrapper tableWrapper);

	IUpdateStep createDenseOutputStep(int outPortNum, TableWrapper tableWrapper);

	IMarginalizationStep createSparseMarginalizationStep(TableWrapper tableWrapper,
		int inPortNum,
		int dimension,
		IFactorTable g_factorTable,
		Tuple2<int[][], int[]> g_and_msg_indices);

	IMarginalizationStep createDenseMarginalizationStep(TableWrapper tableWrapper,
		int inPortNum,
		int dimension,
		IFactorTable g_factorTable);

	double getSparseThreshold();
}