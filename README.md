# AuthMe Integration Demo
This is a small Minecraft plugin that shows how to hook into [AuthMe](https://github.com/AuthMe-Team/AuthMeReloaded).

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

1. Listen to AuthMe events <!-- TODO: javadoc link -->
1. Use AuthMe's API <!-- TODO: javadoc link -->

### 2.1. Listening to AuthMe events
It is best to create a separate class (or classes) for listening to AuthMe events. As with regular Bukkit events, 
simply declare `@EventHandler` methods that take an AuthMe event as argument.
```java
import fr.xephi.authme.events.LoginEvent;

@EventHandler
public void onLogin(LoginEvent event) {
  System.out.println(event.getPlayer() + " has logged in!");
}
```

### 2.2. Using the AuthMe API
The AuthMe API allows you to perform various operations, such as registering a new name or querying AuthMe if a
username password combination is correct. It is suggested that you wrap all method calls to the AuthMe API class in
with a class of your own. This way you can easily manage loading and unloading the API (see next chapter).

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

If you skip this check and, for instance, register an event listener with AuthMe events, you will encounter a
`ClassDefNotFound` error as AuthMe isn't present on the server, and so the classes are unavailable (no way to get them!).
Checking with the PluginManager beforehand prevents this.

##### Tip: Check for API availability _within_ the hooks class
If you look at the Java code above, notice that `AuthMeHook` is _always_ initialized. The class checks internally
whether or not it is hooked with AuthMe (cf. `authMeApi != null` check in 2.2). Your code will be much more
difficult to maintain if your `authMeHook` is either `null` or present, as then you have to check for `null` before
_every_ time you want to use your AuthMe hook.

### 3.2. Check if AuthMe gets enabled or disabled
It is recommend that you check when AuthMe gets enabled or disabled as well. This should rarely occur but guarantees
that your plugin will run smoothly.

```java
// You can put thisin any listener class; this is a standard Bukkit event
@EventHandler 
public void onDisable(PluginDisableEvent event) {
  if ("AuthMe".equals(event.getPlugin().getName())) {
    authMeHook.disableHook(); // set the NewAPI field to null
  }
}

@EventHandler 
public void onDisable(PluginEnableEvent event) {
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
  listens to AuthMe events. When a player wants to log in, there is a 10% chance that the login will request will not
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
