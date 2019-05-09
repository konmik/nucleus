Nucleus
=======

### Deprecation notice

Nucleus is not under develpment anymore. It turns out that Redux architecture scales way better than MVP/MVI/MVVM/MVxxx and I do not see further development of Nucleus valuable.

I recommend considering [ReKotlin](https://github.com/ReKotlin/ReKotlin) as a simple Redux implementation.

### Info

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Nucleus-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1379) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/info.android15.nucleus/nucleus/badge.png)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22info.android15.nucleus%22%20AND%20a%3A%22nucleus%22)


Nucleus is a simple Android library, which utilizes the [Model-View-Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern
to properly connect background tasks with visual parts of an application.

[MVP introduction article](https://github.com/konmik/konmik.github.io/wiki/Introduction-to-Model-View-Presenter-on-Android)

[Wiki](https://github.com/konmik/nucleus/wiki)

### Introduction

Some time has passed since [Model-View-Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
and [RxJava](https://github.com/ReactiveX/RxJava)
completely superseded [Loader](http://developer.android.com/guide/components/loaders.html)
and [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) in our applications.

But there are still some defects in our applications even when using such advanced technologies:
* An application is unable to continue a background task execution after a configuration change.
* An application does not automatically restart a background task after a process restart.

While most applications work without such capabilities, their absence is an obvious bug that just sits there
and waits for a user who pressed "Login" button while being in a subway and switched to another application
because his network connection was too slow. Bugs that almost any application produce in such cases
are numerous.

Android docs are covering such problems very briefly, take a look at:
[Processes and Threads - 4. Background process](http://developer.android.com/guide/components/processes-and-threads.html#Lifecycle)
*"If an activity implements its lifecycle methods correctly, and saves
its current state, killing its process will not have a visible effect on
the user experience, because when the user navigates back to the activity,
the activity restores all of its visible state."*

This is not true - there WILL be a visible effect because we're not restoring background tasks.
The application will restore it's visual state, but it will forget what it is *doing*.
So, if an application restores a progress bar, but does not restore the background task itself -
a user will see the usual "progress bar forever" bug.

### Nucleus' main features

* In case of configuration change Nucleus automatically re-attaches all running background tasks to the new View.
The application will not forget what it is doing.

* In case of process restart Nucleus automatically restarts background tasks.
Even when running on a low memory device or waiting for a long running background task completion,
the application is still reliable.

* The entire library has been built keeping [The Kiss Principle](https://people.apache.org/~fhanik/kiss.html) in mind.
Anyone can read and understand it easily.

* The library does not depend on Dagger, you don't need to write a whole class just to inject a presenter. One annotation
is all you need. Despite presenters are instantiated without Dagger, their dependencies can still be injected.

* Presenter in Nucleus is an external class that does not depend on View, this automatically prevents any troubles
that are connected with activity context leaks.

### History

At the moment of the first release, the library main idea was to be the simplest possible MVP implementation.

A couple of months later, I finally realized that RxJava has became the tool number one for smart background threads handling,
so RxPresenter appeared. Since that moment, the main focus shifted in the direction of RxJava support.

The correct lifecycle handling was something that seemed obvious to me from the beginning, so I did not make
an accent on this feature in the library description. However, since those times, more MVP libraries appeared,
now I need to differentiate Nucleus from other implementations. The library description
became: "Nucleus is a simple Android library, which utilizes the Model-View-Presenter pattern
*to properly connect background tasks with visual parts of an application*."

### Include this library:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus:6.0.0'
}
```

For additional view classes `NucleusSupportFragment`, `NucleusFragmentActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus-support-v4:6.0.0'
}
```

For additional view class `NucleusAppCompatActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus-support-v7:6.0.0'
}
```

ProGuard config:

```
-keepclassmembers class * extends nucleus.presenter.Presenter {
    <init>();
}
```

### For RxJava 2:


``` groovy
dependencies {
    compile 'info.android15.nucleus5:nucleus:7.0.0'
}
```

For additional view classes `NucleusSupportFragment`, `NucleusFragmentActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus5:nucleus-support-v4:7.0.0'
}
```

For additional view class `NucleusAppCompatActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus5:nucleus-support-v7:7.0.0'
}
```

Hint: you can just copy/paste those classes code into ANY View class
to keep your View classes hierarchy as you like to.

ProGuard config:

```
-keepclassmembers class * extends nucleus5.presenter.Presenter {
    <init>();
}
```
