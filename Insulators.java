package org.lac.bionimbus.client;

import java.util.Vector;

import org.lac.bionimbus.shared.Unit;

class Insulators extends ExperimentUnitTable
{
    Vector<String> matches = new Vector<String>();

    Insulators(String forUser)
    {
        super(true);
    }

    synchronized void fillMatches()
    {
        if (matches.size() == 0)
        {
            matches.add("BEAF32(SR)");
            matches.add("GAF-3558");
            matches.add("Su(Hw)-VC");
            matches.add("mod(mdg4)-VC");
        }
    }

    protected void updateUnits(Vector<Unit> result)
    {
        fillMatches();
        Vector<Unit> f = new Vector<Unit>();
        for (Unit r : result)
        {
            if (matches.contains(r.getAntiBody()))
                f.add(r);
        }
        super.updateUnits(f);
    }

    /*
    protected void updateExperiments(Vector<Experiment> result)
    {
        fillMatches();
        Vector<Experiment> f = new Vector<Experiment>();
        for (Experiment r : result)
            if (matches.contains(r.getAntibody()))
                f.add(r);
        super.updateExperiments(f);
    }
    */
}
