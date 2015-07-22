Nucleus
=======

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Nucleus-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1379)

Nucleus is an Android library, which utilizes the [Model-View-Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern
to properly connect background tasks with visual parts of an application.

[MVP introduction article](https://github.com/konmik/konmik.github.io/wiki/Introduction-to-Model-View-Presenter-on-Android)

[Javadoc](http://konmik.github.io/nucleus/)

### Introduction

Some time has been passed from the moment when [Model-View-Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
and [RxJava](https://github.com/ReactiveX/RxJava)
has completely superseded [Loader](http://developer.android.com/guide/components/loaders.html)
and [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) in our applications.

But there are still some defects in our applications even when using such advanced technologies:
* An application is unable to continue a background task execution after a configuration change.
* An application does not automatically restart a background task after a process restart.

While most applications work without such capabilities, their absence is an obvious bug that just sits there
and waits for a user who pressed "Login" button while being in a subway and switched to another application
because his network connection was too slow. Bugs that almost any application produce in such "edge cases"
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

* View class does not need to manually control the presenter's lifecycle.

### Include this library:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus:1.3.3'
}
```

For additional view classes `NucleusSupportFragment`, `NucleusFragmentActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus-support-v4:1.3.3'
}
```

For additional view classes `NucleusActionBarActivity`, `NucleusAppCompatActivity` include:

``` groovy
dependencies {
    compile 'info.android15.nucleus:nucleus-support-v7:1.3.3'
}
```

Hint: you can just copy/paste that classes code into ANY View class
to keep your View classes hierarchy as you like to.
