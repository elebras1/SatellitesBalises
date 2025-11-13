package org.simulation;

import org.model.Mobile;

public interface SimulationInterface {
    void onDataCollectionComplete(Mobile mobile);
    void onDataCollection(Mobile mobile);
    void onEndSync(Mobile mobile);
}
