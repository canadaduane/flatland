# Flatland



https://github.com/user-attachments/assets/ca932a9a-b84e-4f81-941e-b7414197f127


<a href="https://vimeo.com/video/42809224">On Vimeo</a>

Edwin Abbott wrote Flatland: A Romance of Many Dimensions in 1884. It was satire, but also explored what it would be like to live in a 2D world--how would flat people experience a creature entering from a 3rd dimension, for example?

At the same time that I was reading Flatland, I was also taking an introductory class to the Special Theory of Relativity. I was very curious about what it would be like to experience Lorentz Contraction--the phenomenon where the world (or the person traveling) might experience a shortened length in one direction (relative to the speed of travel).

A project from my undergrad in CS at BYU, circa 2003.

## Build & Run

This project depends on an early version of the Java SDK. I've tested Java 8 and 11. Java 8 pre-261 has the ability to create an applet (which this project uses), but I've also converted it to a non-applet form so that it can run as a regular app.

```
cd src
/usr/lib/java/jdk-11.0.24/bin/javac -d ../dist *.java

/usr/lib/java/jdk-11.0.24/bin/java FlatlandApplet
```

See `build.sh` and `run.sh`.
