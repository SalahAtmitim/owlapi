/**
 * 
 */
package org.semanticweb.owlapi.formats;

import javax.annotation.Nonnull;

import org.openrdf.rio.RDFFormat;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class N3DocumentFormatFactory extends
        AbstractRioRDFDocumentFormatFactory {

    private static final long serialVersionUID = 40000L;

    public N3DocumentFormatFactory() {
        super(RDFFormat.N3);
    }

    @Nonnull
    @Override
    public RioRDFDocumentFormat createFormat() {
        return new N3DocumentFormat();
    }
}