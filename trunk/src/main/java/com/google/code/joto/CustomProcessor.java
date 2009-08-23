package com.google.code.joto;

import com.google.code.joto.procesors.ArrayProcessor;
import com.google.code.joto.procesors.CollectionProcessor;
import com.google.code.joto.procesors.DefaultObjectProcessor;
import com.google.code.joto.procesors.EnumProcessor;
import com.google.code.joto.procesors.MapProcessor;

/**
 * This is the interface that must be implemented by the processors for each type of object.
 * <p>
 * Some processors are already included and used by the default implementation:
 * <ul>
 * <li>{@link ArrayProcessor}</li>
 * <li>
 * {@link CollectionProcessor}</li>
 * <li>
 * {@link DefaultObjectProcessor}</li>
 * <li>
 * {@link EnumProcessor}</li>
 * <li>
 * {@link MapProcessor}</li>
 * </ul>
 * @author Liliana.nu
 * @author epere4
 */
public interface CustomProcessor
{
    /**
     * Tells whether this processor can process or not the supplied objectToProcess. If this method
     * returns true, then the
     * {@link #processThis(Object, ReverseEngineerData, int, ProcessMoreCallback)
     * processThis()} method will be called. Otherwise, it won't be.
     * @param objectToProcess
     * @return true if this processor can process the objectToProcess or false otherwise.
     */
    boolean canProcessThis( Object objectToProcess );

    /**
     * This method should take care of handling the code creation for the kind of object received as
     * parameter. This method will be called with the same objectToProcess that returned
     * <code>true</code> in {@link #canProcessThis(Object)}.
     * <p>
     * The {@link ProcessMoreCallback#processThis(Object) callback} must be used if subsequent
     * objects needs to be processed recursively.
     * @param objectToProcess the object to process.
     * @param sharedData this is a data contained that can be accessed from the method.
     * @param depthLevel this increases with each level, starting from zero. Can be used for
     *            indenting code, perhaps.
     * @param callback the {@link ProcessMoreCallback#processThis(Object)} method from this object
     *            must be called for the subsequent objects that need to be processed recursively.
     * @throws Exception
     */
    void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                      ProcessMoreCallback callback )
        throws Exception;
}