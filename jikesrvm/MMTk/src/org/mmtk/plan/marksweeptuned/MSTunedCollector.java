/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package org.mmtk.plan.marksweeptuned;

import org.mmtk.plan.*;
import org.mmtk.vm.VM;
import org.mmtk.utility.Log;

import org.vmmagic.pragma.*;

/**
 * This class implements <i>per-collector thread</i> behavior and state for the
 * <i>MS</i> plan, which implements a full-heap mark-sweep collector.
 * <p>
 * 
 * Specifically, this class defines <i>MS</i> collection behavior (through
 * <code>trace</code> and the <code>collectionPhase</code> method).
 * <p>
 * 
 * @see MSTuned for an overview of the mark-sweep algorithm.
 *      <p>
 * 
 * @see MSTuned
 * @see MSTunedMutator
 * @see StopTheWorldCollector
 * @see CollectorContext
 */
@Uninterruptible
public class MSTunedCollector extends StopTheWorldCollector {

	/****************************************************************************
	 * Instance fields
	 */
	protected MSTunedTraceLocal fullTrace = new MSTunedTraceLocal(
			global().msTrace, null);;
	protected TraceLocal currentTrace = fullTrace;

	/****************************************************************************
	 * Collection
	 */

	/**
	 * Perform a per-collector collection phase.
	 * 
	 * @param phaseId
	 *            The collection phase to perform
	 * @param primary
	 *            Perform any single-threaded activities using this thread.
	 */
	@Inline
	@Override
	public void collectionPhase(short phaseId, boolean primary) {
		Log.writeln("MSTunedCollector: In the collectionPhase method");
		
		if (phaseId == MSTuned.PREPARE) {
			Log.writeln("MSTunedCollector: In the PREPARE phase");
			super.collectionPhase(phaseId, primary);
			fullTrace.prepare();
			return;
		}

		if (phaseId == MSTuned.CLOSURE) {
			Log.writeln("MSTunedCollector: In the CLOSURE phase");
			fullTrace.completeTrace();
			return;
		}

		if (phaseId == MSTuned.RELEASE) {
			Log.writeln("MSTunedCollector: In the RELEASE phase");
			fullTrace.release();
			super.collectionPhase(phaseId, primary);
			return;
		}

		super.collectionPhase(phaseId, primary);
	}

	/****************************************************************************
	 * Miscellaneous
	 */

	/** @return The active global plan as an <code>MS</code> instance. */
	@Inline
	private static MSTuned global() {
		Log.writeln("MSTunedCollector: In the global method");
		return (MSTuned) VM.activePlan.global();
	}

	/** @return The current trace instance. */
	@Override
	public final TraceLocal getCurrentTrace() {
		Log.writeln("MSTunedCollector: getCurrentTrace method");
		return currentTrace;
	}
}
