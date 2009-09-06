/**
 * 
 */
package com.google.code.joto;

import static com.google.code.joto.ReverseEngineerData.concat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.joto.exportable.Creator;
import com.google.code.joto.exportable.InstancesMap;
import com.google.code.joto.exportable.ReverseEngineerReflectionUtil;
import com.google.code.joto.procesors.ArrayProcessor;
import com.google.code.joto.procesors.BooleanProcessor;
import com.google.code.joto.procesors.CharacterProcessor;
import com.google.code.joto.procesors.CollectionProcessor;
import com.google.code.joto.procesors.DateProcessor;
import com.google.code.joto.procesors.DefaultObjectProcessor;
import com.google.code.joto.procesors.EnumProcessor;
import com.google.code.joto.procesors.MapProcessor;
import com.google.code.joto.procesors.NullProcessor;
import com.google.code.joto.procesors.NumberProcessor;
import com.google.code.joto.procesors.PrimitiveTypeProcessor;
import com.google.code.joto.procesors.StringProcessor;

/**
 * @author Liliana.nu
 * @author epere4
 */
public class ReverseEngineerObject
{

    public ReverseEngineerObjectResponse generateCode( Object objectToProcess,
                                                       List<? extends CustomProcessor> listOfUserSuppliedProcessors )
    {
        List<CustomProcessor> listOfProcessors = makeListOfDefaultProcessors();
        if ( listOfUserSuppliedProcessors != null )
        {
            listOfProcessors.addAll( 0, listOfUserSuppliedProcessors );
        }

        ReverseEngineerData sharedData = new ReverseEngineerData( listOfProcessors );

        addSomeClassesToImportAlways( sharedData );

        ReverseEngineerData.writeCreatorHeader( sharedData, objectToProcess );
        sharedData.concat( "final InstancesMap instancesMap = new InstancesMap();\n" );
        sharedData.concat( ReverseEngineerHelper.getTypeAsString( objectToProcess ) );
        sharedData.concat( " generatedObject = " );

        generateCodeRecursively( objectToProcess, sharedData, 0 );

        sharedData.concat( ";\n" );
        ReverseEngineerData.writeReturnStatement( sharedData, "generatedObject" );
        ReverseEngineerData.writeCreatorFooter( sharedData );

        String imports = makeImports( sharedData.getClassesToImport() );
        String code = sharedData.sb.toString();

        return new ReverseEngineerObjectResponse( imports, code );

    }

    private void addSomeClassesToImportAlways( ReverseEngineerData sharedData )
    {
        sharedData.addClassToImport( List.class );
        sharedData.addClassToImport( Set.class );
        sharedData.addClassToImport( Map.class );
        sharedData.addClassToImport( Creator.class );
        sharedData.addClassToImport( Collection.class );
        sharedData.addClassToImport( InstancesMap.class );
    }

    private LinkedList<CustomProcessor> makeListOfDefaultProcessors()
    {
        LinkedList<CustomProcessor> listOfDefaultProcessors = new LinkedList<CustomProcessor>();
        listOfDefaultProcessors.add( new NullProcessor() );
        listOfDefaultProcessors.add( new PrimitiveTypeProcessor() );
        listOfDefaultProcessors.add( new BooleanProcessor() );
        listOfDefaultProcessors.add( new CharacterProcessor() );
        listOfDefaultProcessors.add( new NumberProcessor() );
        listOfDefaultProcessors.add( new DateProcessor() );
        listOfDefaultProcessors.add( new StringProcessor() );
        listOfDefaultProcessors.add( new CollectionProcessor() );
        listOfDefaultProcessors.add( new MapProcessor() );
        listOfDefaultProcessors.add( new ArrayProcessor() );
        listOfDefaultProcessors.add( new EnumProcessor() );
        listOfDefaultProcessors.add( new DefaultObjectProcessor() );
        return listOfDefaultProcessors;
    }

    private void generateCodeRecursively( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel )
    {
        try
        {
            if ( objectToProcess != null )
            {
                sharedData.addClassToImport( objectToProcess.getClass() );
            }
            for ( CustomProcessor customProc : sharedData.listOfProcessors )
            {
                if ( customProc.canProcessThis( objectToProcess ) )
                {
                    customProc.processThis( objectToProcess, sharedData, depthLevel,
                                            new ProcessMoreCallbackImpl( sharedData, depthLevel + 1 ) );
                    break;
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    public static String makeImports( Set<Class> classesToImport )
    {
        StringBuilder sb = new StringBuilder();
        for ( Class clazz : classesToImport )
        {
            if ( !clazz.isArray() && !clazz.isAnonymousClass() && !clazz.isLocalClass() && !clazz.isMemberClass() )
            {
                concat( sb, "import ", clazz.getName(), ";\n" );
            }
        }
        concat( sb, "import static ", ReverseEngineerReflectionUtil.class.getName(), ".*;\n" );
        return sb.toString();
    }


    class ProcessMoreCallbackImpl
        implements ProcessMoreCallback
    {

        public ProcessMoreCallbackImpl( ReverseEngineerData sharedData, int depthLevel )
        {
            this.sharedData = sharedData;
            this.depthLevel = depthLevel;
        }

        final ReverseEngineerData sharedData;

        final int depthLevel;

        public void processThis( Object object )
        {
            ReverseEngineerObject.this.generateCodeRecursively( object, sharedData, depthLevel + 1 );
        }

    }

}
