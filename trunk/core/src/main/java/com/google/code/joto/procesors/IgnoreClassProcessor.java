/**
 * 
 */
package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

/**
 * This processor will ignore the classes that it matches to and will return a null on the
 * {@link #processThis(Object, ReverseEngineerData, int, ProcessMoreCallback)} method.
 * @author epere4
 * @author liliana.nu
 */
public class IgnoreClassProcessor
    implements CustomProcessor
{

    /** classNamePatterns */
    private final Pattern[] classNamePatterns;

    /**
     * Creates an empty {@link IgnoreClassProcessor} that will match nothing.
     */
    public IgnoreClassProcessor()
    {
        this.classNamePatterns = new Pattern[0];
    }

    /**
     * Creates a {@link CustomProcessor} that will ignore all the objects whose class's full class
     * name matches with any of the classNameRegExp supplied.
     * @param classNameRegExp
     */
    @SuppressWarnings("null")
    public IgnoreClassProcessor( String... classNameRegExp )
    {
        this.classNamePatterns = new Pattern[classNameRegExp == null ? 0 : classNameRegExp.length];
        for ( int i = 0; i < classNameRegExp.length; i++ )
        {
            this.classNamePatterns[i] = Pattern.compile( classNameRegExp[i] );
        }
    }

    /**
     * Creates a {@link CustomProcessor} that will ignore all the objects whose class's full class
     * name matches with any of the classNamePatterns supplied.
     * @param classNamePatterns
     */
    public IgnoreClassProcessor( Pattern... classNamePatterns )
    {
        if ( classNamePatterns == null )
        {
            this.classNamePatterns = new Pattern[0];
        }
        else
        {
            this.classNamePatterns = classNamePatterns;
        }
    }

    /**
     * @see com.google.code.joto.CustomProcessor#canProcessThis(java.lang.Object)
     */
    public boolean canProcessThis( Object objectToProcess )
    {
        if ( objectToProcess == null )
        {
            return false;
        }
        String clazzName = objectToProcess.getClass().getName();
        for ( Pattern classNamePattern : classNamePatterns )
        {
            if ( classNamePattern.matcher( clazzName ).matches() )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.google.code.joto.CustomProcessor#processThis(java.lang.Object,
     *      com.google.code.joto.ReverseEngineerData, int, com.google.code.joto.ProcessMoreCallback)
     */
    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        concat( sharedData, "null" );
    }

    /**
     * Creates and returns an {@link IgnoreClassProcessor} that will match for classes.
     * @param clazzes
     * @return an {@link IgnoreClassProcessor} configured to match against clazzes.
     */
    @SuppressWarnings("null")
    public static IgnoreClassProcessor createForClasses( Class<?>... clazzes )
    {
        String[] clazzNames = new String[clazzes == null ? 0 : clazzes.length];
        for ( int i = 0; i < clazzNames.length; i++ )
        {
            clazzNames[i] = clazzes[i].getName();
        }
        return createForClassNames( clazzNames );
    }

    /**
     * Creates and returns an {@link IgnoreClassProcessor} that will match for full class names
     * (that is, the class name with the package).
     * @param clazzNames
     * @return an {@link IgnoreClassProcessor} configured to match against clazzNames.
     */
    @SuppressWarnings("null")
    public static IgnoreClassProcessor createForClassNames( String... clazzNames )
    {
        Pattern[] patterns = new Pattern[clazzNames == null ? 0 : clazzNames.length];
        for ( int i = 0; i < patterns.length; i++ )
        {
            patterns[i] = Pattern.compile( Matcher.quoteReplacement( clazzNames[i] ) );
        }
        return new IgnoreClassProcessor( patterns );
    }

    /**
     * Creates and returns an {@link IgnoreClassProcessor} that will match for simple class names
     * (that is, the class name without the package).
     * @param simpleClazzNames
     * @return an {@link IgnoreClassProcessor} configured to match against simpleClazzNames.
     */
    @SuppressWarnings("null")
    public static IgnoreClassProcessor createForSimpleClassNames( String... simpleClazzNames )
    {
        Pattern[] patterns = new Pattern[simpleClazzNames == null ? 0 : simpleClazzNames.length];
        for ( int i = 0; i < patterns.length; i++ )
        {
            patterns[i] = Pattern.compile( ".*\\." + Matcher.quoteReplacement( simpleClazzNames[i] ) );
        }
        return new IgnoreClassProcessor( patterns );
    }
}
