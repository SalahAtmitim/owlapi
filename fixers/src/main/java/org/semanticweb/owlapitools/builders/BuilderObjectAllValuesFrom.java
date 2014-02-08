package org.semanticweb.owlapitools.builders;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

/** Builder class for OWLObjectAllValuesFrom */
public class BuilderObjectAllValuesFrom extends
        BaseObjectBuilder<OWLObjectAllValuesFrom, BuilderObjectAllValuesFrom> {
    /** builder initialized from an existing object
     * 
     * @param expected
     *            the existing object
     * @param df
     *            data factory */
    public BuilderObjectAllValuesFrom(OWLObjectAllValuesFrom expected,
            OWLDataFactory df) {
        this(df);
        withProperty(expected.getProperty()).withRange(expected.getFiller());
    }

    /** @param df
     *            data factory */
    public BuilderObjectAllValuesFrom(OWLDataFactory df) {
        super(df);
    }

    @Override
    public OWLObjectAllValuesFrom buildObject() {
        return df.getOWLObjectAllValuesFrom(property, range);
    }
}
