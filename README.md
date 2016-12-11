# AuthMe Integration Demo
This is a small Minecraft plugin that shows how to hook into [AuthMe](https://github.com/AuthMe-Team/AuthMeReloaded).

- [1. Declare AuthMe dependency](#1-declare-authme-dependency)
  - [1.1. pom.xml](#11-pomxml)
  - [1.2. plugin.yml](#12-pluginyml)
- [2. Interacting with AuthMe](#2-interacting-with-authme)
  - [2.1. Listening to AuthMe events](#21-listening-to-authme-events)
  - [2.2. Using the AuthMe API](#22-using-the-authme-api)
- [3. Checking that AuthMe is available](#3-checking-that-authme-is-available)
  - [3.1. Check for availability on startup](#31-check-for-availability-on-startup)
  - [3.2. Check if AuthMe gets enabled or disabled](#32-check-if-authme-gets-enabled-or-disabled)
- [4. Working example](#4-working-example)

## 1. Declare AuthMe dependency
You need to declare a soft dependency to AuthMe in order to be able to use its functionality.

### 1.1. pom.xml
In your [pom.xml](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/pom.xml), 
declare the dependency to AuthMe.

```xml
    <repositories>
        <!-- Xephi repo -->
        <repository>
            <id>xephi-repo</id>
            <url>http://ci.xephi.fr/plugin/repository/everything/</url>
        </repository>
    </repositories>
    <dependencies>
        <!-- AuthMe -->
        <dependency>
            <groupId>fr.xephi</groupId>
            <artifactId>authme</artifactId>
            <version>5.2-SNAPSHOT</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
```

- Adding Xephi's repository to `<repositories>` tells Maven to (also) look there for the JAR
- Note the `<optional>true</optional>`. This means it will be available in your IDE and at compile time 
  but it means your plugin doesn't absolutely need it to work. It won't be included in your JAR file.
  
### 1.2. plugin.yml
Add AuthMe as a soft dependency in your [plugin.yml](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/resources/plugin.yml) 
so that Bukkit knows of this dependency.

```yml
softdepend:
    - AuthMe
```

## 2. Interacting with AuthMe
Two ways are available to interact with AuthMe:

1. Listen to AuthMe events
1. Use AuthMe's API

### 2.1. Listening to AuthMe events
It is best to create a separate class (or classes) for listening to 
[AuthMe events](http://ci.xephi.fr/job/AuthMeReloaded/javadoc/fr/xephi/authme/events/package-tree.html). 
As with regular Bukkit events, simply declare `@EventHandler` methods that take an AuthMe event as argument.
```java
import fr.xephi.authme.events.LoginEvent;

@EventHandler
public void onLogin(LoginEvent event) {
  System.out.println(event.getPlayer() + " has logged in!");
}
```

### 2.2. Using the AuthMe API
The AuthMe [NewAPI class](http://ci.xephi.fr/job/AuthMeReloaded/javadoc/fr/xephi/authme/api/NewAPI.html) allows you 
to perform various operations, such as registering a new name or querying AuthMe if a username password combination 
is correct. It is suggested that you wrap all method calls to the AuthMe API class with a class of your own. 
This way you can easily manage loading and unloading the API (see next chapter).

```java
import fr.xephi.authme.api.NewAPI;

// This should be the only class that uses AuthMe's NewAPI
public class AuthMeHook {
  private NewAPI authMeApi;

  // We will see when it's safe to invoke this in the next chapter
  public void initializeHook() {
    authMeApi = NewAPI.getInstance();
  }

  public boolean registerPlayer(String name, String password) {
    if (authMeApi != null) { // check that the API is loaded
      return authMeApi.registerPlayer(name, password);
    }
    return false;
  }
}
```

## 3. Checking that AuthMe is available
Before you can interact with AuthMe, you need to check in your plugin that AuthMe is present and enabled on the server.
The best way to manage this cleanly is to isolate your interactions with AuthMe in separate classes, as mentioned above.

### 3.1. Check for availability on startup
In your plugin's main class, check that AuthMe is enabled before executing actions
that require access to AuthMe members:
```java
@Override
public void onEnable() {
  AuthMeHook authMeHook = new AuthMeHook();
  // other initializations...
  
  if (getServer().getPluginManager().isPluginEnabled("AuthMe")) {
    // it's safe to get AuthMe's NewAPI instance, and so forth...
    getServer().getPluginManager().registerEvents(new AuthMeListener(), this);
    authMeHook.initializeHook();
  }
}
```

If you skip this check, AuthMe is not used, and, for instance, you register an event listener with AuthMe events, 
you will encounter a `ClassDefNotFound` error as AuthMe isn't present on the server and so it doesn't know about those
classes. Checking with the PluginManager that AuthMe is enabled prevents this.

##### Tip: Check for API availability _within_ the hooks class
If you look at the Java code above, notice that `AuthMeHook` is _always_ initialized (i.e. it is never `null`). 
The class checks internally whether or not it is hooked with AuthMe (cf. `authMeApi != null` check in 2.2). This way
you can always call the methods of your class and don't have to worry about performing any checks outside of the class.

### 3.2. Check if AuthMe gets enabled or disabled
It is recommend that you check when AuthMe gets enabled or disabled as well. This should rarely occur but guarantees
that your plugin will run smoothly.

```java
// You can put this in any listener class; this is a standard Bukkit event
@EventHandler 
public void onDisable(PluginDisableEvent event) {
  if ("AuthMe".equals(event.getPlugin().getName())) {
    authMeHook.disableHook(); // set the NewAPI field to null
  }
}

@EventHandler 
public void onEnable(PluginEnableEvent event) {
  if ("AuthMe".equals(event.getPlugin().getName())) {
    authMeHook.initializeHook();
  }
}
```

## 4. Working example
This repository contains the source code of a working plugin that interacts with AuthMe and should provide an
example for all points in this document.

- 1.1. and 1.2: see pom.xml and plugin.yml
- 2.1: [AuthMeListener](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/java/ch/jalu/authme/integrationdemo/listener/AuthMeListener.java)
  listens to AuthMe events. When a player wants to log in, there is a 10% chance that the login request will not
  be granted (in `onPrelogin()`). Once a player has been logged in, the listener will make the player output a
  random greeting (in `onLogin()`).
- 2.2: [AuthMeHook](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/java/ch/jalu/authme/integrationdemo/service/AuthMeHook.java)
  manages the interactions with AuthMe's NewAPI class. It is used in [ExistsCommand](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/java/ch/jalu/authme/integrationdemo/command/ExistsCommand.java),
  which implements a command that tells players whether a username is registered or not (e.g. `/exists test`).
- 3.1: [SamplePlugin](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/java/ch/jalu/authme/integrationdemo/SamplePlugin.java)
  is the main plugin class and defines a `registerAuthMeComponents()` method. At the end of `onEnable()` we check if
  AuthMe is enabled and if so, we invoke `registerAuthMeComponents()`.
- 3.2: [BukkitListener](https://github.com/ljacqu/AuthMe_integration_demo/blob/master/src/main/java/ch/jalu/authme/integrationdemo/listener/BukkitListener.java)
  listens for `PluginEnableEvent` and `PluginDisableEvent` and performs the appropriate actions if the event is for AuthMe.
