package org.csstudio.saverestore;

import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;

/**
 *
 * <code>CompletionNotifier</code> is a callback that is notified whenever a specific action is completed by the data
 * provider. When a specific UI part will trigger an action in the data provider, the data provider will complete the
 * action and notify the registered listeners about the completion of the event. The listener may take additional
 * actions to refresh the view.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface CompletionNotifier {

    /**
     * Called whenever a new branch was created.
     *
     * @param newBranch the new branch
     */
    void branchCreated(Branch newBranch);

    /**
     * Called whenever the repository was synchronised and as a consequence of the synchronisation the repository
     * changed. This event could override any other events.
     */
    void synchronised();

    /**
     * Called whenever the beamline set was saved, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was saved
     */
    void beamlineSetSaved(BeamlineSetData set);

    /**
     * Called whenever the beamline set is successfully deleted, but only if at the same time no updates due to
     * synchronisation were made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param set the set that was deleted
     */
    void beamlineSetDeleted(BeamlineSet set);

    /**
     * Called whenever the snapshot was saved, but only if at the same time no updates due to synchronisation were made.
     * If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the saved snapshot
     */
    void snapshotSaved(VSnapshot snapshot);

    /**
     * Called whenever the snapshot was tagged, but only if at the same time no updates due to synchronisation were
     * made. If the repository was also updated, only {@link #synchronised()} is called.
     *
     * @param snapshot the snapshot that was tagged
     */
    void snapshotTagged(Snapshot snapshot);

    /**
     * Called whenever the data was imported, but only if at the same time no updates due to synchronisation were made.
     * If the repository wa also updated, only {@link #synchronised()} is called.
     *
     * @param source the source of data
     * @param toBranch the destination branch
     * @param toBase the destination base level
     */
    void dataImported(BeamlineSet source, Branch toBranch, Optional<BaseLevel> toBase);
}
