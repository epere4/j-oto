# Introduction #

I have released version joto-1.0.0. You can download it from the download section. A [request](http://jira.codehaus.org/browse/MAVENUPLOAD-2660) have been done to have it in maven central repo. I will let you know when it is ready.

# Details #

## Basic Usage ##
In order to use it, this is the minimum code:

```
ReverseEngineerObject reverseEngineerObject = new ReverseEngineerObject();
List<CustomProcessor> userSuppliedProcessors = null;
ReverseEngineerObjectResponse response = reverseEngineerObject.generateCode( someStrings, userSuppliedProcessors );
System.out.println(response);
```

You can check usage from [ReverseEngineerObjectUnitTest.java](http://code.google.com/p/j-oto/source/browse/trunk/core/src/test/java/com/google/code/joto/ReverseEngineerObjectUnitTest.java).

## Alternative Usage ##

Since the fix of [issue #16](https://code.google.com/p/j-oto/issues/detail?id=#16) the usability has been improved. There is now a ` ClassCreatorHelper ` that will create a source java file with the generated code. I allows you to supply the package name, the source folder where that package should be located and a classname prefix and suffix (between prefix and suffix there will be a number that increments automatically with each call to ` extractToDummyObject() `).

Here is a sample code taken from the [ClassCreatorHelperUnitTest.java](http://code.google.com/p/j-oto/source/browse/trunk/core/src/test/java/com/google/code/joto/util/ClassCreatorHelperUnitTest.java?spec=svn33&r=33)

```
File outputSrcFolder = new File( "target" );
String outputPackageName = "com.test.packages";
List<? extends CustomProcessor> userSuppliedProcessors = null;
ClassCreatorHelper classCreatorHelper = new ClassCreatorHelper( outputSrcFolder, outputPackageName,
                                                   userSuppliedProcessors );

SomeDummyClass someDummyClass = new SomeDummyClass();
someDummyClass.setValues();
File generatedClassFile = classCreatorHelper
    .extractToDummyObject( someDummyClass, "SomeDummyClass", "Scenary1" );

System.out.println( "Generated class file: " + generatedClassFile );
```