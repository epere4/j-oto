package com.google.code.joto.autogenerated;
import java.util.Collection;
import com.google.code.joto.exportable.Creator;
import java.util.Map;
import com.google.code.joto.Joto;
import com.google.code.joto.exportable.InstancesMap;
import java.util.List;
import java.lang.String;
import java.util.Set;
import java.lang.Object;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.*;

/** 
 * This class was automatically generated and it is present on the svn 
 * only as an example documentation.
 */
class JotoAutoGenerated {
  public static void main(String [] args) {
    System.out.println("Created instance: ");
    System.out.println(
new Creator<Joto>(){ 
public Joto create() {
final InstancesMap instancesMap = new InstancesMap();
Joto generatedObject = new Creator<Joto>(){ 
public Joto create() {
Joto obj = new Joto(  );
instancesMap.registerInstance( 23583040, obj);  /* @167d940 */
obj.setName("j-oto");
obj.setVersion("1.0");
return obj;
}}.create();
return generatedObject;
}}.create()
    );
  }
}
