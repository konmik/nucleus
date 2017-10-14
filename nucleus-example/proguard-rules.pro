# Warning: rx.internal.utils.unsafe.*: can't find referenced class sun.misc.Unsafe
-dontwarn sun.misc.Unsafe
# Note: com.google.gson.internal.UnsafeAllocator: can't find dynamically referenced class sun.misc.Unsafe
-dontnote sun.misc.Unsafe
# Note: com.google.gson.internal.UnsafeAllocator accesses a declared field 'theUnsafe' dynamically
-dontnote com.google.gson.internal.UnsafeAllocator

# Warning: retrofit.appengine.UrlFetchClient: can't find referenced class com.google.appengine.api.urlfetch.*
-dontwarn com.google.appengine.api.urlfetch.*

# Warning: okio.Okio: can't find referenced class java.nio.file.*
-dontwarn okio.Okio

# Warning: okio.DeflaterSink: can't find referenced class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Note: duplicate definition of library class [*]
-dontnote org.apache.http.**
-dontnote android.net.http.**

# Note: com.squareup.okhttp.internal.Platform: can't find dynamically referenced class org.apache.harmony.xnet.provider.jsse.OpenSSLSocketImpl
-dontnote com.squareup.okhttp.internal.Platform

# Note: rx.internal.util.PlatformDependent accesses a field 'SDK_INT' dynamically
-dontnote rx.internal.util.PlatformDependent
