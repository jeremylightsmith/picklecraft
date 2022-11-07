Pickle Mod
----------

Stuff that we make w/ our short friends

Setup Process:
==============================

1. Build the project `./gradlew build`

1. You're left with a choice.

    1. Open build.gradle in IDEA
    1. Run the following command: `./gradlew genIntellijRuns`
    1. Refresh the Gradle Project in IDEA if required.

1. To run the client, picklecraft [runClient] in IDEA

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Protobuf
============================

```
$ protoc --java_out=${OUTPUT_DIR} path/to/your/proto/file
```

Additional Resources: 
=========================
Community Documentation: http://mcforge.readthedocs.io/en/latest/gettingstarted/  
LexManos' Install Video: https://www.youtube.com/watch?v=8VEdtQLuLO0  
Forge Forum: https://forums.minecraftforge.net/  
Forge Discord: https://discord.gg/UvedJ9m  