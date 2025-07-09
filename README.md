# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram for Web API
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADE2EhoANYwAEoo9kiqFnJIEGiBAO4AFkhgYoiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPQ+BlAAOmgA3gBEHZRowAC2KP1l-TD9ADQzuOop0ByT03MzKKPASAhrMwC+mMKlMIWs7FyUFQNDUCPj+xv9i6rLUKtTM-P9Wzt7X36RzYnG4sHOJ1EFSg0ViWSgAAoojE4pQogBHHxqMAASmOJVEZyKsnkShU6gq9hQYAAqp0EXcHig8STFMo1KoiUYdGUAGIhDgwOmUVkwHSWGCMsZiHQw4DhYWwFKZNKSzpMmDABByjgSlAADzhGlZZI5ZwhBJUFUVrPxIhU5qKJ2uMAU2pQwF1DXl6AAovqVNgCEk7ac8uccuYKgAWJwAZh6A3G6mAVMmM19UG85TVw2lmvdnol8lC6CBZk4mBN7PUjuK9pQFTQPgQCFDhPO1fJqjKIDlWUVDM6rJZ2lNtfOxjKCg4gpt2nbDs7Y5rPb7HqyCh8YDSCOA27SI6rK+7XKnM8FW53tshS-OIKuOeRcLRahbWAfYLrzpzt3V0umCp1h+fcdwaCBSzQQCZkOUNKDrSMMAqAAmJwnETQZ-0eGAgO+GZQLScDIOg9YjnQDhTC8Xx-ACaB2CpGAABkIBiZIAnSTJsmQcwuR-CoanqZo2gMdREjQRMZilbDcOeV53lWHCYOmI4fy5T8XV6SSsImRTgIWfQ3hWEiYOBS4vwtBsKgQFiQgRZjWIxLE4jxW9DGXUlV0palByk5ljw809Jx5GB+U4IVh20MUJV8zBZQ9BVOiMCA1DQAByZgDThfy2UCiyqChGBm1bRc3Lyl0aRGA9oCQAAvFAOH9QNgzQODwQjbikJgWMAEYJP6ZNVFTHSgMzbMKh8Sqd2quqFLIysuzNcMnUtRtwpFBc4vlGBqgM+SYBCEBoBhcASu-EpysmtJpvqxqUCDMTWoQjqwBjJxer6GYBqGkjRugcbLuu2aKwo1yuQWil1zkFAr13Aij3BzkgunWdXQPW0EbOyzUevBdQaWi5QRdeyQiiGwkgMTB1La5bqGuTTMLzaSlJAg8iLLXSTMe-HEJemBUPQj6GfuACOeeAi2ag0WYDmijPG8PxAmQew0jALwUHQJiWN8Zh2IyLJMB53jzpzSppF9RjfQaX0WlaETVDEnpxYg9AuaKKmKidyDKbM+C8oK6z7G1hEYaPPHiRPDkKgAMwFE0D2DtHtFHALFqKKcQoFbHD0i8Vc2F8ZYrlcIADkIGYAOqUFEJsvHRG-atQqWzbPGypzCqCMB277pDVTuee173vpr601036cwmjuoFq+rlOBmvV0x-KG5D3GVrBiOKSMFBuE3eOV-kZOctT7kKiiUYIBoLObzX-H3c1wPt1J7BybEKnF406Wufa3Jef5iSZcovLGi8hkiMThDAAA4tKTkutOIG2ekbWmJsIGWxtvYaUjtWbOxar3N2Pscyexdm-euq1kBxCgcmBOOMD6nXckfTeVIwAwwllQ7ONCMZIwzmFZh2CYBR2gHnQu8UYCl3LixSu+0WocJIU2JutDW7WgBlPGaXdmpfyKDzAefVh7DQzFmP6kolHTyBuReeuUaZL1Wjwr2YcZAbx7DAMhYAKFqFYfDexZ5goQOpAWBAMB0HJnkXgwmOYwHkOgU-F+3sQnv1-DMAJah0yVAGAkgAktIdM3VkJxmjD8DiA5tLpj0v0HQCBQChEVEyIpeF+gJOLgBGSBwYBNBUsbJ6P8UJoT6gk1QSSUnSnSZk7JuSZj5JQJUhpzMZilPKRMx4Mkfh1MmaRZpAC5bUUVkgZWYBSlYngP2QwLjUh6y4j-RBpR+J1EaGgjBl0JaJiWeMVpSC1L4I9lgyCDzpT1KedEx85yCpoBQCkFxCJHkoHmIQtAeJNolzLjAdAvhlaSP4VAbYaiW4WJdAAIU9I5bEqiHq92-lGLqXTBY6J+vonMehBQwkxNiWepjbEIy8kwj56A3FJzMcfdOoVLwcuSKiwRsKhRGJmjyicMjG7FUxfWC5YrJ7GMJT3NpfcOlksHp9Dk31R7Uv+kqmaTL5oePxq5Co1iXYsvsRUSGWRQUuPcSnCcadgoXkgdKa+DY6x3zgAclxkS0AU2IQomA9M0kZIqFknJn9iUaP7nzcl4aBmRpgNG6Mn9THrIVgESw29rIpBgAAKQgCED14xjlwMNqGmoNIhKtASZgsC2DEzYDKXmqAcAIDWSgPMCNzywzBMfO85tny+htuAB2rtPa+0ptMjE6VAArUtaBQUTqnd26As7xjpPmPvYALkb7h2dQ4xhlq0BcvYaa11FR+VZwlnwgRdwhFbXblVZR9VJV1wsYCuRcq+KKvfcqgMd0MVqpJZ1Hq2idUjxGvqwxhqZ6ZpNSexeBVz20OPfQ091JQURsPrXTxt7M5HNzhGl9cL-H5mVDuKjO7pAIsNHEY0prpVFWbjfUNuKOD4riCqnB4H40atjALIeMHdH9DHhUWlMB6VOTAMaii0if0N0davb10qQBpBQCAUICgqD-F0LsTIlgERqfkPMddlBp3QBhUXV0hZdQwBLFazjWKcxuh1JYb0kF+PqPgAm2MCYKXiaKVJgsXnnM+klgA61J7xrYC0Pa6UCIrOds3b2vOTICML04TSRLUNnPafLWIOVd8S0hADWoZ+QbX74NiTcWNgmAsar-h9NZVEc1KxVl4SdAWPSwGANgNthAEhJErfrat7n+JmwtlbG2xhXYE2HWGz+xCVOrRANwPACJD0aaw7XW122oDznkLtr9RGZDb2pIYLUfi7hGHU5Y9e8WYBbYG+Z4A52OE3quzvW7rY6OGCS8ATDdjXvvbwPu7716T5-Zu74zUB5HvyCCctsEFREADcDcG+rZrjaNYHb7CDv8k3IYokAA