-injars ../../ListVirtualHost.jar
-outjars ../../ListVirtualHostProGuard.jar

-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Classes/classes.jar

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-allowaccessmodification
-dontobfuscate
-overloadaggressively
-useuniqueclassmembernames
-defaultpackage ''
-ignorewarnings

# Keep - Applications. Keep all application classes that have a main method.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}


