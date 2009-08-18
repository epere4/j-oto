package com.google.code.joto;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SG0203967
 */
public class ReverseEngineerObjectWorkerParameter
{

    public ReverseEngineerObjectWorkerParameter()
    {
        this( null, null, null );
    }

    public ReverseEngineerObjectWorkerParameter( final Set<Class> classesToImport )
    {
        this( classesToImport, null, null );
    }

    public ReverseEngineerObjectWorkerParameter( final Set<Class> classesToImport, final StringBuilder sb,
                                                 List<CustomProcessor> listOfProcessors )
    {
        this.classesToImport = classesToImport == null ? new HashSet<Class>() : classesToImport;
        this.sb = sb == null ? new StringBuilder() : sb;
        this.listOfProcessors = listOfProcessors == null ? Collections.EMPTY_LIST : listOfProcessors;

    }

    public ReverseEngineerObjectWorkerParameter( List<CustomProcessor> listOfProcessors )
    {
        this( null, null, listOfProcessors );
    }

    /** objectsAlreadyProcessed */
    public final Set<Object> objectsAlreadyProcessed = new HashSet<Object>();

    /** classesAlreadyDeclared */
    public final Set<String> classesAlreadyDeclared = new HashSet<String>();

    /** classesToImport */
    public final Set<Class> classesToImport;

    /** sb */
    public final StringBuilder sb;

    /** listOfProcessors */
    public final List<CustomProcessor> listOfProcessors;
}
