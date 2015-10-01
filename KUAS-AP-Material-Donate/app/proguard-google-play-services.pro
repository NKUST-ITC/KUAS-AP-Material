## Google Play Services Analytics 8.1.0 specific rules ##
## https://github.com/dandar3/android-google-play-services-analytics/blob/master/proguard.txt ##

-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Needed when building against the Marshmallow SDK
-dontwarn org.apache.http.**

-dontwarn com.google.android.gms.internal.**